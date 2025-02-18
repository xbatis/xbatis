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

package db.sql.api.cmd.struct;

public interface IWhereIgnoreMethod<SELF> {
    /**
     * 构建条件时忽略null值
     *
     * @param bool 是否忽略
     * @return 当前类
     */
    SELF ignoreNullValueInCondition(boolean bool);

    /**
     * 构建条件时忽略空字符串
     *
     * @param bool 是否忽略
     * @return 当前类
     */
    SELF ignoreEmptyInCondition(boolean bool);

    /**
     * 构建条件时,是否对字符串进行trim
     *
     * @param bool 是否进行trim
     * @return 当前类
     */
    SELF trimStringInCondition(boolean bool);
}
