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

package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.basic.BasicValue;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class DateDiff extends BasicFunction<DateDiff> {

    private final Cmd another;

    public DateDiff(Cmd key, Cmd another) {
        super(SqlConst.DATE_DIFF, key);
        this.another = another;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(SqlConst.CEIL).append(SqlConst.BRACKET_LEFT).append(SqlConst.ABS).append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append("JULIANDAY");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append(SqlConst.SUBTRACT);
            sqlBuilder.append("JULIANDAY");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.another.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BRACKET_RIGHT).append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.H2 || context.getDbType() == DbType.SQL_SERVER || context.getDbType() == DbType.DM) {
            sqlBuilder.append("ABS(").append(operator).append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append("DAY,");
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder = this.another.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.GAUSS) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT).append("DATE_PART").append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append("'DAY',");
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append('-');
            sqlBuilder.append("DATE_PART").append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append("'DAY',");
            this.another.sql(module, this, context, sqlBuilder).append("::DATE ");
            sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.DB2) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append("DAYS").append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append('-');
            sqlBuilder.append("DAYS").append(SqlConst.BRACKET_LEFT);

            if (this.another instanceof BasicValue) {
                sqlBuilder.append("CAST(");
            }
            this.another.sql(module, this, context, sqlBuilder);
            if (this.another instanceof BasicValue) {
                sqlBuilder.append(" AS TIMESTAMP )");
            }

            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else {
            sqlBuilder.append(operator).append(SqlConst.BRACKET_LEFT);
            this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            this.another.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        }
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key, this.another);
    }
}
