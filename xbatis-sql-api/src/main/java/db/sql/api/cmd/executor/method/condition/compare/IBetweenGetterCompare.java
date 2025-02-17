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

import java.util.function.Supplier;

public interface IBetweenGetterCompare<RV, V> {

    default <T> RV between(Getter<T> column, V value, V value2) {
        return between(true, column, 1, value, value2);
    }

    default <T> RV between(boolean when, Getter<T> column, V value, V value2) {
        return this.between(when, column, 1, value, value2);
    }

    default <T> RV between(Getter<T> column, int storey, V value, V value2) {
        return between(true, column, storey, value, value2);
    }

    <T> RV between(boolean when, Getter<T> column, int storey, V value, V value2);


    default <T> RV between(Getter<T> column, V[] values) {
        return between(true, column, 1, values);
    }

    default <T> RV between(boolean when, Getter<T> column, V[] values) {
        return this.between(when, column, 1, values);
    }

    default <T> RV between(Getter<T> column, int storey, V[] values) {
        return between(true, column, storey, values);
    }

    default <T> RV between(boolean when, Getter<T> column, int storey, V[] values) {
        return this.between(when, column, storey, values[0], values[1]);
    }

    default <T> RV between(Getter<T> column, Supplier<V> value1Supplier, Supplier<V> value2Supplier) {
        return between(column, 1, value1Supplier, value2Supplier);
    }

    default <T> RV between(Getter<T> column, int storey, Supplier<V> value1Supplier, Supplier<V> value2Supplier) {
        return this.between(true, column, storey, value1Supplier, value2Supplier);
    }

    default <T> RV between(boolean when, Getter<T> column, int storey, Supplier<V> value1Supplier, Supplier<V> value2Supplier) {
        if (!when) {
            return (RV) this;
        }
        return this.between(true, column, storey, value1Supplier.get(), value2Supplier.get());
    }
}
