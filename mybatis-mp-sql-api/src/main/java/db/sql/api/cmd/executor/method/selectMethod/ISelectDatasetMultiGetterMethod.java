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
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;

import java.util.function.Function;

public interface ISelectDatasetMultiGetterMethod<SELF extends ISelectDatasetMultiGetterMethod> {

    <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns);

    default <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.select(dataset, columns);
    }

    <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f);

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.select(dataset, getterFields, f);
    }
}
