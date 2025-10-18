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
import db.sql.api.impl.tookit.SqlConst;

public class DateAdd extends BasicFunction<DateAdd> {

    private final int n;

    private final TimeUnits timeUnit;

    public DateAdd(Cmd key, int n, TimeUnits timeUnit) {
        super(SqlConst.DATE_ADD, key);
        this.n = n;
        this.timeUnit = timeUnit;
    }


    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.H2) {
            sqlBuilder.append("DATEADD").append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append(timeUnit.name(), 0, timeUnit.name().length() - 1);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder.append(n);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.OPEN_GAUSS) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append("+'");
            sqlBuilder.append(n);
            sqlBuilder.append(SqlConst.BLANK);
            sqlBuilder.append(timeUnit.name(), 0, timeUnit.name().length() - 1);
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.DM || context.getDbType() == DbType.SQL_SERVER) {
            sqlBuilder.append("DATEADD").append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append(timeUnit.name(), 0, timeUnit.name().length() - 1);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder.append(n);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.ORACLE || context.getDbType() == DbType.KING_BASE) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append('+');
            sqlBuilder.append(SqlConst.INTERVAL).append(SqlConst.SINGLE_QUOT).append(this.n).append(SqlConst.SINGLE_QUOT);
            sqlBuilder.append(timeUnit.name(), 0, timeUnit.name().length() - 1);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.DB2) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append('+');
            sqlBuilder.append(this.n).append(SqlConst.BLANK);
            sqlBuilder.append(timeUnit.name());
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        } else if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append("DATETIME");
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder.append(SqlConst.SINGLE_QUOT);


            sqlBuilder.append(this.n).append(SqlConst.BLANK);
            sqlBuilder.append(timeUnit.name());
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        }

        sqlBuilder.append(operator).append(SqlConst.BRACKET_LEFT);
        sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER).append(SqlConst.INTERVAL).append(this.n);
        sqlBuilder.append(SqlConst.BLANK);
        sqlBuilder.append(timeUnit.name(), 0, timeUnit.name().length() - 1);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}