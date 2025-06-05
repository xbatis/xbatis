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

package cn.xbatis.core.mybatis.provider;

import cn.xbatis.core.function.ThreeFunction;
import cn.xbatis.core.mybatis.mapper.context.*;
import cn.xbatis.core.util.DbTypeUtil;
import db.sql.api.DbType;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SQLCmdSqlSource implements SqlSource {

    private final static Map<String, ThreeFunction<Object, ProviderContext, DbType, String>> SQL_GENERATOR_FUN_MAP = new HashMap<>();

    static {
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.QUERY_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.cmdQuery((SQLCmdQueryContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.GET_QUERY_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.getCmdQuery((SQLCmdQueryContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.GET_BY_ID_QUERY_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.getByIdCmdQuery((SQLCmdQueryContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.COUNT_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.cmdCount((SQLCmdCountQueryContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.QUERY_COUNT_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.countFromQuery((SQLCmdCountFromQueryContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.UPDATE_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.update((SQLCmdUpdateContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.DELETE_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.delete((SQLCmdDeleteContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.SAVE_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.save((SQLCmdInsertContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.UPDATE_AND_RETURNING_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.updateAndReturning((SQLCmdUpdateContext) context, providerContext, dbType));
        SQL_GENERATOR_FUN_MAP.put(MybatisSQLProvider.DELETE_AND_RETURNING_NAME, (context, providerContext, dbType) -> MybatisSQLProvider.deleteAndReturning((SQLCmdDeleteContext) context, providerContext, dbType));
    }

    private final Configuration configuration;
    private final Method providerMethod;
    private final ProviderContext providerContext;

    public SQLCmdSqlSource(Configuration configuration, Method providerMethod, ProviderContext providerContext) {
        this.configuration = configuration;
        this.providerMethod = providerMethod;
        this.providerContext = providerContext;
    }


    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        String methodName = providerMethod.getName();
        ThreeFunction<Object, ProviderContext, DbType, String> sqlGenerator = SQL_GENERATOR_FUN_MAP.get(methodName);
        if (Objects.isNull(sqlGenerator)) {
            throw new RuntimeException("Unadapted: Unknown SQL method: " + methodName);
        }
        String sql = sqlGenerator.apply(parameterObject, this.providerContext, getDbType());
        return new BoundSql(this.configuration, sql, Collections.singletonList(new ParameterMapping
                .Builder(configuration, "name", Object.class)
                .build()), parameterObject);
    }

    public DbType getDbType() {
        return DbTypeUtil.getDbType(configuration);
    }

}
