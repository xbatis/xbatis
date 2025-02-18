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

public interface IGteGetterCompare<RV, V> {

    default <T> RV gte(Getter<T> column, V value) {
        return gte(true, column, 1, value);
    }

    default <T> RV gte(boolean when, Getter<T> column, V value) {
        return this.gte(when, column, 1, value);
    }

    default <T> RV gte(Getter<T> column, int storey, V value) {
        return gte(true, column, storey, value);
    }

    <T> RV gte(boolean when, Getter<T> column, int storey, V value);

    default <T, T2> RV gte(Getter<T> column, Getter<T2> value) {
        return gte(true, column, value);
    }

    default <T, T2> RV gte(boolean when, Getter<T> column, Getter<T2> value) {
        return this.gte(when, column, 1, value, 1);
    }

    default <T, T2> RV gte(Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        return gte(true, column, columnStorey, value, valueStorey);
    }

    <T, T2> RV gte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey);
}
