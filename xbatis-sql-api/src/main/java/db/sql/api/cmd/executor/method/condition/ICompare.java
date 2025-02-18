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

    RV empty(COLUMN column);

    RV notEmpty(COLUMN column);

    RV eq(COLUMN column, V value);

    RV ne(COLUMN column, V value);

    RV gt(COLUMN column, V value);

    RV gte(COLUMN column, V value);

    RV lt(COLUMN column, V value);

    RV lte(COLUMN column, V value);

    default RV like(COLUMN column, String value) {
        return this.like(LikeMode.DEFAULT, column, value);
    }

    RV like(LikeMode mode, COLUMN column, String value);

    default RV notLike(COLUMN column, String value) {
        return this.notLike(LikeMode.DEFAULT, column, value);
    }

    RV notLike(LikeMode mode, COLUMN column, String value);

    default RV iLike(COLUMN column, String value) {
        return this.iLike(LikeMode.DEFAULT, column, value);
    }

    RV iLike(LikeMode mode, COLUMN column, String value);

    default RV notILike(COLUMN column, String value) {
        return this.notILike(LikeMode.DEFAULT, column, value);
    }

    RV notILike(LikeMode mode, COLUMN column, String value);

    RV between(COLUMN column, V value, V value2);

    RV notBetween(COLUMN column, V value, V value2);

    RV isNull(COLUMN column);

    RV isNotNull(COLUMN column);
}
