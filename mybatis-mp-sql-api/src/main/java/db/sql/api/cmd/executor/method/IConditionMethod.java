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


    default <T> SELF and(Getter<T> column, Function<TABLE_FIELD, ICondition> function) {
        return this.and(column, 1, function);
    }

    default <T> SELF and(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> function) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(column, 1, function);
    }

    default <T> SELF and(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> function) {
        conditionChain().and(column, storey, function);
        return (SELF) this;
    }

    default <T> SELF and(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> function) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(column, storey, function);
    }

    default <T> SELF or(Getter<T> column, Function<TABLE_FIELD, ICondition> function) {
        return this.or(column, 1, function);
    }

    default <T> SELF or(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> function) {
        if (!when) {
            return (SELF) this;
        }
        return this.or(column, 1, function);
    }

    default <T> SELF or(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> function) {
        conditionChain().or(column, storey, function);
        return (SELF) this;
    }

    default <T> SELF or(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> function) {
        if (!when) {
            return (SELF) this;
        }
        return this.or(column, storey, function);
    }

    default <T> SELF and(Function<TABLE_FIELD[], ICondition> function, Getter<T>... columns) {
        return this.and(function, 1, columns);
    }

    default <T> SELF and(boolean when, Function<TABLE_FIELD[], ICondition> function, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.and(function, 1, columns);
    }

    default <T> SELF and(Function<TABLE_FIELD[], ICondition> function, int storey, Getter<T>... columns) {
        conditionChain().and(function, storey, columns);
        return (SELF) this;
    }

    default <T> SELF and(boolean when, Function<TABLE_FIELD[], ICondition> function, int storey, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        conditionChain().and(function, storey, columns);
        return (SELF) this;
    }

    default <T> SELF or(Function<TABLE_FIELD[], ICondition> function, Getter<T>... columns) {
        return this.or(function, 1, columns);
    }

    default <T> SELF or(boolean when, Function<TABLE_FIELD[], ICondition> function, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.or(function, 1, columns);
    }

    default <T> SELF or(Function<TABLE_FIELD[], ICondition> function, int storey, Getter<T>... columns) {
        conditionChain().or(function, storey, columns);
        return (SELF) this;
    }

    default <T> SELF or(boolean when, Function<TABLE_FIELD[], ICondition> function, int storey, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        conditionChain().or(function, storey, columns);
        return (SELF) this;
    }

    default SELF and(Function<TABLE_FIELD[], ICondition> function, GetterField... getterFields) {
        conditionChain().and(function, getterFields);
        return (SELF) this;
    }

    default SELF or(Function<TABLE_FIELD[], ICondition> function, GetterField... getterFields) {
        conditionChain().or(function, getterFields);
        return (SELF) this;
    }

    default SELF and(boolean when, Function<TABLE_FIELD[], ICondition> function, GetterField... getterFields) {
        conditionChain().and(when, function, getterFields);
        return (SELF) this;
    }

    default SELF or(boolean when, Function<TABLE_FIELD[], ICondition> function, GetterField... getterFields) {
        conditionChain().or(when, function, getterFields);
        return (SELF) this;
    }

    default SELF and(Function<SELF, ICondition> function) {
        conditionChain().and(function.apply((SELF) this));
        return (SELF) this;
    }

    default SELF or(Function<SELF, ICondition> function) {
        conditionChain().or(function.apply((SELF) this));
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
    default SELF between(COLUMN column, Serializable value, Serializable value2) {
        conditionChain().between(column, value, value2);
        return (SELF) this;
    }

    @Override
    default SELF notBetween(COLUMN column, Serializable value, Serializable value2) {
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
    default <T> SELF between(boolean when, Getter<T> column, int storey, Serializable value, Serializable value2) {
        conditionChain().between(when, column, storey, value, value2);
        return (SELF) this;
    }

    @Override
    default <T> SELF notBetween(boolean when, Getter<T> column, int storey, Serializable value, Serializable value2) {
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
