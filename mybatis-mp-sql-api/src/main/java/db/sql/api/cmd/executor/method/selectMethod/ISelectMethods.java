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

package db.sql.api.cmd.executor.method.selectMethod;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Function;

public interface ISelectMethods<SELF extends ISelectMethods,
        TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd>
        extends
        ISelectCmdMethod<SELF, COLUMN>,
        ISelectGetterMethod<SELF, TABLE, TABLE_FIELD>,
        ISelectMultiGetterMethod<SELF, TABLE, TABLE_FIELD>,

        ISelectDatasetMethod<SELF>,
        ISelectDatasetGetterMethod<SELF>,
        ISelectDatasetMultiGetterMethod<SELF> {

    @Override
    default <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        for (Getter<T> column : columns) {
            this.select(dataset, column);
        }
        return (SELF) this;
    }

    SELF select(String columnName);

    SELF select(String columnName, Function<IDatasetField, Cmd> f);

    default SELF select(boolean when, String columnName) {
        if (!when) {
            return (SELF) this;
        }
        return this.select(columnName);
    }

    default SELF select(boolean when, String columnName, Function<IDatasetField, Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.select(columnName, f);
    }
}
