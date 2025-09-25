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

import db.sql.api.Getter;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.ICondition;

import java.util.function.Function;

public interface IConditionChainAnd<SELF extends IConditionChainAnd, TABLE_FIELD> {

    SELF and();

    SELF and(ICondition condition);

    default SELF and(boolean when, ICondition condition) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(condition);
    }

    default <T> SELF and(Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.and(true, column, f);
    }

    default <T> SELF and(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.and(when, column, 1, f);
    }

    default <T> SELF and(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        return this.and(true, column, storey, f);
    }

    <T> SELF and(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f);

    default SELF and(GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        return and(true, getterFields, f);
    }


    SELF and(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f);
}
