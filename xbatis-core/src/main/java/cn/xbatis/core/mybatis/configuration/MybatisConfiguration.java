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

package cn.xbatis.core.mybatis.configuration;


import cn.xbatis.core.XbatisConfig;
import cn.xbatis.core.db.reflect.FieldInfo;
import cn.xbatis.core.exception.NotTableClassException;
import cn.xbatis.core.mybatis.executor.*;
import cn.xbatis.core.mybatis.executor.resultset.MybatisDefaultResultSetHandler;
import cn.xbatis.core.mybatis.executor.statement.MybatisRoutingStatementHandler;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.mybatis.mapping.ResultMapUtils;
import cn.xbatis.core.mybatis.typeHandler.EnumTypeHandler;
import cn.xbatis.core.mybatis.typeHandler.MybatisTypeHandlerUtil;
import cn.xbatis.core.util.GenericUtil;
import cn.xbatis.db.annotations.Table;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.executor.*;
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
import java.util.*;


public class MybatisConfiguration extends Configuration {

    public MybatisConfiguration() {
        super();
        this.init();
    }

    public MybatisConfiguration(Environment environment) {
        super(environment);
        this.init();
    }

    private void init() {
        this.setDefaultScriptingLanguage(MybatisLanguageDriver.class);
        this.setDefaultEnumTypeHandler(EnumTypeHandler.class);
    }

    public void printBanner() {
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
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        mappedStatement = DynamicsMappedStatement.wrapMappedStatement(mappedStatement, parameterObject);
        StatementHandler statementHandler = new MybatisRoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        return (StatementHandler) this.interceptorChain.pluginAll(statementHandler);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object parameterObject, BoundSql boundSql) {
        if (parameterObject instanceof PreparedParameterContext && !ms.getId().endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            return (ParameterHandler) interceptorChain.pluginAll(new PreparedParameterHandler(this, (PreparedParameterContext) parameterObject));
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
        super.addMapper(type);
        //替换成自己的   MapperProxy 工厂
        if (this.mapperRegistry.hasMapper(type)) {
            MetaObject msMetaObject = this.newMetaObject(this.mapperRegistry);
            Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) msMetaObject.getValue("knownMappers");
            knownMappers.put(type, new BasicMapperProxyFactory(type));
        }
    }

    private void clearResultMap() {
        Iterator<Map.Entry<String, ResultMap>> it = resultMaps.entrySet().iterator();
        String removeIdPrefix1 = "$";
        String removeIdPrefix2 = BasicMapper.class.getName() + ".$";
        String removeIdPrefix3 = XbatisConfig.getSingleMapperClass().getName() + ".$";
        boolean checkPrefix3 = !removeIdPrefix2.equals(removeIdPrefix3);
        while (it.hasNext()) {
            Map.Entry<String, ResultMap> entry = it.next();
            ResultMap resultMap = entry.getValue();
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

        if (XbatisConfig.getSingleMapperClass() == BasicMapper.class) {
            //添加基础 BasicMapper
            if (!this.hasMapper(BasicMapper.class)) {
                this.addBasicMapper(BasicMapper.class);
                this.clearResultMap();
                if (type == BasicMapper.class) {
                    return;
                }
            }
        }

        if (BasicMapper.class.isAssignableFrom(type) && type != BasicMapper.class) {
            this.addBasicMapper(type);
            this.clearResultMap();
            return;
        } else if (MybatisMapper.class.isAssignableFrom(type)) {
            List<Class<?>> list = GenericUtil.getGenericInterfaceClass(type);
            Optional<Class<?>> entityOptional = list.stream().filter(item -> item.isAnnotationPresent(Table.class)).findFirst();
            if (!entityOptional.isPresent()) {
                if (list.size() != 1) {
                    throw new RuntimeException(type + " did not add a generic");
                } else {
                    throw new NotTableClassException(list.get(0));
                }
            }
            ResultMapUtils.getResultMap(this, entityOptional.get());
        }

        super.addMapper(type);

        if (MybatisMapper.class.isAssignableFrom(type)) {
            //替换成自己的   MapperProxy 工厂
            if (this.mapperRegistry.hasMapper(type)) {
                MetaObject msMetaObject = this.newMetaObject(this.mapperRegistry);
                Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) msMetaObject.getValue("knownMappers");
                knownMappers.put(type, new MybatisMapperProxyFactory(type));
            }
        }
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return super.getMapper(type, sqlSession);
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
            executor = new BatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new ReuseExecutor(this, transaction);
        } else {
            executor = new SimpleExecutor(this, transaction);
        }
        executor = new MybatisExecutor(executor);
        if (this.cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        return (Executor) this.interceptorChain.pluginAll(executor);
    }
}



