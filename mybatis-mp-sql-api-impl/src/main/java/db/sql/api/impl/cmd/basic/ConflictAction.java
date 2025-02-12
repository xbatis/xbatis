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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IConflictAction;
import db.sql.api.cmd.basic.IConflictUpdate;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.tookit.CmdUtils;

import java.util.function.Consumer;

public class ConflictAction<T> implements IConflictAction<T>, Cmd {

    private final CmdFactory cmdFactory;

    private ConflictUpdate<T> conflictUpdate;

    private boolean doNothing;

    public ConflictAction(CmdFactory cmdFactory) {
        this.cmdFactory = cmdFactory;
    }

    public IConflictUpdate<T> getConflictUpdate() {
        return conflictUpdate;
    }

    @Override
    public void doNothing() {
        doNothing = true;
    }

    @Override
    public void doUpdate(Consumer<IConflictUpdate<T>> consumer) {
        if (this.conflictUpdate == null) {
            this.conflictUpdate = new ConflictUpdate(cmdFactory);
        }
        consumer.accept(this.conflictUpdate);
    }

    @Override
    public boolean isDoNothing() {
        return doNothing && this.conflictUpdate == null;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (this.doNothing == false && this.conflictUpdate == null) {
            throw new IllegalStateException("conflict action not set");
        }
        if (this.conflictUpdate == null) {
            if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.KING_BASE || context.getDbType() == DbType.SQLITE) {
                sqlBuilder.append(" DO NOTHING");
            } else if (context.getDbType() == DbType.OPEN_GAUSS) {
                sqlBuilder.append(" UPDATE NOTHING");
            }
        } else {
            if (context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.KING_BASE || context.getDbType() == DbType.SQLITE) {
                sqlBuilder.append(" DO UPDATE");
            } else if (context.getDbType() == DbType.OPEN_GAUSS) {
                sqlBuilder.append(" UPDATE");
            }
            this.conflictUpdate.sql(module, this, context, sqlBuilder);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.conflictUpdate);
    }
}
