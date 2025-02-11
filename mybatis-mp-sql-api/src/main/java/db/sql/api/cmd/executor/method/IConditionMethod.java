/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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

import db.sql.api.Getter;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.executor.method.condition.IConditionMethods;
import db.sql.api.cmd.struct.Nested;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;


public interface IConditionMethod<SELF extends IConditionMethod,
        TABLE_FIELD,
        COLUMN,
        V,
        CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>
        >
        extends IConditionMethods<SELF, COLUMN, V>,
        Nested<SELF, CONDITION_CHAIN> {

    CONDITION_CHAIN conditionChain();

    default SELF and() {
        conditionChain().and();
        return (SELF) this;
    }

    default SELF or() {
        conditionChain().or();
        return (SELF) this;
    }


    default <T> SELF and(Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.and(column, 1, f);
    }

    default <T> SELF and(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(column, 1, f);
    }

    default <T> SELF and(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        conditionChain().and(column, storey, f);
        return (SELF) this;
    }

    default <T> SELF and(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(column, storey, f);
    }

    default <T> SELF or(Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.or(column, 1, f);
    }

    default <T> SELF or(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.or(column, 1, f);
    }

    default <T> SELF or(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        conditionChain().or(column, storey, f);
        return (SELF) this;
    }

    default <T> SELF or(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.or(column, storey, f);
    }

    default SELF and(GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        conditionChain().and(getterFields, f);
        return (SELF) this;
    }

    default SELF or(GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        conditionChain().or(getterFields, f);
        return (SELF) this;
    }

    default SELF and(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        conditionChain().and(when, getterFields, f);
        return (SELF) this;
    }

    default SELF or(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        conditionChain().or(when, getterFields, f);
        return (SELF) this;
    }

    default SELF and(Function<SELF, ICondition> f) {
        conditionChain().and(f.apply((SELF) this));
        return (SELF) this;
    }

    default SELF or(Function<SELF, ICondition> f) {
        conditionChain().or(f.apply((SELF) this));
        return (SELF) this;
    }

    default SELF and(ICondition condition) {
        return this.and(true, condition);
    }

    default SELF or(ICondition condition) {
        return this.or(true, condition);
    }

    default SELF and(boolean when, ICondition condition) {
        if (when && condition != null) {
            conditionChain().and(condition);
        }
        return (SELF) this;
    }

    default SELF or(boolean when, ICondition condition) {
        if (when && condition != null) {
            conditionChain().or(condition);
        }
        return (SELF) this;
    }

    @Override
    default SELF andNested(Consumer<CONDITION_CHAIN> consumer) {
        conditionChain().andNested(consumer);
        return (SELF) this;
    }

    @Override
    default SELF orNested(Consumer<CONDITION_CHAIN> consumer) {
        conditionChain().orNested(consumer);
        return (SELF) this;
    }

    @Override
    default SELF eq(COLUMN column, V value) {
        conditionChain().eq(column, value);
        return (SELF) this;
    }

    @Override
    default SELF ne(COLUMN column, V value) {
        conditionChain().ne(column, value);
        return (SELF) this;
    }

    @Override
    default SELF gt(COLUMN column, V value) {
        conditionChain().gt(column, value);
        return (SELF) this;
    }

    @Override
    default SELF gte(COLUMN column, V value) {
        conditionChain().gte(column, value);
        return (SELF) this;
    }

    @Override
    default SELF lt(COLUMN column, V value) {
        conditionChain().lt(column, value);
        return (SELF) this;
    }

    @Override
    default SELF lte(COLUMN column, V value) {
        conditionChain().lte(column, value);
        return (SELF) this;
    }

    @Override
    default SELF between(COLUMN column, V value, V value2) {
        conditionChain().between(column, value, value2);
        return (SELF) this;
    }

    @Override
    default SELF notBetween(COLUMN column, V value, V value2) {
        conditionChain().notBetween(column, value, value2);
        return (SELF) this;
    }

    @Override
    default SELF like(LikeMode mode, COLUMN column, String value) {
        conditionChain().like(mode, column, value);
        return (SELF) this;
    }

    @Override
    default SELF notLike(LikeMode mode, COLUMN column, String value) {
        conditionChain().notLike(mode, column, value);
        return (SELF) this;
    }

    @Override
    default SELF iLike(LikeMode mode, COLUMN column, String value) {
        conditionChain().iLike(mode, column, value);
        return (SELF) this;
    }

    @Override
    default SELF notILike(LikeMode mode, COLUMN column, String value) {
        conditionChain().notILike(mode, column, value);
        return (SELF) this;
    }

    @Override
    default SELF isNull(COLUMN column) {
        conditionChain().isNull(column);
        return (SELF) this;
    }

    @Override
    default SELF isNotNull(COLUMN column) {
        conditionChain().isNotNull(column);
        return (SELF) this;
    }

    @Override
    default <T> SELF empty(boolean when, Getter<T> column, int storey) {
        conditionChain().empty(when, column, storey);
        return (SELF) this;
    }

    @Override
    default <T> SELF notEmpty(boolean when, Getter<T> column, int storey) {
        conditionChain().notEmpty(when, column, storey);
        return (SELF) this;
    }

    @Override
    default SELF empty(COLUMN column) {
        conditionChain().empty(column);
        return (SELF) this;
    }

    @Override
    default SELF notEmpty(COLUMN column) {
        conditionChain().empty(column);
        return (SELF) this;
    }

    @Override
    default <T> SELF eq(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().eq(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF eq(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().eq(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }

    @Override
    default <T> SELF ne(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().ne(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF ne(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().ne(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }

    @Override
    default <T> SELF gt(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().gt(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF gt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().gt(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }

    @Override
    default <T> SELF gte(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().gte(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF gte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().gte(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }

    @Override
    default <T> SELF lt(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().lt(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF lt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().lt(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }

    @Override
    default <T> SELF lte(boolean when, Getter<T> column, int storey, V value) {
        conditionChain().lte(when, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T, T2> SELF lte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        conditionChain().lte(when, column, columnStorey, value, valueStorey);
        return (SELF) this;
    }


    @Override
    default <T> SELF like(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        conditionChain().like(when, mode, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T> SELF notLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        conditionChain().notLike(when, mode, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T> SELF iLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        conditionChain().iLike(when, mode, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T> SELF notILike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        conditionChain().notILike(when, mode, column, storey, value);
        return (SELF) this;
    }

    @Override
    default <T> SELF between(boolean when, Getter<T> column, int storey, V value, V value2) {
        conditionChain().between(when, column, storey, value, value2);
        return (SELF) this;
    }

    @Override
    default <T> SELF notBetween(boolean when, Getter<T> column, int storey, V value, V value2) {
        conditionChain().notBetween(when, column, storey, value, value2);
        return (SELF) this;
    }

    @Override
    default <T> SELF isNull(boolean when, Getter<T> column, int storey) {
        conditionChain().isNull(when, column, storey);
        return (SELF) this;
    }

    @Override
    default <T> SELF isNotNull(boolean when, Getter<T> column, int storey) {
        conditionChain().isNotNull(when, column, storey);
        return (SELF) this;
    }

    @Override
    default SELF in(COLUMN column, IQuery query) {
        conditionChain().in(column, query);
        return (SELF) this;
    }

    @Override
    default SELF in(COLUMN column, Serializable... values) {
        conditionChain().in(column, values);
        return (SELF) this;
    }

    @Override
    default SELF in(COLUMN column, Collection<? extends Serializable> values) {
        conditionChain().in(column, values);
        return (SELF) this;
    }

    @Override
    default <T> SELF in(boolean when, Getter<T> column, int storey, IQuery query) {
        conditionChain().in(when, column, storey, query);
        return (SELF) this;
    }

    @Override
    default <T> SELF in(boolean when, Getter<T> column, int storey, Serializable[] values) {
        conditionChain().in(when, column, storey, values);
        return (SELF) this;
    }

    @Override
    default <T> SELF in(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        conditionChain().in(when, column, storey, values);
        return (SELF) this;
    }

    @Override
    default SELF exists(boolean when, IQuery query) {
        conditionChain().exists(when, query);
        return (SELF) this;
    }

    @Override
    default SELF notExists(boolean when, IQuery query) {
        conditionChain().notExists(when, query);
        return (SELF) this;
    }

    @Override
    default SELF notIn(COLUMN column, IQuery query) {
        conditionChain().notIn(column, query);
        return (SELF) this;
    }

    @Override
    default SELF notIn(COLUMN column, Serializable... values) {
        conditionChain().notIn(column, values);
        return (SELF) this;
    }

    @Override
    default SELF notIn(COLUMN column, Collection<? extends Serializable> values) {
        conditionChain().notIn(column, values);
        return (SELF) this;
    }

    @Override
    default <T> SELF notIn(boolean when, Getter<T> column, int storey, IQuery query) {
        conditionChain().notIn(when, column, storey, query);
        return (SELF) this;
    }

    @Override
    default <T> SELF notIn(boolean when, Getter<T> column, int storey, Serializable[] values) {
        conditionChain().notIn(when, column, storey, values);
        return (SELF) this;
    }

    @Override
    default <T> SELF notIn(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        conditionChain().notIn(when, column, storey, values);
        return (SELF) this;
    }
}
