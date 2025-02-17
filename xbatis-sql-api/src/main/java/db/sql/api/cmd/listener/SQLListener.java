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

package db.sql.api.cmd.listener;

import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.struct.IOn;

public interface SQLListener {

    /**
     * 插入时的事件
     *
     * @param source
     * @param dataset
     */
    default void onInsert(Object source, IDataset<?, ?> dataset) {

    }

    /**
     * 修改时的事件
     *
     * @param source
     * @param dataset
     */
    default void onUpdate(Object source, IDataset<?, ?> dataset) {

    }

    /**
     * 删除时的事件
     *
     * @param source
     * @param dataset
     */
    default void onDelete(Object source, IDataset<?, ?> dataset) {

    }

    /**
     * TRUNCATE TABLE 时的事件
     *
     * @param dataset
     */
    default void onTruncate(IDataset<?, ?> dataset) {

    }

    /**
     * query调用from时的事件
     *
     * @param source
     * @param dataset
     */
    default void onFrom(Object source, IDataset<?, ?> dataset) {

    }

    /**
     * 调用join时的事件
     *
     * @param source
     * @param mode
     * @param mainTable
     * @param secondTable
     * @param on
     */
    default void onJoin(Object source, JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, IOn<?, ?, ?, ?, ?, ?, ?> on) {

    }
}
