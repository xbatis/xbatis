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
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.List;

public class RowValues implements Cmd {

    private final List<Cmd> values;

    private final int columnSize;

    public RowValues(List<Cmd> values, int columnSize) {
        this.values = values;
        this.columnSize = columnSize;
        int length = values == null ? 0 : values.size();
        if (length % columnSize != 0) {
            throw new IllegalArgumentException(String.format("column size must be a multiple of %d", columnSize));
        }
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (values == null) {
            return sqlBuilder;
        }
        if (columnSize == 1) {
            return CmdUtils.join(module, parent, context, sqlBuilder, values, SqlConst.DELIMITER);
        }
        int length = values.size();

        for (int i = 0; i < length; i++) {
            boolean start = i % columnSize == 0;
            if (i == 0) {
                sqlBuilder.append(SqlConst.BRACKET_LEFT);
            } else if (start) {
                //非首次的批次开始
                sqlBuilder.append(SqlConst.DELIMITER);
                sqlBuilder.append(SqlConst.BRACKET_LEFT);
            } else {
                sqlBuilder.append(SqlConst.DELIMITER);
            }
            Cmd cmd = values.get(i);
            if (cmd == null) {
                sqlBuilder.append(SqlConst.NULL);
            } else {
                sqlBuilder = cmd.sql(module, parent, context, sqlBuilder);
            }
            if ((i + 1) % columnSize == 0) {
                // 1个批次结束
                sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            }
        }

        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.values);
    }
}
