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

import cn.xbatis.core.dbType.DbTypeUtil;
import cn.xbatis.core.util.PagingUtil;
import db.sql.api.IDbType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public class PagingCountSqlSource implements SqlSource {

    private final Configuration configuration;
    private final SqlSource sqlSource;
    private final boolean optimize;

    public PagingCountSqlSource(Configuration configuration, SqlSource sqlSource, boolean optimize) {
        this.configuration = configuration;
        this.sqlSource = sqlSource;
        this.optimize = optimize;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        String sql = PagingUtil.getCountSQL(getDbType(), boundSql.getSql(), optimize);
        return new PagingBoundSql(this.configuration, sql, boundSql);
    }


    public IDbType getDbType() {
        return DbTypeUtil.getDbType(configuration);
    }
}
