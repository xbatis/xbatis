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

import db.sql.api.Cmd;
import db.sql.api.cmd.executor.method.IWhereMethod;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;

public interface IWhere<SELF extends IWhere, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>> extends IWhereMethod<SELF, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>, Cmd {
    /**
     * 为搜索（注意查询和搜索是不一样的）
     *
     * @return 自己
     */
    default SELF forSearch() {
        return this.forSearch(true);
    }

    /**
     * 为搜索（注意查询和搜索是不一样的）
     *
     * @param bool 开关
     * @return 自己
     */
    default SELF forSearch(boolean bool) {
        this.ignoreNullValueInCondition(bool);
        this.ignoreEmptyInCondition(bool);
        this.trimStringInCondition(bool);
        return (SELF) this;
    }
}
