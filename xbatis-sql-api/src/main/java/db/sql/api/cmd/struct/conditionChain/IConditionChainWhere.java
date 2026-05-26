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

package db.sql.api.cmd.struct.conditionChain;

import db.sql.api.Getter;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.ICondition;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IConditionChainWhere<SELF extends IConditionChainWhere, TABLE_FIELD> {

    SELF where(ICondition condition);

    default SELF where(boolean when, Consumer<SELF> consumer) {
        if (when) {
            consumer.accept((SELF) this);
        }
        return (SELF) this;
    }

    default SELF where(boolean when, ICondition condition) {
        if (!when) {
            return (SELF) this;
        }
        return this.where(condition);
    }

    default <T> SELF where(Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.where(true, column, f);
    }

    default <T> SELF where(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.where(when, column, 1, f);
    }

    default <T> SELF where(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        return this.where(true, column, storey, f);
    }

    <T> SELF where(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f);

    default SELF where(GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        return where(true, getterFields, f);
    }

    SELF where(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f);
}
