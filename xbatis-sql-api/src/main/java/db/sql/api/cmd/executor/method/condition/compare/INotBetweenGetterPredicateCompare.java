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

import java.util.function.Predicate;

public interface INotBetweenGetterPredicateCompare<RV, V> extends INotBetweenGetterCompare<RV, V> {

    default <T> RV notBetween(Getter<T> column, V value, V value2, Predicate<V> predicate) {
        return notBetween(column, 1, value, value2, predicate);
    }

    default <T> RV notBetween(Getter<T> column, int storey, V value, V value2, Predicate<V> predicate) {
        return this.notBetween(predicate.test(value) && predicate.test(value2), column, storey, value, value2);
    }

    <T> RV notBetween(boolean when, Getter<T> column, int storey, V value, V value2);

    default <T> RV notBetween(Getter<T> column, V[] values, Predicate<V[]> predicate) {
        return notBetween(column, 1, values, predicate);
    }

    default <T> RV notBetween(Getter<T> column, int storey, V[] values, Predicate<V[]> predicate) {
        return this.notBetween(predicate.test(values), column, storey, values);
    }

    default <T> RV notBetween(boolean when, Getter<T> column, int storey, V[] values) {
        if (!when) {
            return (RV) this;
        }
        return this.notBetween(true, column, storey, values[0], values[1]);
    }
}
