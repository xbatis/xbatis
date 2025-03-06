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

package db.sql.api.cmd.executor.method.orderByMethod;


import db.sql.api.Getter;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

public interface IOrderByMultiGetterMethod<SELF extends IOrderByMultiGetterMethod, TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> extends IOrderByMultiGetterFunMethod<SELF, TABLE, TABLE_FIELD> {

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(Getter<T>... columns) {
        return this.orderBy(ascOrderByDirection(), 1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(int storey, Getter<T>... columns) {
        return this.orderBy(ascOrderByDirection(), storey, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderByDesc(Getter<T>... columns) {
        return this.orderBy(descOrderByDirection(), 1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderByDesc(int storey, Getter<T>... columns) {
        return this.orderBy(descOrderByDirection(), storey, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(boolean when, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), 1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(boolean when, int storey, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), storey, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderByDesc(boolean when, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), 1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderByDesc(boolean when, int storey, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), storey, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(IOrderByDirection orderByDirection, Getter<T>... columns) {
        return this.orderBy(orderByDirection, 1, columns);
    }

    @SuppressWarnings("unchecked")
    <T> SELF orderBy(IOrderByDirection orderByDirection, int storey, Getter<T>... columns);

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(boolean when, IOrderByDirection orderByDirection, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, 1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF orderBy(boolean when, IOrderByDirection orderByDirection, int storey, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, storey, columns);
    }
}
