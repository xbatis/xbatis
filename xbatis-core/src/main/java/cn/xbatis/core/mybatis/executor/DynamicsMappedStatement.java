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

package cn.xbatis.core.mybatis.executor;

import cn.xbatis.core.mybatis.configuration.MybatisConfiguration;
import cn.xbatis.core.mybatis.configuration.MybatisMapperProxy;
import cn.xbatis.core.mybatis.mapper.context.*;
import cn.xbatis.core.mybatis.mapping.ResultMapWrapper;
import cn.xbatis.core.mybatis.provider.SQLCmdSqlSource;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import db.sql.api.SqlBuilderContext;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class DynamicsMappedStatement {

    public final static String MAPPED_STATEMENT_DB_KEY_NAME = "(dbType)";

    public static MappedStatement wrapMappedStatement(MappedStatement ms, Object parameterObject) {
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
            return createInsertMappedStatement(ms, parameterObject);
        } else if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return ms;
        } else if (parameterObject instanceof SelectPreparedContext) {
            SelectPreparedContext selectPreparedContext = (SelectPreparedContext) parameterObject;
            return createQueryMappedStatement(selectPreparedContext.getReturnType(), ms);
        } else if (parameterObject instanceof SQLCmdUpdateContext && ms.getSqlCommandType() == SqlCommandType.SELECT) {
            SQLCmdUpdateContext context = (SQLCmdUpdateContext) parameterObject;
            if (context.getExecution() instanceof UpdateChain) {
                UpdateChain updateChain = (UpdateChain) context.getExecution();
                if (updateChain.getReturnType() != null) {
                    return createQueryMappedStatement(updateChain.getReturnType(), ms);
                }
            }
            return ms;
        } else if (parameterObject instanceof SQLCmdDeleteContext && ms.getSqlCommandType() == SqlCommandType.SELECT) {
            SQLCmdDeleteContext context = (SQLCmdDeleteContext) parameterObject;
            if (context.getExecution() instanceof DeleteChain) {
                DeleteChain deleteChain = (DeleteChain) context.getExecution();
                if (deleteChain.getReturnType() != null) {
                    return createQueryMappedStatement(deleteChain.getReturnType(), ms);
                }
            }
            return ms;
        } else if (ms.getResultMaps().get(0).getType() != Object.class && !ms.getId().endsWith(MybatisMapperProxy.MAP_WITH_KEY_METHOD_NAME)) {
            return ms;
        } else if (parameterObject != null && parameterObject instanceof Map && ms.getSqlCommandType() == SqlCommandType.SELECT) {
            //兼容 PageHelper
            Map<String, Object> parameterMap = (Map<String, Object>) parameterObject;
            if (!parameterMap.containsKey("sqlBuilderContext")) {
                return ms;
            }
            Object sqlBuilderContext = parameterMap.get("sqlBuilderContext");
            if (sqlBuilderContext != null && sqlBuilderContext instanceof SqlBuilderContext) {
                if (!parameterMap.containsKey("execution")) {
                    return ms;
                }
                Object execution = parameterMap.get("execution");
                if (execution != null && execution instanceof BaseQuery) {
                    BaseQuery<?, ?> query = (BaseQuery) execution;
                    if (Objects.isNull(query.getReturnType())) {
                        return ms;
                    }
                    return createQueryMappedStatement(query.getReturnType(), ms);
                }
            }

            return ms;
        } else if (!(parameterObject instanceof SQLCmdQueryContext)) {
            return ms;
        }
        SQLCmdQueryContext queryContext = (SQLCmdQueryContext) parameterObject;
        if (Objects.isNull(queryContext.getExecution().getReturnType())) {
            return ms;
        }
        return createQueryMappedStatement(queryContext.getExecution().getReturnType(), ms);
    }

    private static MappedStatement createInsertMappedStatement(MappedStatement ms, Object parameterObject) {
        //这里是通用mapper 需要在运行时处理
        if (!(parameterObject instanceof SQLCmdInsertContext)) {
            return ms;
        }
        SQLCmdInsertContext sqlCmdInsertContext = (SQLCmdInsertContext) parameterObject;
        if (Objects.isNull(sqlCmdInsertContext.getEntityType())) {
            return ms;
        }

        SQLCmdSqlSource sqlCmdSqlSource = (SQLCmdSqlSource) ms.getSqlSource();

        String id = MybatisIdUtil.convertIdPath(sqlCmdInsertContext.getEntityType().getName()) + "-" + sqlCmdSqlSource.getDbType() + "-" + MAPPED_STATEMENT_DB_KEY_NAME + "@" + MybatisIdUtil.convertIdPath(ms.getId());

        if (ms.getConfiguration().hasStatement(id)) {
            return ms.getConfiguration().getMappedStatement(id);
        }

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), id, ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .resultMaps(ms.getResultMaps())
                .parameterMap(ms.getParameterMap())
                .keyGenerator(ms.getKeyGenerator())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .lang(ms.getLang())
                .timeout(ms.getTimeout())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());
        MappedStatement newMappedStatement = msBuilder.build();
        try {
            TableIdGeneratorWrapper.addEntityKeyGenerator(newMappedStatement, sqlCmdInsertContext.getEntityType());
            if (ms.getConfiguration().hasStatement(id)) {
                return ms.getConfiguration().getMappedStatement(id);
            }
            synchronized (id.intern()) {
                if (ms.getConfiguration().hasStatement(id)) {
                    return ms.getConfiguration().getMappedStatement(id);
                }
                ms.getConfiguration().addMappedStatement(newMappedStatement);
                return newMappedStatement;
            }
        } catch (IllegalArgumentException e) {
            ms.getStatementLog().warn(e.getMessage());
        }
        return ms;
    }

    private static MappedStatement createQueryMappedStatement(Class returnTypeClass, MappedStatement ms) {
        String id = MybatisIdUtil.convertIdPath(returnTypeClass.getName()) + "@" + MybatisIdUtil.convertIdPath(ms.getId());
        if (ms.getConfiguration().hasStatement(id)) {
            return ms.getConfiguration().getMappedStatement(id);
        }
        ResultMap resultMap;
        String resultMapId = returnTypeClass.getName();
        if (ms.getConfiguration().hasResultMap(resultMapId)) {
            resultMap = ms.getConfiguration().getResultMap(resultMapId);
        } else {
            resultMap = new ResultMap.Builder(ms.getConfiguration(), resultMapId, returnTypeClass, Collections.emptyList(), false).build();
        }
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), id, ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .resultMaps(ResultMapWrapper.replaceResultMap((MybatisConfiguration) ms.getConfiguration(), Collections.singletonList(resultMap)))
                .parameterMap(ms.getParameterMap())
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .lang(ms.getLang())
                .timeout(ms.getTimeout())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());
        MappedStatement newMappedStatement = msBuilder.build();
        try {
            if (ms.getConfiguration().hasStatement(id)) {
                return ms.getConfiguration().getMappedStatement(id);
            }
            synchronized (id.intern()) {
                if (ms.getConfiguration().hasStatement(id)) {
                    return ms.getConfiguration().getMappedStatement(id);
                }
                ms.getConfiguration().addMappedStatement(newMappedStatement);
            }
        } catch (IllegalArgumentException e) {
            ms.getStatementLog().warn(e.getMessage());
        }

        return newMappedStatement;

    }
}
