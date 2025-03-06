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

import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Consumer;

public interface IFromMethod<SELF extends IFromMethod, TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> {

    @SuppressWarnings("unchecked")
    SELF from(IDataset<?, ?>... tables);

    default SELF from(Class... entities) {
        return this.from(1, entities);
    }

    default SELF from(int storey, Class... entities) {
        for (Class entity : entities) {
            this.from(entity, storey);
        }
        return (SELF) this;
    }

    default SELF from(Class entity, int storey) {
        return this.from(entity, storey, null);
    }

    default SELF from(Class entity, Consumer<TABLE> consumer) {
        return this.from(entity, 1, consumer);
    }

    SELF from(Class entity, int storey, Consumer<TABLE> consumer);
}
