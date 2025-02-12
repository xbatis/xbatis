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

package db.sql.api.cmd.executor.method.orderByMethod;


import db.sql.api.Getter;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

public interface IOrderByGetterMethod<SELF extends IOrderByGetterMethod, TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> extends IOrderByGetterFunMethod<SELF, TABLE, TABLE_FIELD> {

    default <T> SELF orderBy(Getter<T> column) {
        return this.orderBy(ascOrderByDirection(), column, 1);
    }

    default <T> SELF orderBy(Getter<T> column, int storey) {
        return this.orderBy(ascOrderByDirection(), column, storey);
    }

    default <T> SELF orderByDesc(Getter<T> column) {
        return this.orderBy(descOrderByDirection(), column, 1);
    }

    default <T> SELF orderByDesc(Getter<T> column, int storey) {
        return this.orderBy(descOrderByDirection(), column, storey);
    }

    default <T> SELF orderBy(boolean when, Getter<T> column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), column, 1);
    }

    default <T> SELF orderBy(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), column, storey);
    }

    default <T> SELF orderByDesc(boolean when, Getter<T> column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), column, 1);
    }

    default <T> SELF orderByDesc(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), column, storey);
    }

    default <T> SELF orderBy(IOrderByDirection orderByDirection, Getter<T> column) {
        return this.orderBy(orderByDirection, column, 1);
    }

    default <T> SELF orderBy(IOrderByDirection orderByDirection, Getter<T> column, int storey) {
        return this.orderBy(orderByDirection, column, storey, null);
    }

    default <T> SELF orderBy(boolean when, IOrderByDirection orderByDirection, Getter<T> column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, column, 1);
    }

    default <T> SELF orderBy(boolean when, IOrderByDirection orderByDirection, Getter<T> column, int storey) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, column, storey);
    }
}
