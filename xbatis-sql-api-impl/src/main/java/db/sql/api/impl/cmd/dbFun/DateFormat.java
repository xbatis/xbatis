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
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.DatePattern;
import db.sql.api.impl.tookit.SqlConst;

public class DateFormat extends BasicFunction<DateFormat> {

    private final Cmd pattern;

    public DateFormat(Cmd key, String pattern) {
        super(null, key);
        this.pattern = Methods.cmd(pattern);
    }

    public DateFormat(Cmd key, DatePattern datePattern) {
        super(null, key);
        this.pattern = datePattern;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(SqlConst.DATE_FORMAT(context.getDbType()));
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = pattern.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        }
        sqlBuilder.append(SqlConst.DATE_FORMAT(context.getDbType()));
        sqlBuilder.append(SqlConst.BRACKET_LEFT);

        sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER);
        sqlBuilder = pattern.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}