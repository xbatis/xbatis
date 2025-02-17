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

public interface INeGetterPredicateCompare<RV, V> {

    default <T, V2> RV ne(Getter<T> column, V2 value, Predicate<V2> predicate) {
        return this.ne(column, 1, value, predicate);
    }

    default <T, V2> RV ne(Getter<T> column, int storey, V2 value, Predicate<V2> predicate) {
        return ne(predicate.test(value), column, storey, (V) value);
    }

    <T> RV ne(boolean when, Getter<T> column, int storey, V value);
}