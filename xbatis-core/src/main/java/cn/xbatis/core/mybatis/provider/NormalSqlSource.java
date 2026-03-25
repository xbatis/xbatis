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

package cn.xbatis.core.mybatis.provider;

import cn.xbatis.core.dbType.DbTypeUtil;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.executor.Where;
import db.sql.api.IDbType;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NormalSqlSource implements SqlSource {

    private final Configuration configuration;

    private final SqlSource delegate;

    public NormalSqlSource(Configuration configuration, SqlSource delegate) {
        this.configuration = configuration;
        this.delegate = delegate;
    }

    private IDbType setParameterDbType(Object parameterObject) {
        return setParameterDbType(parameterObject, null, null);
    }

    private IDbType setParameterDbType(Object parameterObject, String paramName, IDbType dbType) {
        if (parameterObject instanceof Where) {
            dbType = DbTypeUtil.getDbType(this.configuration);
            Where where = (Where) parameterObject;
            where.setDbType(dbType);
            where.setMybatisParamName(paramName);
        } else if (parameterObject instanceof Query) {
            dbType = DbTypeUtil.getDbType(this.configuration);
            Query query = (Query) parameterObject;
            query.setDbType(dbType);
            query.setMybatisParamName(paramName);
        } else if (parameterObject instanceof MapperMethod.ParamMap) {
            Set<Map.Entry<String, Object>> values = ((Map) parameterObject).entrySet();
            Map<String, Object> paramNameSet = new HashMap<>();
            Set<Object> hasSet = new HashSet<>();
            for (Map.Entry<String, Object> entry : values) {
                if (!(entry.getValue() instanceof Where || entry.getValue() instanceof Query)) {
                    continue;
                }
                if (entry.getKey().startsWith("arg") || entry.getKey().startsWith("param")) {
                    paramNameSet.put(entry.getKey(), entry.getValue());
                } else {
                    hasSet.add(entry.getValue());
                    dbType = setParameterDbType(entry.getValue(), entry.getKey(), dbType);
                }
            }

            for (Map.Entry<String, Object> entry : paramNameSet.entrySet()) {
                if (hasSet.contains(entry.getValue())) {
                    continue;
                }
                dbType = setParameterDbType(entry.getValue(), entry.getKey(), dbType);
            }
        }
        return dbType;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        setParameterDbType(parameterObject);
        return delegate.getBoundSql(parameterObject);
    }
}
