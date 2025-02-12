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
import db.sql.api.impl.cmd.basic.BasicValue;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import static db.sql.api.impl.tookit.SqlConst.CONCAT;
import static db.sql.api.impl.tookit.SqlConst.CONCAT_SPLIT_SYMBOL;

public class Concat extends BasicFunction<Concat> {

    private final Cmd[] values;

    public Concat(Cmd key, Object... values) {
        super(CONCAT, key);
        Cmd[] vs = new Cmd[values.length];
        int i = 0;
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            if (value instanceof Cmd) {
                vs[i++] = (Cmd) value;
            } else {
                vs[i++] = Methods.cmd(value);
            }
        }
        this.values = vs;
    }

    private static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Cmd[] cmds, char[] delimiter) {
        if (Objects.isNull(cmds) || cmds.length < 1) {
            return builder;
        }
        int length = cmds.length;
        for (int i = 0; i < length; i++) {
            if (i != 0 && delimiter != null) {
                builder.append(delimiter);
            }
            Cmd value = cmds[i];
            builder = cmds[i].sql(module, parent, context, builder);

            if (value.getClass() == BasicValue.class && (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.OPEN_GAUSS)) {
                builder.append(SqlConst.CAST_TEXT);
            }
        }
        return builder;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if ((context.getDbType() == DbType.ORACLE || context.getDbType() == DbType.DB2) && this.values.length > 1 || context.getDbType() == DbType.SQLITE) {
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.CONCAT_SPLIT_SYMBOL);
            CmdUtils.join(module, parent, context, sqlBuilder, this.values, CONCAT_SPLIT_SYMBOL);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        }

        sqlBuilder.append(this.operator).append(SqlConst.BRACKET_LEFT);
        sqlBuilder = this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER);
        join(module, this, context, sqlBuilder, this.values, SqlConst.DELIMITER);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }


    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key, this.values);
    }
}
