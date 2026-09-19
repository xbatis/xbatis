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
import cn.xbatis.core.db.reflect.FieldInfo;
import cn.xbatis.core.mybatis.executor.*;
import cn.xbatis.core.mybatis.executor.resultset.MybatisDefaultResultSetHandler;
import cn.xbatis.core.mybatis.executor.statement.MybatisRoutingStatementHandler;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.PreparedParameterContext;
import cn.xbatis.core.mybatis.mapping.ResultMapUtils;
import cn.xbatis.core.mybatis.typeHandler.EnumTypeHandler;
import cn.xbatis.core.mybatis.typeHandler.MybatisTypeHandlerUtil;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MybatisConfiguration extends Configuration {

    private static boolean PRINTED_BANNER = false;

    /**
     * 是否打印 banner
     */
    private boolean banner = true;

    /**
     * 是否初始化
     */
    private boolean initialized;

    protected final XbatisMapperRegistry mapperRegistry;
    /**
     * 是否异步初始化
     */
    private boolean asyncInit = false;

    public MybatisConfiguration() {
        super();
        mapperRegistry = new XbatisMapperRegistry(this);
        this.initSetting();
    }

    public MybatisConfiguration(Environment environment) {
        super(environment);
        mapperRegistry = new XbatisMapperRegistry(this);
        this.initSetting();
    }

    private void initSetting() {
        this.setDefaultScriptingLanguage(MybatisLanguageDriver.class);
        this.setDefaultEnumTypeHandler(EnumTypeHandler.class);
    }

    public void onInit() {
        if (initialized) {
            return;
        }
        initialized = true;
        XbatisGlobalConfig.onInit();
        this.printBanner();

        //添加基础 单Mapper
        Class<?> basicMapper = XbatisGlobalConfig.getSingleMapperClass();
        if (!this.hasMapper(basicMapper)) {
            this.addBasicMapper(basicMapper);
        }
    }

    private void printBanner() {
        if (!banner) {
            return;
        }
        if (PRINTED_BANNER) {
            return;
        }
        PRINTED_BANNER = true;
        try (BufferedReader reader = new BufferedReader(Resources.getResourceAsReader("xbatis.banner"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
        MetaObject metaObject = this.newMetaObject(resultMapResolver);
        String extend = (String) metaObject.getValue("extend");
        if ("$xbatis".equals(extend)) {

            MapperBuilderAssistant assistant = (MapperBuilderAssistant) metaObject.getValue("assistant");
            String id = (String) metaObject.getValue("id");
            Class<?> type = (Class) metaObject.getValue("type");
            Discriminator discriminator = (Discriminator) metaObject.getValue("discriminator");
            List<ResultMapping> resultMappings = (List<ResultMapping>) metaObject.getValue("resultMappings");
            Boolean autoMapping = (Boolean) metaObject.getValue("autoMapping");

            extend = XbatisIdUtil.convertResultMapIdPath(type.getName());
            //预加载
            ResultMapUtils.addAndGetResultMap(this, type);
            try {
                resultMappings.addAll(this.getResultMap(extend).getResultMappings());
                extend = null;
                Constructor<ResultMapResolver> constructor = ResultMapResolver.class.getConstructor(MapperBuilderAssistant.class, String.class, Class.class, String.class,
                        Discriminator.class, List.class, Boolean.class);
                ResultMapResolver newResolver = constructor.newInstance(assistant, id, type, extend, discriminator, resultMappings, autoMapping);
                resultMapResolver = newResolver;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        super.addIncompleteResultMap(resultMapResolver);
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        mappedStatement = DynamicsMappedStatement.wrapMappedStatement(mappedStatement, parameterObject, boundSql);
        StatementHandler statementHandler = new MybatisRoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        return (StatementHandler) this.interceptorChain.pluginAll(statementHandler);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object parameterObject, BoundSql boundSql) {
        if (ms.getId().endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            return super.newParameterHandler(ms, parameterObject, boundSql);
        }
        if (parameterObject instanceof PreparedParameterContext) {
            return (ParameterHandler) interceptorChain.pluginAll(new PreparedParameterHandler(this, (PreparedParameterContext) parameterObject));
        }
        if (parameterObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) parameterObject;
            if (map.containsKey("xbatis")) {
                //兼容 其他框架修改参数的情况 例如PageHelper
                return (ParameterHandler) interceptorChain.pluginAll(new OtherFrameworkPreparedParameterHandler(this, boundSql, (Map) parameterObject));
            }
        }
        return super.newParameterHandler(ms, parameterObject, boundSql);
    }

    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new MybatisDefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        return (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        super.addMappedStatement(MappedStatementUtil.wrap(ms));
    }

    private <T> void addBasicMapper(Class<T> type) {
        //BasicMapper 比较重要
        mapperRegistry.addMapper(type, asyncInit);
    }

    protected void clearBasicMapperResultMap(Class<?> type) {
        Iterator<Map.Entry<String, ResultMap>> it = resultMaps.entrySet().iterator();
        String removeIdPrefix1 = "$";
        String removeIdPrefix2 = BasicMapper.class.getName() + ".$";
        String removeIdPrefix3 = type.getName() + ".$";
        boolean checkPrefix3 = !removeIdPrefix2.equals(removeIdPrefix3);
        while (it.hasNext()) {
            Map.Entry<String, ResultMap> entry = it.next();
            Object value = entry.getValue();
            if (!(value instanceof ResultMap)) {
                continue;
            }
            ResultMap resultMap = (ResultMap) value;
            if (resultMap.getType() != Object.class && resultMap.getType() != Integer.class && resultMap.getType() != Map.class) {
                continue;
            }
            if (resultMap.getId().startsWith(removeIdPrefix1) || resultMap.getId().startsWith(removeIdPrefix2)) {
                it.remove();
                continue;
            }
            if (checkPrefix3 && resultMap.getId().startsWith(removeIdPrefix3)) {
                it.remove();
            }
        }
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        //设置新的basicMapper
        if (XbatisGlobalConfig.getSingleMapperClass() == BasicMapper.class && type != BasicMapper.class && BasicMapper.class.isAssignableFrom(type)) {
            XbatisGlobalConfig.setSingleMapperClass((Class<? extends BasicMapper>) type);
        }
        if (!initialized) {
            this.onInit();
        }

        if (XbatisGlobalConfig.getSingleMapperClass() == type) {
            //已在 onInit 中初始化
            return;
        }

        if (XbatisGlobalConfig.getSingleMapperClass().isAssignableFrom(type)) {
            if (!this.hasMapper(type)) {
                this.addBasicMapper(type);
            }
            return;
        }
        mapperRegistry.addMapper(type, this.asyncInit);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (asyncInit) {
            mapperRegistry.checkAndWait(id, validateIncompleteStatements);
        }
        return super.getMappedStatement(id, validateIncompleteStatements);
    }

    public ResultMapping buildResultMapping(boolean id, FieldInfo fieldInfo, String columnName, JdbcType jdbcType, Class<? extends TypeHandler<?>> typeHandlerClass) {
        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(this, fieldInfo.getField().getName())
                .column(columnName)
                .javaType(fieldInfo.getTypeClass())
                .jdbcType(jdbcType)
                .typeHandler(MybatisTypeHandlerUtil.getTypeHandler(this, fieldInfo, typeHandlerClass, jdbcType));
        if (id) {
            resultMappingBuilder.flags(Collections.singletonList(ResultFlag.ID));
        }
        return resultMappingBuilder.build();
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? this.defaultExecutorType : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new MybatisBatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new MybatisReuseExecutor(this, transaction);
        } else {
            executor = new MybatisSimpleExecutor(this, transaction);
        }

        if (this.cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        return (Executor) this.interceptorChain.pluginAll(executor);
    }

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }

    public boolean isAsyncInit() {
        return asyncInit;
    }

    public void setAsyncInit(boolean asyncInit) {
        this.asyncInit = asyncInit;
    }
}



