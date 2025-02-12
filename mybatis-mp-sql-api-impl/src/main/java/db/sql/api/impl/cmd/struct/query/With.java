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

package db.sql.api.impl.cmd.struct.query;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IWithQuery;
import db.sql.api.cmd.struct.query.IWith;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class With implements IWith<With> {

    private final IWithQuery withQuery;

    public With(IWithQuery withQuery) {
        this.withQuery = withQuery;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        Cmd recursive = this.withQuery.getRecursive();

        if (Objects.nonNull(recursive) && (context.getDbType() == DbType.H2 || context.getDbType() ==
                DbType.MYSQL || context.getDbType() == DbType.MARIA_DB || context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.OPEN_GAUSS || context.getDbType() == DbType.KING_BASE)) {
            sqlBuilder.append(SqlConst.RECURSIVE);
        }

        sqlBuilder.append(this.withQuery.getAlias());
        if (Objects.nonNull(recursive)) {
            sqlBuilder = recursive.sql(module, this, context, sqlBuilder);
        }

        sqlBuilder.append(SqlConst.AS).append(SqlConst.BRACKET_LEFT);
        sqlBuilder = this.withQuery.sql(module, this, context, sqlBuilder).append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.withQuery);
    }
}
