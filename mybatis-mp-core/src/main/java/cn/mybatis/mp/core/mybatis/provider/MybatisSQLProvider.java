/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.mybatis.provider;


import cn.mybatis.mp.core.mybatis.mapper.context.*;
import cn.mybatis.mp.core.sql.executor.BaseQuery;
import db.sql.api.DbType;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Objects;

public class MybatisSQLProvider {
    public static final String SAVE_NAME = "save";
    public static final String UPDATE_NAME = "update";
    public static final String DELETE_NAME = "delete";
    public static final String QUERY_NAME = "cmdQuery";
    public static final String GET_QUERY_NAME = "getCmdQuery";
    public static final String GET_BY_ID_QUERY_NAME = "getByIdCmdQuery";
    public static final String COUNT_NAME = "cmdCount";
    public static final String QUERY_COUNT_NAME = "countFromQuery";

    private MybatisSQLProvider() {

    }

    public static String save(SQLCmdInsertContext insertContext, ProviderContext providerContext, DbType dbType) {
        insertContext.init(dbType);
        return insertContext.sql(dbType);
    }

    public static String update(SQLCmdUpdateContext updateContext, ProviderContext providerContext, DbType dbType) {
        updateContext.init(dbType);
        String sql = updateContext.sql(dbType);
        if ((updateContext.getExecution().getWhere() == null || !updateContext.getExecution().getWhere().hasContent()) && (!updateContext.getExecution().getWhere().extConditionChain().hasContent())) {
            throw new RuntimeException("update has no where condition content ");
        }
        return sql;
    }

    public static String delete(SQLCmdDeleteContext deleteContext, ProviderContext providerContext, DbType dbType) {
        deleteContext.init(dbType);
        String sql = deleteContext.sql(dbType);
        if (deleteContext.getExecution().getWhere() == null || !deleteContext.getExecution().getWhere().hasContent()) {
            throw new RuntimeException("delete has no where condition content ");
        }
        return sql;
    }

    /**
     * 处理前缀映射
     *
     * @param queryContext
     */
    private static void handlerPrefixMapping(SQLCmdQueryContext queryContext) {
        BaseQuery query = queryContext.getExecution();
        if (Objects.nonNull(query.getReturnType())) {
            TablePrefixUtil.prefixMapping(query, query.getReturnType());
        }
    }


    public static String countFromQuery(SQLCmdCountFromQueryContext queryContext, ProviderContext providerContext, DbType dbType) {
        queryContext.init(dbType);
        return queryContext.sql(dbType);
    }

    public static String cmdQuery(SQLCmdQueryContext queryContext, ProviderContext providerContext, DbType dbType) {
        queryContext.init(dbType);
        handlerPrefixMapping(queryContext);
        return queryContext.sql(dbType);
    }

    public static String getByIdCmdQuery(SQLCmdQueryContext queryContext, ProviderContext providerContext, DbType dbType) {
        queryContext.init(dbType);
        return queryContext.sql(dbType);
    }

    public static String getCmdQuery(SQLCmdQueryContext queryContext, ProviderContext providerContext, DbType dbType) {
        //SQL_SERVER 需要order by 才能分页 所以不加
        if (dbType != DbType.SQL_SERVER) {
            if (Objects.isNull(queryContext.getExecution().getLimit())) {
                queryContext.getExecution().limit(2);
            }
        }
        return cmdQuery(queryContext, null, dbType);
    }

    public static String cmdCount(SQLCmdCountQueryContext queryContext, ProviderContext providerContext, DbType dbType) {
        queryContext.init(dbType);
        return queryContext.sql(dbType);
    }
}
