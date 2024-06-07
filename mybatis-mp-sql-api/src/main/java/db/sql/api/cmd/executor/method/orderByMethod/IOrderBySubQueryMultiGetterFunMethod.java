package db.sql.api.cmd.executor.method.orderByMethod;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.IColumnField;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.executor.ISubQuery;

import java.util.function.Function;

public interface IOrderBySubQueryMultiGetterFunMethod<SELF extends IOrderBySubQueryMultiGetterFunMethod, DATASET_FILED extends Cmd> extends IBaseOrderByMethods {

    default <T> SELF orderByWithFun(ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns) {
        return this.orderByWithFun(subQuery, ascOrderByDirection(), f, columns);
    }

    default <T> SELF orderByDescWithFun(ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns) {
        return this.orderByWithFun(subQuery, descOrderByDirection(), f, columns);
    }

    default SELF orderByWithFun(ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields) {
        return this.orderByWithFun(subQuery, ascOrderByDirection(), f, columnFields);
    }

    default SELF orderByDescWithFun(ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields) {
        return this.orderByWithFun(subQuery, descOrderByDirection(), f, columnFields);
    }

    default <T> SELF orderByWithFun(boolean when, ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, ascOrderByDirection(), f, columns);
    }

    default <T> SELF orderByDescWithFun(boolean when, ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, descOrderByDirection(), f, columns);
    }

    default SELF orderByWithFun(boolean when, ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, ascOrderByDirection(), f, columnFields);
    }

    default SELF orderByDescWithFun(boolean when, ISubQuery subQuery, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, descOrderByDirection(), f, columnFields);
    }

    <T> SELF orderByWithFun(ISubQuery subQuery, IOrderByDirection orderByDirection, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns);

    SELF orderByWithFun(ISubQuery subQuery, IOrderByDirection orderByDirection, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields);

    default <T> SELF orderByWithFun(boolean when, ISubQuery subQuery, IOrderByDirection orderByDirection, Function<DATASET_FILED[], Cmd> f, Getter<T>... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, orderByDirection, f, columns);
    }

    default SELF orderByWithFun(boolean when, ISubQuery subQuery, IOrderByDirection orderByDirection, Function<DATASET_FILED[], Cmd> f, IColumnField... columnFields) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByWithFun(subQuery, orderByDirection, f, columnFields);
    }
}
