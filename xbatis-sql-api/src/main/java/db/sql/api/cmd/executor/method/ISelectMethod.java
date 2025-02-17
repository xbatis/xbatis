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

package db.sql.api.cmd.executor.method;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.*;
import db.sql.api.cmd.executor.method.selectMethod.ISelectMethods;

import java.util.function.Consumer;

public interface ISelectMethod<SELF extends ISelectMethod,
        TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd>

        extends ISelectMethods<SELF,
        TABLE,
        TABLE_FIELD,
        COLUMN> {

    SELF selectDistinct();

    default SELF select1() {
        this.select((COLUMN) SQL1.INSTANCE);
        return (SELF) this;
    }

    default SELF selectAll() {
        this.select((COLUMN) SQLCmdAll.INSTANCE);
        return (SELF) this;
    }

    default SELF selectAll(IDataset<?, ?> dataset) {
        this.select((COLUMN) new SQLCmdAll(dataset));
        return (SELF) this;
    }

    SELF selectCount1();

    SELF selectCountAll();

    SELF selectCount1(Consumer<ICount1<?>> consumer);

    SELF selectCountAll(Consumer<ICountAll<?>> consumer);

    default SELF select(Class entity) {
        return this.select(entity, 1);
    }

    SELF select(Class entity, int storey);

    default SELF select(Class... entities) {
        return this.select(1, entities);
    }

    default SELF select(int storey, Class... entities) {
        for (Class entity : entities) {
            this.select(entity, storey);
        }
        return (SELF) this;
    }

    default <T> SELF selectIgnore(Getter<T> column) {
        return this.selectIgnore(column, 1);
    }

    <T> SELF selectIgnore(Getter<T> column, int storey);

    default <T> SELF selectIgnore(Getter<T>... columns) {
        return this.selectIgnore(1, columns);
    }

    default <T> SELF selectIgnore(int storey, Getter<T>... columns) {
        for (Getter column : columns) {
            this.selectIgnore(column, storey);
        }
        return (SELF) this;
    }
}
