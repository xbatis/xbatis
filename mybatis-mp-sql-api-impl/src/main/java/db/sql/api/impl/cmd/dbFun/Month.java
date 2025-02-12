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

package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.tookit.SqlConst;

public class Month extends BasicFunction<Month> {
    public Month(Cmd key) {
        super(null, key);
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(SqlConst.MONTH(context.getDbType()));
        if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT).append("'%m'").append(SqlConst.DELIMITER);
        } else if (context.getDbType() != DbType.PGSQL && context.getDbType() != DbType.OPEN_GAUSS && context.getDbType() != DbType.ORACLE) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
        }

        this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}
