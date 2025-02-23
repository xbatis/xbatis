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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IConflictUpdate;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.executor.AbstractInsert;
import db.sql.api.impl.cmd.struct.insert.InsertFields;
import db.sql.api.impl.cmd.struct.update.UpdateSets;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class ConflictUpdate<T> implements IConflictUpdate<T>, Cmd {

    private final CmdFactory cmdFactory;

    private boolean overwriteAll;

    private List<TableField> customizeSetValueFields;

    private List<TableField> overwriteFields;

    private List<TableField> ignoreFields;

    private UpdateSets updateSets;

    //是否已经执行
    private boolean execute;

    public ConflictUpdate(CmdFactory cmdFactory) {
        this.cmdFactory = cmdFactory;
    }

    @Override
    public IConflictUpdate<T> set(Getter<T> field, Object value) {
        if (customizeSetValueFields == null) {
            this.customizeSetValueFields = new ArrayList<>();
        }
        if (this.updateSets == null) {
            this.updateSets = new UpdateSets();
        }
        TableField tableField = cmdFactory.field(field);
        this.updateSets.set(tableField, Methods.cmd(value));
        this.customizeSetValueFields.add(tableField);
        return this;
    }

    @Override
    public IConflictUpdate<T> overwrite(Getter<T>... fields) {
        for (Getter<T> field : fields) {
            TableField tableField = cmdFactory.field(field);
            if (this.overwriteFields == null) {
                this.overwriteFields = new ArrayList<>();
            }
            this.overwriteFields.add(tableField);
        }
        return this;
    }

    @Override
    public IConflictUpdate<T> overwriteAll() {
        this.overwriteAll = true;
        return this;
    }

    @Override
    public IConflictUpdate<T> ignore(Getter<T>... fields) {
        for (Getter<T> field : fields) {
            TableField tableField = cmdFactory.field(field);
            if (this.ignoreFields == null) {
                this.ignoreFields = new ArrayList<>();
            }
            this.ignoreFields.add(tableField);
        }
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (!this.execute) {
            this.execute = true;
            if (this.updateSets == null) {
                this.updateSets = new UpdateSets();
            }
            if (this.overwriteAll) {
                AbstractInsert insert = (AbstractInsert) module;
                InsertFields insertFields = insert.getInsertFields();
                insertFields.getFields().stream().filter(item -> !item.isId()).forEach(i -> {
                    if (this.ignoreFields != null && this.ignoreFields.contains(i)) {
                        return;
                    }
                    if (this.customizeSetValueFields != null && this.customizeSetValueFields.contains(i)) {
                        return;
                    }
                    updateSets.set(i, new ConflictUpdateTableField(i));
                });
            } else if (this.overwriteFields != null) {
                this.overwriteFields.forEach(i -> {
                    if (this.ignoreFields != null && this.ignoreFields.contains(i)) {
                        return;
                    }
                    if (this.customizeSetValueFields != null && this.customizeSetValueFields.contains(i)) {
                        return;
                    }
                    updateSets.set(i, new ConflictUpdateTableField(i));
                });
            } else if (this.customizeSetValueFields == null) {
                throw new IllegalStateException("conflict update not set");
            }
        }

        if (this.updateSets == null) {
            throw new IllegalStateException("conflict update not set");
        }
        return this.updateSets.sql(module, this, context, sqlBuilder);
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.updateSets);
    }
}
