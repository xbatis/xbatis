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
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import static db.sql.api.impl.tookit.SqlConst.GROUP_CONCAT;

public class GroupConcat extends BasicFunction<GroupConcat> {

    private final Cmd split;

    public GroupConcat(Cmd key, String split) {
        super(null, key);
        this.split = Methods.cmd(split);
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.ORACLE || context.getDbType() == DbType.DB2 || context.getDbType() == DbType.DM) {
            sqlBuilder.append("LISTAGG");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            this.split.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append(" WITHIN GROUP( ");
            sqlBuilder.append(SqlConst.ORDER_BY);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        } else if (context.getDbType() == DbType.SQL_SERVER) {
            sqlBuilder.append("STRING_AGG");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            this.split.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append(" WITHIN GROUP( ");
            sqlBuilder.append(SqlConst.ORDER_BY);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        } else if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.OPEN_GAUSS) {
            sqlBuilder.append("STRING_AGG");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append("::TEXT ");
            sqlBuilder.append(SqlConst.DELIMITER);
            this.split.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.ORDER_BY);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        } else if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(GROUP_CONCAT);
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            this.split.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        } else {
            sqlBuilder.append(GROUP_CONCAT);
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.ORDER_BY);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(" SEPARATOR ");
            this.split.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key);
    }
}
