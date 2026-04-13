/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.*;

import java.util.function.Function;

public interface IOrderByMethods<SELF extends IOrderByMethods,
        TABLE extends ITable<TABLE, TABLE_FIELD>,
        TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd>
        extends

        IOrderByCmdMethod<SELF, COLUMN>,
        IOrderByCmdAscMethod<SELF, COLUMN>,
        IOrderByCmdDescMethod<SELF, COLUMN>,

        IOrderByGetterMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByGetterAscMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByGetterDescMethod<SELF, TABLE, TABLE_FIELD>,

        IOrderByGetterFunMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByGetterFunAscMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByGetterFunDescMethod<SELF, TABLE, TABLE_FIELD>,

        IOrderByMultiGetterMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByMultiGetterAscMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByMultiGetterDescMethod<SELF, TABLE, TABLE_FIELD>,

        IOrderByMultiGetterFunMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByMultiGetterFunAscMethod<SELF, TABLE, TABLE_FIELD>,
        IOrderByMultiGetterFunDescMethod<SELF, TABLE, TABLE_FIELD>,

        IOrderByDatasetMethod<SELF>,
        IOrderByDatasetAscMethod<SELF>,
        IOrderByDatasetDescMethod<SELF>,

        IOrderByDatasetGetterMethod<SELF>,
        IOrderByDatasetGetterAscMethod<SELF>,
        IOrderByDatasetGetterDescMethod<SELF>,

        IOrderByDatasetGetterFunMethod<SELF>,
        IOrderByDatasetGetterFunAscMethod<SELF>,
        IOrderByDatasetGetterFunDescMethod<SELF>,

        IOrderByDatasetMultiGetterMethod<SELF>,
        IOrderByDatasetMultiGetterAscMethod<SELF>,
        IOrderByDatasetMultiGetterDescMethod<SELF>,

        IOrderByDatasetMultiGetterFunMethod<SELF>,
        IOrderByDatasetMultiGetterFunAscMethod<SELF>,
        IOrderByDatasetMultiGetterFunDescMethod<SELF> {


    default SELF orderBy(String columnName) {
        return this.orderBy(ascOrderByDirection(), columnName);
    }

    default SELF orderByAsc(String columnName) {
        return this.orderBy(ascOrderByDirection(), columnName);
    }

    default SELF orderByDesc(String columnName) {
        return this.orderBy(descOrderByDirection(), columnName);
    }

    default SELF orderBy(boolean when, String columnName) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columnName);
    }

    default SELF orderByAsc(boolean when, String columnName) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columnName);
    }

    default SELF orderByDesc(boolean when, String columnName) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), columnName);
    }

    default SELF orderBy(String columnName, Function<IDatasetField<?>, Cmd> f) {
        return this.orderBy(ascOrderByDirection(), columnName, f);
    }

    default SELF orderByAsc(String columnName, Function<IDatasetField<?>, Cmd> f) {
        return this.orderBy(ascOrderByDirection(), columnName, f);
    }

    default SELF orderByDesc(String columnName, Function<IDatasetField<?>, Cmd> f) {
        return this.orderBy(descOrderByDirection(), columnName, f);
    }

    default SELF orderBy(boolean when, String columnName, Function<IDatasetField<?>, Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columnName, f);
    }

    default SELF orderByAsc(boolean when, String columnName, Function<IDatasetField<?>, Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columnName, f);
    }

    default SELF orderByDesc(boolean when, String columnName, Function<IDatasetField<?>, Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), columnName, f);
    }

    SELF orderBy(IOrderByDirection orderByDirection, String columnName);

    SELF orderBy(IOrderByDirection orderByDirection, String columnName, Function<IDatasetField<?>, Cmd> f);

    @Override
    @SuppressWarnings("unchecked")
    default <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, Getter<T>... columns) {
        for (Getter<T> column : columns) {
            this.orderBy(dataset, orderByDirection, column);
        }
        return (SELF) this;
    }

    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, String columnName) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, columnName);
    }

    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, String columnName, Function<IDatasetField<?>, Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, columnName, f);
    }
}
