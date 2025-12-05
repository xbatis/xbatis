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

package db.sql.api.cmd.executor.method.condition.compare;

import db.sql.api.Getter;
import db.sql.api.cmd.LikeMode;

public interface INotLikeGetterCompare<RV, V> {

    default <T> RV notLike(Getter<T> column, V value) {
        return notLike(true, LikeMode.DEFAULT, column, 1, value);
    }

    default <T> RV notLike(boolean when, Getter<T> column, V value) {
        return this.notLike(when, LikeMode.DEFAULT, column, 1, value);
    }

    default <T> RV notLike(Getter<T> column, V value, int storey) {
        return notLike(true, column, storey, value);
    }

    default <T> RV notLike(boolean when, Getter<T> column, int storey, V value) {
        return this.notLike(when, LikeMode.DEFAULT, column, storey, value);
    }

    default <T> RV notLike(LikeMode mode, Getter<T> column, V value) {
        return notLike(mode, column, 1, value);
    }

    default <T> RV notLike(boolean when, LikeMode mode, Getter<T> column, V value) {
        return this.notLike(when, mode, column, 1, value);
    }

    default <T> RV notLike(LikeMode mode, Getter<T> column, int storey, V value) {
        return this.notLike(true, mode, column, storey, value);
    }

    <T> RV notLike(boolean when, LikeMode mode, Getter<T> column, int storey, V value);
}
