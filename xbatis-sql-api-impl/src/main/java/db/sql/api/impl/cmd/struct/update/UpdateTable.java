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

package db.sql.api.impl.cmd.struct.update;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.update.IUpdateTable;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.executor.AbstractUpdate;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;

public class UpdateTable implements IUpdateTable<Table> {

    private final Table[] tables;

    public UpdateTable(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public Table[] getTables() {
        return tables;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (this.tables == null || this.tables.length < 1) {
            return sqlBuilder;
        }
        if (context.getDbType() == DbType.CLICK_HOUSE) {
            sqlBuilder.append(SqlConst.ALTER_TABLE);
        } else {
            sqlBuilder.append(SqlConst.UPDATE);
        }

        int length = this.tables.length;
        for (int i = 0; i < length; i++) {
            Table table = this.tables[i];
            if (i != 0) {
                sqlBuilder.append(SqlConst.DELIMITER);
            }

            if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.GAUSS) {
                //只能修改一张
                AbstractUpdate abstractUpdate = (AbstractUpdate) module;
                if (Objects.nonNull(abstractUpdate.getFrom())) {
                    abstractUpdate.getFrom().getTables().remove(table);
                }
            }

            if (context.getDbType() == DbType.SQL_SERVER || context.getDbType() == DbType.CLICK_HOUSE || context.getDbType() == DbType.SQLITE) {
                //SQLITE CLICK_HOUSE 不支持别名

                //SQL_SERVER 别名支持的话 需要 带有from ；否则 只能是表面
                AbstractUpdate abstractUpdate = (AbstractUpdate) module;
                if (Objects.nonNull(abstractUpdate.getFrom())) {
                    if (table.getAlias() != null) {
                        sqlBuilder.append(table.getAlias());
                    } else {
                        sqlBuilder.append(table.getName());
                    }
                } else {
                    //没有 from 不加别名
                    table.setAlias(null);
                    sqlBuilder.append(table.getName());
                }
                sqlBuilder.append(SqlConst.BLANK);
                return sqlBuilder;
            }

            sqlBuilder.append(table.getName());
            sqlBuilder.append(SqlConst.BLANK);
            if (table.getAlias() != null) {
                sqlBuilder.append(table.getAlias());
            }
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, (Object[]) this.tables);
    }
}
