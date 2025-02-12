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

package db.sql.api.impl.cmd.struct.insert;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.insert.IInsertTable;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.executor.AbstractInsert;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.regex.Matcher;

public class InsertTable implements IInsertTable<Table> {

    protected final Table table;

    public InsertTable(Table table) {
        this.table = table;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        AbstractInsert abstractInsert = (AbstractInsert) parent;

        boolean insertIgnore = (context.getDbType() == DbType.MYSQL || context.getDbType() == DbType.MARIA_DB || context.getDbType() == DbType.H2 || context.getDbType() == DbType.ORACLE)
                && abstractInsert.getConflict() != null
                && abstractInsert.getConflict().getConflictAction().isDoNothing();

        if (insertIgnore) {
            if (context.getDbType() == DbType.ORACLE) {
                //可能需要增加ConflictKeys
                abstractInsert.getConflict().addDefaultConflictKeys(abstractInsert, context.getDbType());

                sqlBuilder.append(SqlConst.INSERT).append("--+ IGNORE_ROW_ON_DUPKEY_INDEX(")
                        .append(table.getName(context.getDbType())).append(SqlConst.BRACKET_LEFT)
                        .append(String.join(",", abstractInsert.getConflict().getConflictKeys()))
                        .append(SqlConst.BRACKET_RIGHT).append(SqlConst.BRACKET_RIGHT)
                        .append(System.lineSeparator().replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement("\\\\")))
                        .append(SqlConst.INTO);
            } else {
                sqlBuilder.append(SqlConst.INSERT_IGNORE_INTO);
            }
        } else {
            sqlBuilder.append(SqlConst.INSERT_INTO);
        }

        sqlBuilder.append(this.table.getName());
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.table);
    }
}
