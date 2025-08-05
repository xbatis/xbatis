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

package db.sql.api.cmd.executor.method;

import db.sql.api.cmd.struct.IWhereIgnoreMethod;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;

public interface IWhereMethod<SELF extends IWhereMethod,
        TABLE_FIELD,
        COLUMN,
        V,
        CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>
        >
        extends IConditionMethod<SELF, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        IWhereIgnoreMethod<SELF> {

    @Override
    default SELF ignoreNullValueInCondition(boolean bool) {
        conditionChain().setIgnoreNull(bool);
        return (SELF) this;
    }

    @Override
    default SELF ignoreEmptyInCondition(boolean bool) {
        conditionChain().setIgnoreEmpty(bool);
        return (SELF) this;
    }

    @Override
    default SELF trimStringInCondition(boolean bool) {
        conditionChain().setStringTrim(bool);
        return (SELF) this;
    }
}
