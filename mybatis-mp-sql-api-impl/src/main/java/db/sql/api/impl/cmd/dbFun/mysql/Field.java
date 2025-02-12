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

package db.sql.api.impl.cmd.dbFun.mysql;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.dbFun.BasicFunction;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class Field extends BasicFunction<Field> {

    private final Cmd[] values;

    public Field(Cmd key, Object[] values) {
        super(SqlConst.FILED, key);
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

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(this.operator).append(SqlConst.BRACKET_LEFT);
        this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER);
        CmdUtils.join(module, this, context, sqlBuilder, this.values, SqlConst.DELIMITER);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key, this.values);
    }
}
