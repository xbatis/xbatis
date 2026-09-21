/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */

package cn.xbatis.core.mybatis.configuration;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.exception.NotTableClassException;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.mybatis.mapping.ResultMapUtils;
import cn.xbatis.core.util.GenericUtil;
import cn.xbatis.db.annotations.Table;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;


public class XbatisMapperRegistry extends MapperRegistry {

    protected final Map<Class<?>, MapperProxyFactory<?>> knownMappers;
    protected final Set<String> loadingMappers = ConcurrentHashMap.newKeySet();
    private final MybatisConfiguration config;
    private ThreadPoolExecutor loadExecutor;

    public XbatisMapperRegistry(MybatisConfiguration config) {
        super(config);
        this.config = config;
        this.knownMappers = getKnownMappers(this);

        if (this.config.isAsyncInit()) {
            loadExecutor = new ThreadPoolExecutor(
                    0,                  // corePoolSize = 0，核心线程也不常驻
                    3,                             // maximumPoolSize
                    60, TimeUnit.SECONDS,          // 空闲 60 秒后回收
                    new LinkedBlockingQueue<>(),    // 任务队列
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            loadExecutor.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                loadExecutor.shutdownNow();
            }));
        }
    }

    private static Map<Class<?>, MapperProxyFactory<?>> getKnownMappers(MapperRegistry mapperRegistry) {
        try {
            Field knownMappersField = MapperRegistry.class.getDeclaredField("knownMappers");
            knownMappersField.setAccessible(true);
            return (Map<Class<?>, MapperProxyFactory<?>>) knownMappersField.get(mapperRegistry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void parseMapper(Class<T> type) {
        boolean loadCompleted = false;
        try {
            //现在加载实体的ResultMap
            if (MybatisMapper.class.isAssignableFrom(type)) {
                List<Class<?>> list = GenericUtil.getGenericInterfaceClass(type);
                Optional<Class<?>> entityOptional = list.stream().filter(item -> item.isAnnotationPresent(Table.class)).findFirst();
                if (!entityOptional.isPresent()) {
                    if (list.size() != 1) {
                        throw new RuntimeException(type + " did not add a generic");
                    } else {
                        throw new NotTableClassException(list.get(0));
                    }
                }
                ResultMapUtils.addAndGetResultMap(config, entityOptional.get());
            }

            MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
            parser.parse();

            if (BasicMapper.class.isAssignableFrom(type)) {
                // 移除一开始注册是单mapper
                if (type != BasicMapper.class) {
                    XbatisGlobalConfig.setSingleMapperClass((Class<? extends BasicMapper>) type);
                    this.knownMappers.remove(BasicMapper.class);
                }
                // 清空多余的resultMap
                this.config.clearBasicMapperResultMap(type);
            }

            loadCompleted = true;
        } finally {
            if (!loadCompleted) {
                knownMappers.remove(type);
            }
        }
    }

    public <T> MapperProxyFactory<T> getMapperProxyFactory(Class<T> type) {
        if (MybatisMapper.class.isAssignableFrom(type)) {
            return new MybatisMapperProxyFactory<>(type);
        } else if (BasicMapper.class.isAssignableFrom(type)) {
            return new BasicMapperProxyFactory(type);
        }
        return new MapperProxyFactory<>(type);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        this.addMapper(type, false);
    }

    /**
     * 支持异步 解析mapper方法
     *
     * @param type  Mapper class
     * @param async 是否异步
     * @param <T>   Mapper 类型
     */
    public <T> void addMapper(Class<T> type, boolean async) {
        if (!type.isInterface()) {
            return;
        }
        if (this.hasMapper(type)) {
            throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
        }

        if (!async) {
            knownMappers.put(type, getMapperProxyFactory(type));
            parseMapper(type);
            return;
        }

        if (!loadingMappers.add(type.getName())) {
            return;
        }
        knownMappers.put(type, getMapperProxyFactory(type));
        if (async) {
            Runnable runnable = () -> {
                try {
                    parseMapper(type);
                    loadingMappers.remove(type.getName());
                } catch (Exception e) {
                    Log log = LogFactory.getLog(XbatisMapperRegistry.class);
                    if (log instanceof NoLoggingImpl) {
                        System.err.println("解析Mapper：" + type + "异常");
                        e.printStackTrace();
                    } else {
                        log.error("解析Mapper：" + type + "异常", e);
                    }
                    System.exit(1);
                }
            };
            if (loadExecutor == null) {
                CompletableFuture.runAsync(runnable);
            } else {
                CompletableFuture.runAsync(runnable, loadExecutor);
            }
        }
    }

    /**
     * 如果加载中2秒还未初始化完 就不管了
     *
     * @param mappedStatementId
     */
    public void checkAndWait(String mappedStatementId, boolean validateIncompleteStatements) {
        if (loadingMappers.isEmpty()) {
            return;
        }
        if (config.hasStatement(mappedStatementId, validateIncompleteStatements)) {
            return;
        }
        checkAndWait(mappedStatementId.substring(0, mappedStatementId.lastIndexOf(".")), 1);
    }

    private void checkAndWait(String clazz, int checkTimes) {
        if (loadingMappers.isEmpty()) {
            return;
        }
        if (!loadingMappers.contains(clazz)) {
            return;
        }
        if (checkTimes > 200) {
            return;
        }
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
        checkAndWait(clazz, ++checkTimes);
    }
}
