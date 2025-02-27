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

import db.sql.api.Getter;

public interface IConflictUpdate<T> {

    /**
     * 设置值
     *
     * @param field
     * @param value
     * @return
     */
    IConflictUpdate<T> set(Getter<T> field, Object value);

    /**
     * 覆盖字段
     *
     * @param fields
     * @return
     */
    IConflictUpdate<T> overwrite(Getter<T>... fields);

    /**
     * 覆盖所有修改字段
     * 除主键外
     *
     * @return
     */
    IConflictUpdate<T> overwriteAll();

    /**
     * 忽略字段
     *
     * @param fields
     * @return
     */
    IConflictUpdate<T> ignore(Getter<T>... fields);


}
