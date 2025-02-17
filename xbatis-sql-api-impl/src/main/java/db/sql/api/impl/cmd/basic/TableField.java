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

import db.sql.api.cmd.basic.ITableField;

public class TableField extends AbstractDatasetField<TableField> implements ITableField<TableField, Table> {

    private Boolean id;

    public TableField(Table table, String name) {
        super(table, name);
    }

    public TableField(Table table, String name, boolean id) {
        super(table, name);
        this.id = id;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TableField;
    }

    @Override
    public Table getTable() {
        return (Table) super.getTable();
    }

    public boolean isId() {
        if (id == null) {
            this.id = getTable().isId(this.getName());
        }
        return id;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $table = this.getTable().toString();
        result = result * 59 + ($table == null ? 43 : $table.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $alias = this.getAlias();
        result = result * 59 + ($alias == null ? 43 : $alias.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TableField)) {
            return false;
        } else {
            TableField other = (TableField) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$table = this.getTable();
                Object other$table = other.getTable();
                if (this$table != other$table) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$alias = this.getAlias();
                Object other$alias = other.getAlias();
                if (this$alias == null) {
                    return other$alias == null;
                }
                return this$alias.equals(other$alias);
            }
        }
    }
}
