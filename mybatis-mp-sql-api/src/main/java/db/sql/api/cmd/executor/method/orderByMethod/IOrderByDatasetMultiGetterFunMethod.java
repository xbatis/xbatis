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


import db.sql.api.Cmd;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.basic.IOrderByDirection;

import java.util.function.Function;

public interface IOrderByDatasetMultiGetterFunMethod<SELF extends IOrderByDatasetMultiGetterFunMethod> extends IBaseOrderByMethods {

    <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, GetterField[] getterFields, Function<IDatasetField[], Cmd> f);

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        return this.orderBy(dataset, ascOrderByDirection(), getterFields, f);
    }

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(dataset, ascOrderByDirection(), getterFields, f);
    }

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderByDesc(IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        return this.orderBy(dataset, descOrderByDirection(), getterFields, f);
    }

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderByDesc(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(dataset, descOrderByDirection(), getterFields, f);
    }

    default <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(dataset, orderByDirection, getterFields, f);
    }


}
