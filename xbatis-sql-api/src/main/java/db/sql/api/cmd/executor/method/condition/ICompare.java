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

package db.sql.api.cmd.executor.method.condition;

import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.executor.method.condition.compare.*;

import java.util.function.Supplier;

/**
 * 比较器
 *
 * @param <RV>     返回
 * @param <COLUMN> 列
 * @param <V>      比较值
 */
public interface ICompare<RV, COLUMN, V> extends
        IEqGetterCompare<RV, V>,
        IEqGetterPredicateCompare<RV, V>,
        INeGetterCompare<RV, V>,
        INeGetterPredicateCompare<RV, V>,
        IGtGetterCompare<RV, V>,
        IGtGetterPredicateCompare<RV, V>,
        IGteGetterCompare<RV, V>,
        IGteGetterPredicateCompare<RV, V>,
        ILtGetterCompare<RV, V>,
        ILtGetterPredicateCompare<RV, V>,
        ILteGetterCompare<RV, V>,
        ILteGetterPredicateCompare<RV, V>,
        ILikeGetterCompare<RV>,
        ILikeGetterPredicateCompare<RV>,
        INotLikeGetterCompare<RV>,
        INotLikeGetterPredicateCompare<RV>,
        IILikeGetterCompare<RV>,
        IILikeGetterPredicateCompare<RV>,
        INotILikeGetterCompare<RV>,
        INotILikeGetterPredicateCompare<RV>,
        IBetweenGetterCompare<RV, V>,
        IBetweenGetterPredicateCompare<RV, V>,
        INotBetweenGetterCompare<RV, V>,
        INotBetweenGetterPredicateCompare<RV, V>,
        IIsNullGetterCompare<RV>,
        IIsNotNullGetterCompare<RV>,
        IEmptyGetterCompare<RV>,
        INotEmptyGetterCompare<RV> {

    default RV $conditionWhenFalseRV() {
        return (RV) this;
    }

    RV empty(COLUMN column);

    default RV empty(boolean when, COLUMN column) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.empty(column);
    }

    default RV empty(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return $conditionWhenFalseRV();
        }
        return this.empty(column);
    }

    default RV empty(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.empty(supplier);
    }

    RV notEmpty(COLUMN column);

    default RV notEmpty(boolean when, COLUMN column) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notEmpty(column);
    }

    default RV notEmpty(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return $conditionWhenFalseRV();
        }
        return this.notEmpty(column);
    }

    default RV notEmpty(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notEmpty(supplier);
    }

    RV eq(COLUMN column, V value);

    default RV eq(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.eq(column, value);
    }

    RV ne(COLUMN column, V value);

    default RV ne(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.ne(column, value);
    }

    RV gt(COLUMN column, V value);

    default RV gt(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.gt(column, value);
    }

    RV gte(COLUMN column, V value);

    default RV gte(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.gte(column, value);
    }

    RV lt(COLUMN column, V value);

    default RV lt(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.lt(column, value);
    }

    RV lte(COLUMN column, V value);

    default RV lte(boolean when, COLUMN column, V value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.lte(column, value);
    }

    default RV like(COLUMN column, String value) {
        return this.like(LikeMode.DEFAULT, column, value);
    }

    default RV like(boolean when, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.like(column, value);
    }

    RV like(LikeMode mode, COLUMN column, String value);

    default RV like(boolean when, LikeMode mode, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.like(mode, column, value);
    }

    default RV notLike(COLUMN column, String value) {
        return this.notLike(LikeMode.DEFAULT, column, value);
    }

    default RV notLike(boolean when, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notLike(column, value);
    }

    RV notLike(LikeMode mode, COLUMN column, String value);

    default RV notLike(boolean when, LikeMode mode, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notLike(mode, column, value);
    }

    default RV iLike(COLUMN column, String value) {
        return this.iLike(LikeMode.DEFAULT, column, value);
    }

    default RV iLike(boolean when, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.iLike(column, value);
    }

    RV iLike(LikeMode mode, COLUMN column, String value);

    default RV iLike(boolean when, LikeMode mode, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.iLike(mode, column, value);
    }

    default RV notILike(COLUMN column, String value) {
        return this.notILike(LikeMode.DEFAULT, column, value);
    }

    default RV notILike(boolean when, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notILike(column, value);
    }

    RV notILike(LikeMode mode, COLUMN column, String value);

    default RV notILike(boolean when, LikeMode mode, COLUMN column, String value) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notILike(mode, column, value);
    }

    RV between(COLUMN column, V value, V value2);

    default RV between(boolean when, COLUMN column, V value, V value2) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.between(column, value, value2);
    }

    RV notBetween(COLUMN column, V value, V value2);

    default RV notBetween(boolean when, COLUMN column, V value, V value2) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.notBetween(column, value, value2);
    }

    RV isNull(COLUMN column);

    default RV isNull(boolean when, COLUMN column) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.isNull(column);
    }

    default RV isNull(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return $conditionWhenFalseRV();
        }
        return this.isNull(column);
    }

    default RV isNull(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.isNull(supplier);
    }

    RV isNotNull(COLUMN column);

    default RV isNotNull(boolean when, COLUMN column) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.isNotNull(column);
    }

    default RV isNotNull(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return $conditionWhenFalseRV();
        }
        return this.isNull(column);
    }

    default RV isNotNull(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return $conditionWhenFalseRV();
        }
        return this.isNotNull(supplier);
    }
}
