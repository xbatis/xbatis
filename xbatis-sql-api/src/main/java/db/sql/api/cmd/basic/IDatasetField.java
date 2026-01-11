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

package db.sql.api.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.IDbType;

public interface IDatasetField<T extends IDatasetField<T>> extends IField<T>, Cmd {

    /**
     * 数据集对象 表或子表
     *
     * @return IDataset
     */
    IDataset getTable();

    /**
     * 字段名字
     *
     * @return 列名
     */
    String getName();

    /**
     * 根据dbType 返回处理后的列
     *
     * @param dbType 数据库类型
     * @return 列名
     */
    String getName(IDbType dbType);

}
