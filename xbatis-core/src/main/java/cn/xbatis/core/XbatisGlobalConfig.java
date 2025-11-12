/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn) xbatis.
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

package cn.xbatis.core;


import cn.xbatis.core.logicDelete.LogicDeleteSwitch;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.intercept.MethodInterceptor;
import cn.xbatis.core.sql.SQLBuilder;
import cn.xbatis.core.sql.XbatisSQLBuilder;
import cn.xbatis.core.sql.executor.BaseUpdate;
import cn.xbatis.core.sql.listener.ForeignKeySQLListener;
import cn.xbatis.core.sql.listener.LogicDeleteSQLListener;
import cn.xbatis.core.sql.listener.OptimisticLockSQLListener;
import cn.xbatis.core.sql.listener.TenantSQLListener;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.DatabaseCaseRule;
import cn.xbatis.listener.OnInsertListener;
import cn.xbatis.listener.OnUpdateListener;
import db.sql.api.DbType;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.SQLImplGlobalConfig;
import db.sql.api.impl.paging.IPagingProcessor;
import db.sql.api.impl.paging.PagingProcessorFactory;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 全局配置
 */
public final class XbatisGlobalConfig {
    private static final Object NULL = new Object();
    private static final Map<String, BiFunction<Class<?>, Class<?>, ?>> DYNAMIC_VALUE_MANAGER = new ConcurrentHashMap();
    private static final List<SQLListener> SQL_LISTENERS = new ArrayList<>();
    private static final List<MethodInterceptor> MAPPER_METHOD_INTERCEPTORS = new ArrayList<>();

    private static volatile Object TABLE_UNDERLINE = NULL;
    private static volatile Object COLUMN_UNDERLINE = NULL;
    private static volatile Object DEFAULT_BATCH_SIZE = NULL;
    private static volatile Object SQL_BUILDER = NULL;
    private static volatile Object LOGIC_DELETE_SWITCH = NULL;
    private static volatile Object LOGIC_DELETE_INTERCEPTOR = NULL;
    private static volatile Object SINGLE_MAPPER_CLASS = NULL;
    private static volatile Object GLOBAL_ON_INSERT_LISTENER = NULL;
    private static volatile Object GLOBAL_ON_UPDATE_LISTENER = NULL;
    private static volatile Object INTERCEPT_OFFICIAL_MAPPER_METHOD = NULL;
    private static volatile Object FETCH_IN_BATCH_SIZE = NULL;


    static {
        SQL_LISTENERS.add(new ForeignKeySQLListener());
        SQL_LISTENERS.add(new TenantSQLListener());
        SQL_LISTENERS.add(new LogicDeleteSQLListener());
        SQL_LISTENERS.add(new OptimisticLockSQLListener());
    }

    private XbatisGlobalConfig() {

    }

    /**
     * 初始化时触发
     */
    public static void onInit() {
        setDynamicValue("{BLANK}", (source, type) -> {
            if (type == String.class) {
                return StringPool.EMPTY;
            } else if (type.isArray()) {
                return Array.newInstance(type, 0);
            } else if (List.class.isAssignableFrom(type)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(type)) {
                return new HashSet<>();
            } else if (Map.class.isAssignableFrom(type)) {
                return new HashMap<>();
            }
            throw new RuntimeException("Inconsistent types：" + type);
        });

        setDynamicValue("{NOW}", (source, type) -> {
            if (type == LocalDateTime.class) {
                return LocalDateTime.now();
            } else if (type == LocalDate.class) {
                return LocalDate.now();
            } else if (type == LocalTime.class) {
                return LocalTime.now();
            } else if (type == Date.class) {
                return new Date();
            } else if (type == Long.class) {
                return System.currentTimeMillis();
            } else if (type == Integer.class) {
                return (int) (System.currentTimeMillis() / 1000);
            } else if (type == String.class) {
                return LocalDate.now().toString();
            }
            throw new RuntimeException("Inconsistent types：" + type);
        });

        setDynamicValue("{TODAY}", (source, type) -> {
            if (type == LocalDate.class) {
                return LocalDate.now();
            } else if (type == String.class) {
                return LocalDate.now().toString();
            } else if (type == LocalDate[].class) {
                return new LocalDate[]{LocalDate.now(), LocalDate.now()};
            } else if (type == LocalDateTime[].class) {
                return new LocalDateTime[]{LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1)};
            } else if (type == Long[].class) {
                long time = LocalDate.now().toEpochDay();
                return new Long[]{time, time + TimeUnit.DAYS.toMillis(1) - 1};
            } else if (type == String[].class) {
                String today = LocalDate.now().toString();
                return new String[]{today, today + "23:59:59"};
            } else if (type == List.class) {
                List<LocalDateTime> list = new ArrayList<>();
                list.add(LocalDate.now().atStartOfDay());
                list.add(LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1));
                return list;
            }
            throw new RuntimeException("Inconsistent types：" + type);
        });
    }

    /**
     * 数据库命名规则 默认 不处理
     *
     * @return 命名规则
     */
    public static DatabaseCaseRule getDatabaseCaseRule() {
        return SQLImplGlobalConfig.getDatabaseCaseRule();
    }

    /**
     * 设置数据库命名规则 默认 不处理
     *
     * @return 是否成功
     */
    public static boolean setDatabaseCaseRule(DatabaseCaseRule databaseCaseRule) {
        return SQLImplGlobalConfig.setDatabaseCaseRule(databaseCaseRule);
    }


    /**
     * 数据库命名规则 默认 不处理
     *
     * @return 命名规则
     */
    public static DatabaseCaseRule getDatabaseCaseRule(DbType dbType) {
        return SQLImplGlobalConfig.getDatabaseCaseRule(dbType);
    }

    /**
     * 设置数据库命名规则 默认 不处理
     *
     * @return 是否成功
     */
    public static void setDatabaseCaseRule(DbType dbType, DatabaseCaseRule databaseCaseRule) {
        SQLImplGlobalConfig.setDatabaseCaseRule(dbType, databaseCaseRule);
    }

    /**
     * 数据库表是否下划线规则 默认 true
     *
     * @return 是否是下划线规则
     */
    public static boolean isTableUnderline() {
        if (TABLE_UNDERLINE == NULL) {
            TABLE_UNDERLINE = true;
        }
        return (boolean) TABLE_UNDERLINE;
    }


    /**
     * 设置数据库表是否下划线规则（必须在项目启动时设置，否则可能永远不会成功）
     *
     * @param bool 是否是下划线规则
     * @return 是否成功
     */
    public static boolean setTableUnderline(boolean bool) {
        if (TABLE_UNDERLINE == NULL) {
            TABLE_UNDERLINE = bool;
            return true;
        }
        return false;
    }


    /**
     * 数据库列是否下划线规则 默认 true
     *
     * @return 列是否是下划线命名规则
     */
    public static boolean isColumnUnderline() {
        if (COLUMN_UNDERLINE == NULL) {
            COLUMN_UNDERLINE = true;
        }
        return (boolean) COLUMN_UNDERLINE;
    }

    /**
     * 设置数据库列是否下划线规则（必须在项目启动时设置，否则可能永远不会成功）
     *
     * @param bool 列是否下划线命名规则
     * @return 是否成功
     */
    public static boolean setColumnUnderline(boolean bool) {
        if (COLUMN_UNDERLINE == NULL) {
            COLUMN_UNDERLINE = bool;
            return true;
        }
        return false;
    }


    /**
     * 默认1000
     *
     * @return 批量提交的默认size
     */
    public static int getDefaultBatchSize() {
        if (DEFAULT_BATCH_SIZE == NULL) {
            DEFAULT_BATCH_SIZE = 1000;
        }
        return (int) DEFAULT_BATCH_SIZE;
    }

    /**
     * 设置批量的size
     *
     * @param defaultBatchSize
     */
    public static boolean setDefaultBatchSize(int defaultBatchSize) {
        if (defaultBatchSize < 1) {
            throw new RuntimeException("defaultBatchSize can't less 1");
        }
        if (DEFAULT_BATCH_SIZE == NULL) {
            DEFAULT_BATCH_SIZE = defaultBatchSize;
            return true;
        }
        return false;
    }

    /**
     * 获取xbatis SQL BUILDER
     *
     * @return 返回QuerySQLBuilder
     */
    public static SQLBuilder getSQLBuilder() {
        if (SQL_BUILDER == NULL) {
            SQL_BUILDER = new XbatisSQLBuilder();
        }
        return (SQLBuilder) SQL_BUILDER;
    }

    /**
     * 设置xbatis SQL BUILDER
     *
     * @param sqlBuilder
     * @return 是否成功
     */
    public static boolean setSQLBuilder(SQLBuilder sqlBuilder) {
        if (SQL_BUILDER == NULL) {
            SQL_BUILDER = sqlBuilder;
            return true;
        }
        return false;
    }

    /**
     * 获取逻辑删除开关，默认开启
     *
     * @return 逻辑开关的是否打开
     */
    public static boolean isLogicDeleteSwitchOpen() {
        if (LOGIC_DELETE_SWITCH == NULL) {
            LOGIC_DELETE_SWITCH = true;
        }
        Boolean state = LogicDeleteSwitch.getState();
        if (state != null) {
            //局部开关 优先
            return state;
        }
        return (boolean) LOGIC_DELETE_SWITCH;
    }

    /**
     * 设置逻辑删除开关状态（必须在项目启动时设置，否则可能永远true）
     *
     * @param bool 开关状态
     * @return 是否成功
     */
    public static boolean setLogicDeleteSwitch(boolean bool) {
        if (LOGIC_DELETE_SWITCH == NULL) {
            LOGIC_DELETE_SWITCH = bool;
            return true;
        }
        return false;
    }

    /**
     * 获取逻辑删除 update 拦截器
     *
     * @return
     */
    public static BiConsumer<Class<?>, BaseUpdate<?>> getLogicDeleteInterceptor() {
        if (LOGIC_DELETE_INTERCEPTOR == NULL) {
            LOGIC_DELETE_INTERCEPTOR = null;
        }
        return (BiConsumer<Class<?>, BaseUpdate<?>>) LOGIC_DELETE_INTERCEPTOR;
    }

    /**
     * 设置逻辑删除 update 拦截器
     *
     * @param interceptor
     * @return 是否成功
     */
    public static boolean setLogicDeleteInterceptor(BiConsumer<Class<?>, BaseUpdate<?>> interceptor) {
        if (LOGIC_DELETE_INTERCEPTOR == NULL) {
            LOGIC_DELETE_INTERCEPTOR = interceptor;
            return true;
        }
        return false;
    }

    /**
     * 判断key是否是动态值格式，例如 是不是"{xxx}"
     *
     * @param key
     * @return
     */
    public static boolean isDynamicValueKeyFormat(String key) {
        return key.startsWith("{") && key.endsWith("}");
    }

    /**
     * 设置动态值的函数的方法
     *
     * @param key 动态值 需要符合 "{xxx}"的格式
     * @param f   返回该key的动态值的函数
     */
    public static void setDynamicValue(String key, BiFunction<Class<?>, Class<?>, Object> f) {
        if (!isDynamicValueKeyFormat(key)) {
            key = "{" + key + "}";
        }
        DYNAMIC_VALUE_MANAGER.computeIfAbsent(key, mapKey -> f);
    }

    public static void checkDynamicValueKey(String key) {
        if (!isDynamicValueKeyFormat(key)) {
            throw new RuntimeException("key must start with '{' and end with '}'");
        }
    }

    /**
     * 请用 setDynamicValue 代替
     * 设置默认值的函数的方法
     *
     * @param key 默认值 需要符合 "{xxx}"的格式
     * @param f   返回该key的默认值的函数
     */
    @Deprecated
    public static void setDefaultValue(String key, BiFunction<Class<?>, Class<?>, Object> f) {
        setDynamicValue(key, f);
    }

    /**
     * 获取默认值
     *
     * @param clazz 字段所在的class
     * @param type  默认值的类型
     * @param key   默认值的key，key必须以{}包裹，例如:{NOW}
     * @param <T>   类型clazz的泛型
     * @return 返回指定类型clazz key的默认值
     */
    public static <T> T getDefaultValue(Class<?> clazz, Class<T> type, String key) {
        if (!isDynamicValueKeyFormat(key)) {
            return TypeConvertUtil.convert(key, type);
        }
        return getDynamicValue(clazz, type, key);
    }

    /**
     * 获取默认值
     *
     * @param clazz   字段所在的class
     * @param type    默认值的类型
     * @param key     默认值的key，key必须以{}包裹，例如:{NOW}
     * @param context 上下文 用于缓存动态值（非动态的不缓存）
     * @param <T>     类型clazz的泛型
     * @return 返回指定类型clazz key的默认值
     */
    public static <T> T getDefaultValue(Class<?> clazz, Class<T> type, String key, Map<String, Object> context) {
        if (!isDynamicValueKeyFormat(key)) {
            return TypeConvertUtil.convert(key, type);
        }
        return getDynamicValue(clazz, type, key, context);
    }

    /**
     * 获取指定key的动态值
     *
     * @param clazz 字段所在的class
     * @param type  动态值的类型
     * @param key   动态值的key
     * @param <T>   类型clazz的泛型
     * @return 返回指定类型clazz key的动态值
     */
    public static <T> T getDynamicValue(Class<?> clazz, Class<T> type, String key) {
        if (!isDynamicValueKeyFormat(key)) {
            key = "{" + key + "}";
        }

        BiFunction<Class<?>, Class<?>, T> f = (BiFunction<Class<?>, Class<?>, T>) DYNAMIC_VALUE_MANAGER.get(key);
        if (f == null) {
            throw new RuntimeException("default value key:  " + key + " not set");
        }
        return f.apply(clazz, type);
    }

    /**
     * 获取指定key的动态值
     *
     * @param clazz 字段所在的class
     * @param type  动态值的类型
     * @param key   动态值的key
     * @param <T>   类型clazz的泛型
     * @return 返回指定类型clazz key的动态值
     */
    public static <T> T getDynamicValue(Class<?> clazz, Class<T> type, String key, Map<String, Object> context) {
        if (context == null) {
            return getDynamicValue(clazz, type, key);
        }
        if (!isDynamicValueKeyFormat(key)) {
            key = "{" + key + "}";
        }

        Object obj = context.get(key);
        if (obj == null) {
            obj = getDynamicValue(clazz, type, key);
            if (obj == null) {
                obj = NULL;
            }
            context.put(key, obj);
        }
        if (obj == null || obj == NULL) {
            return null;
        }
        return (T) obj;
    }

    /**
     * 获取单Mapper的class 用于BasicMapper.withSqlSession方法 statement 拼接
     *
     * @return 单Mapper的class
     */
    public static Class<? extends BasicMapper> getSingleMapperClass() {
        if (SINGLE_MAPPER_CLASS == NULL) {
            SINGLE_MAPPER_CLASS = BasicMapper.class;
        }
        return (Class) SINGLE_MAPPER_CLASS;
    }

    /**
     * 设置单Mapper的class 用于BasicMapper.withSqlSession方法 statement 拼接
     *
     * @param singleMapperClass
     * @return 是否成功
     */
    public static boolean setSingleMapperClass(Class<? extends BasicMapper> singleMapperClass) {
        if (SINGLE_MAPPER_CLASS == NULL) {
            SINGLE_MAPPER_CLASS = singleMapperClass;
            return true;
        }
        return false;
    }

    /**
     * 添加SQLListener
     *
     * @param sqlListener
     */
    public static void addSQLListener(SQLListener sqlListener) {
        SQL_LISTENERS.add(sqlListener);
    }

    /**
     * 移除SQLListener
     *
     * @param type
     */
    public static <T extends SQLListener> void removeSQLListener(Class<T> type) {
        Iterator<SQLListener> iterator = SQL_LISTENERS.iterator();
        while (iterator.hasNext()) {
            if (type.isAssignableFrom(iterator.next().getClass())) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取所有的SQLListener
     *
     * @return
     */
    public static List<SQLListener> getSQLListeners() {
        return Collections.unmodifiableList(SQL_LISTENERS);
    }

    /**
     * 设置分页处理器
     *
     * @param dbType          数据库类型
     * @param pagingProcessor 分页处理器
     */
    public static void setPagingProcessor(DbType dbType, IPagingProcessor pagingProcessor) {
        PagingProcessorFactory.setProcessor(dbType, pagingProcessor);
    }

    /**
     * 获取分页处理器
     *
     * @param dbType 数据库类型
     * @return 分页处理器
     */
    public static IPagingProcessor getPagingProcessor(DbType dbType) {
        return PagingProcessorFactory.getProcessor(dbType);
    }

    /**
     * 添加MapperMethodInterceptor
     *
     * @param methodInterceptor
     */
    public static void addMapperMethodInterceptor(MethodInterceptor methodInterceptor) {
        MAPPER_METHOD_INTERCEPTORS.add(methodInterceptor);
    }

    /**
     * 移除MapperMethodInterceptor
     *
     * @param type
     */
    public static <T extends MethodInterceptor> void removeMapperMethodInterceptor(Class<T> type) {
        Iterator<MethodInterceptor> iterator = MAPPER_METHOD_INTERCEPTORS.iterator();
        while (iterator.hasNext()) {
            if (type.isAssignableFrom(iterator.next().getClass())) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取所有Mapper的方法拦截器
     *
     * @return
     */
    public static List<MethodInterceptor> getMapperMethodInterceptors() {
        return Collections.unmodifiableList(MAPPER_METHOD_INTERCEPTORS);
    }

    /**
     * 是否拦截官方的mapper方法
     *
     * @return
     */
    public static boolean isEnableInterceptOfficialMapperMethod() {
        if (INTERCEPT_OFFICIAL_MAPPER_METHOD == NULL) {
            INTERCEPT_OFFICIAL_MAPPER_METHOD = false;
        }
        return (boolean) INTERCEPT_OFFICIAL_MAPPER_METHOD;
    }

    /**
     * 开启拦截官方的mapper方法
     *
     * @return 是否成功
     */
    public static boolean enableInterceptOfficialMapperMethod() {
        if (INTERCEPT_OFFICIAL_MAPPER_METHOD == NULL) {
            INTERCEPT_OFFICIAL_MAPPER_METHOD = true;
            return true;
        }
        return false;
    }

    /**
     * 获取全局OnInsertListener
     *
     * @return
     */
    public static OnInsertListener getGlobalOnInsertListener() {
        if (GLOBAL_ON_INSERT_LISTENER == NULL) {
            GLOBAL_ON_INSERT_LISTENER = null;
        }
        return (OnInsertListener) GLOBAL_ON_INSERT_LISTENER;
    }

    /**
     * 设置全局OnInsertListener
     *
     * @param listener
     */
    public static void setGlobalOnInsertListener(OnInsertListener<?> listener) {
        if (GLOBAL_ON_INSERT_LISTENER == NULL) {
            GLOBAL_ON_INSERT_LISTENER = listener;
        }
    }

    /**
     * 获取全局OnUpdateListener
     *
     * @return
     */
    public static OnUpdateListener<?> getGlobalOnUpdateListener() {
        if (GLOBAL_ON_UPDATE_LISTENER == NULL) {
            GLOBAL_ON_UPDATE_LISTENER = null;
        }
        return (OnUpdateListener) GLOBAL_ON_UPDATE_LISTENER;
    }

    /**
     * 设置全局OnUpdateListener
     *
     * @param listener
     * @return 是否成功
     */
    public static boolean setGlobalOnUpdateListener(OnUpdateListener<?> listener) {
        if (GLOBAL_ON_UPDATE_LISTENER == NULL) {
            GLOBAL_ON_UPDATE_LISTENER = listener;
            return true;
        }
        return false;
    }

    /**
     * 获取@Fetch的IN批量size
     *
     * @return
     */
    public static int getFetchInBatchSize() {
        if (FETCH_IN_BATCH_SIZE == NULL) {
            FETCH_IN_BATCH_SIZE = 100;
        }
        return (Integer) FETCH_IN_BATCH_SIZE;
    }

    /**
     * 设置@Fetch的IN批量size
     *
     * @param fetchInBatchSize
     * @return 是否成功
     */
    public static boolean setFetchInBatchSize(int fetchInBatchSize) {
        if (FETCH_IN_BATCH_SIZE == NULL) {
            FETCH_IN_BATCH_SIZE = fetchInBatchSize;
            return true;
        }
        return false;
    }
}
