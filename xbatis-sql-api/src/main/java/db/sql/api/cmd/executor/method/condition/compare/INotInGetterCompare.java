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
import db.sql.api.cmd.executor.IQuery;

import java.io.Serializable;
import java.util.Collection;

public interface INotInGetterCompare<RV> {

    default <T> RV notIn(Getter<T> column, IQuery query) {
        return this.notIn(true, column, 1, query);
    }

    default <T> RV notIn(boolean when, Getter<T> column, IQuery query) {
        return this.notIn(when, column, 1, query);
    }

    default <T> RV notIn(Getter<T> column, int storey, IQuery query) {
        return this.notIn(true, column, storey, query);
    }

    <T> RV notIn(boolean when, Getter<T> column, int storey, IQuery query);

    default <T> RV notIn(Getter<T> column, Serializable... values) {
        return this.notIn(true, column, 1, values);
    }

    default <T> RV notIn(boolean when, Getter<T> column, Serializable... values) {
        return this.notIn(when, column, 1, values);
    }

    default <T> RV notIn(Getter<T> column, int storey, Serializable[] values) {
        return this.notIn(true, column, storey, values);
    }

    <T> RV notIn(boolean when, Getter<T> column, int storey, Serializable[] values);

    default <T> RV notIn(Getter<T> column, Collection<? extends Serializable> values) {
        return this.notIn(true, column, 1, values);
    }

    default <T> RV notIn(boolean when, Getter<T> column, Collection<? extends Serializable> values) {
        return this.notIn(when, column, 1, values);
    }

    default <T> RV notIn(Getter<T> column, int storey, Collection<? extends Serializable> values) {
        return this.notIn(true, column, storey, values);
    }

    <T> RV notIn(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values);
}
