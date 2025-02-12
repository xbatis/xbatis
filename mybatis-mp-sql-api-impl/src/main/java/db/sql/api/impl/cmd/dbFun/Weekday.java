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

import java.time.LocalDate;

public class Weekday extends BasicFunction<Weekday> {
    public Weekday(Cmd key) {
        super(null, key);
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.parse("2023-10-11").getDayOfWeek().getValue());
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(SqlConst.WEEKDAY(context.getDbType()));
        if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT).append("'%w'").append(SqlConst.DELIMITER);
        } else if (context.getDbType() != DbType.SQL_SERVER) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
        }

        sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
        if (context.getDbType() == DbType.ORACLE || context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.OPEN_GAUSS || context.getDbType() == DbType.KING_BASE) {
            sqlBuilder.append(SqlConst.DELIMITER).append(" 'D'");
        }
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}
