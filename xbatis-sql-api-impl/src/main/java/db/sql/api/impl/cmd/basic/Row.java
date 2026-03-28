/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.dbFun.FunctionInterface;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class Row implements FunctionInterface {

    private final Cmd[] columns;

    public Row(Cmd[] columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("columns is empty");
        }
        this.columns = columns;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (columns.length == 1) {
            sqlBuilder = CmdUtils.join(module, this, context, sqlBuilder, columns, SqlConst.DELIMITER);
            return sqlBuilder;
        }
        sqlBuilder.append(SqlConst.BRACKET_LEFT);
        sqlBuilder = CmdUtils.join(module, this, context, sqlBuilder, columns, SqlConst.DELIMITER);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, columns);
    }
}
