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

package db.sql.api.cmd.struct.conditionChain;


import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.executor.method.condition.IConditionMethods;
import db.sql.api.cmd.struct.Nested;

import java.util.function.Consumer;


public interface IConditionChain<SELF extends IConditionChain,
        TABLE_FIELD,
        COLUMN,
        V>

        extends IConditionMethods<SELF, COLUMN, V>,
        Nested<SELF, SELF>,
        IConditionChainAnd<SELF, TABLE_FIELD>,
        IConditionChainOr<SELF, TABLE_FIELD>,
        ICondition {

    SELF setIgnoreEmpty(boolean bool);

    SELF setIgnoreNull(boolean bool);

    SELF setStringTrim(boolean bool);

    boolean hasContent();

    SELF newInstance();

    @Override
    default SELF andNested(Consumer<SELF> consumer) {
        SELF newSelf = newInstance();
        consumer.accept(newSelf);
        this.and(newSelf.hasContent(), newSelf);
        return (SELF) this;
    }

    @Override
    default SELF orNested(Consumer<SELF> consumer) {
        SELF newSelf = newInstance();
        consumer.accept(newSelf);
        this.or(newSelf.hasContent(), newSelf);
        return (SELF) this;
    }
}
