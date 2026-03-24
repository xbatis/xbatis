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

import cn.xbatis.core.mybatis.executor.MappedStatementUtil;
import cn.xbatis.core.util.PagingUtil;
import cn.xbatis.page.IPager;
import db.sql.api.IDbType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

public class PagingListSqlSource implements SqlSource {

    private final Configuration configuration;
    private final SqlSource sqlSource;

    public PagingListSqlSource(Configuration configuration, SqlSource sqlSource) {
        this.configuration = configuration;
        this.sqlSource = sqlSource;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        IPager<?> pager;
        if (parameterObject instanceof IPager) {
            pager = (IPager<?>) parameterObject;
        } else {
            Map<String, Object> params = (Map<String, Object>) parameterObject;
            if (params.containsKey("param1")) {
                pager = (IPager<?>) params.get("param1");
            } else {
                pager = (IPager<?>) params.get("arg0");
            }
        }
        IDbType dbType = MappedStatementUtil.getDbType(configuration, parameterObject, boundSql);
        return new PagingBoundSql(dbType, this.configuration, PagingUtil.getLimitedSQL(dbType, pager, sql), boundSql);
    }
}
