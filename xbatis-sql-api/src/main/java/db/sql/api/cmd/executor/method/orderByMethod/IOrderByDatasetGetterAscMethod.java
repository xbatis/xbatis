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


import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;

public interface IOrderByDatasetGetterAscMethod<SELF extends IOrderByDatasetGetterAscMethod> extends IOrderByDatasetGetterMethod<SELF> {

    default <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderByAsc(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column) {
        return this.orderBy(dataset, ascOrderByDirection(), column);
    }

    default <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderByAsc(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(dataset, ascOrderByDirection(), column);
    }
}
