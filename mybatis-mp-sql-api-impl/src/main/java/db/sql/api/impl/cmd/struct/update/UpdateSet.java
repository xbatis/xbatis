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

package db.sql.api.impl.cmd.struct.update;


import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IInsert;
import db.sql.api.cmd.struct.update.IUpdateSet;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.executor.AbstractUpdate;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;

public class UpdateSet implements IUpdateSet<TableField, Cmd> {

    private final TableField field;

    private final Cmd value;

    public UpdateSet(TableField field, Cmd value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public TableField getField() {
        return this.field;
    }

    @Override
    public Cmd getValue() {
        return this.value;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.KING_BASE || context.getDbType() == DbType.OPEN_GAUSS || module instanceof IInsert) {
            //PG update set 列 不支持别名 直接拼接列名
            sqlBuilder.append(this.field.getName(context.getDbType()));
        } else if (context.getDbType() == DbType.SQL_SERVER) {
            //SQL_SERVER 别名支持的话 需要 带有from ；否则 只能是表面
            AbstractUpdate abstractUpdate = (AbstractUpdate) module;
            if (Objects.nonNull(abstractUpdate.getFrom())) {
                sqlBuilder = this.field.sql(module, this, context, sqlBuilder);
            } else {
                sqlBuilder.append(this.field.getName(context.getDbType()));
            }
        } else {
            sqlBuilder = this.field.sql(module, this, context, sqlBuilder);
        }

        sqlBuilder.append(SqlConst.EQ);
        sqlBuilder = this.value.sql(module, this, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.field, this.value);
    }
}
