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

package db.sql.api.cmd.executor;

import db.sql.api.Getter;
import db.sql.api.cmd.basic.IConflict;
import db.sql.api.cmd.basic.IConflictAction;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;
import db.sql.api.cmd.struct.insert.IInsertFields;
import db.sql.api.cmd.struct.insert.IInsertSelect;
import db.sql.api.cmd.struct.insert.IInsertTable;
import db.sql.api.cmd.struct.insert.IInsertValues;

import java.util.List;
import java.util.function.Consumer;

public interface IInsert<SELF extends IInsert,
        TABLE extends ITable<TABLE, TABLE_FIELD>,
        TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        V,
        INSERT_TABLE extends IInsertTable<TABLE>,
        INSERT_FIELD extends IInsertFields<TABLE_FIELD>,
        INSERT_VALUE extends IInsertValues<V>,
        INSERT_SELECT extends IInsertSelect<IQuery>
        >
        extends IExecutor<SELF, TABLE, TABLE_FIELD> {


    INSERT_TABLE $insert(TABLE table);

    INSERT_FIELD $fields(TABLE_FIELD... fields);

    INSERT_FIELD $fields(List<TABLE_FIELD> fields);

    INSERT_VALUE $values(List<V> values);

    INSERT_SELECT $fromSelect(IQuery query);


    default SELF insert(TABLE table) {
        $insert(table);
        return (SELF) this;
    }

    SELF insert(Class entity);

    SELF insert(Class entity, Consumer<TABLE> consumer);

    default SELF fields(TABLE_FIELD... fields) {
        $fields(fields);
        return (SELF) this;
    }

    default SELF fields(List<TABLE_FIELD> fields) {
        $fields(fields);
        return (SELF) this;
    }

    <T> SELF fields(Getter<T>... fields);

    default SELF values(List<Object> values) {
        return this.values(values, false);
    }

    SELF values(List<Object> values, boolean enableNull);

    <T> SELF conflictKeys(Getter<T>... conflictKeys);

    SELF conflictKeys(String... conflictKeys);

    <T> SELF onConflict(Consumer<IConflictAction<T>> action);

    SELF fromSelect(IQuery query);

    INSERT_TABLE getInsertTable();

    INSERT_FIELD getInsertFields();

    INSERT_VALUE getInsertValues();

    IConflict getConflict();

}
