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

package db.sql.api.impl.cmd.basic;

import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.impl.tookit.SqlConst;

public abstract class AbstractDataset<T extends AbstractDataset<T, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> implements IDataset<T, DATASET_FIELD> {

    @Override
    public DATASET_FIELD $(String name) {
        return (DATASET_FIELD) new DatasetField(this, name);
    }

    /**
     * 就是 *
     *
     * @return DatasetField
     */
    public DatasetField all() {
        return new DatasetField(this, SqlConst.ALL);
    }
}
