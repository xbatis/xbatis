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


import db.sql.api.Getter;
import db.sql.api.cmd.executor.IQuery;

public interface IExistsMethod<RV> {

    default RV exists(IQuery query) {
        return this.exists(true, query);
    }

    RV exists(boolean when, IQuery query);

    default <T1, T2> RV exists(Getter<T1> sourceGetter, Getter<T2> targetGetter) {
        return this.exists(sourceGetter, 1, targetGetter);
    }

    default <T1, T2> RV exists(boolean when, Getter<T1> sourceGetter, Getter<T2> targetGetter) {
        return this.exists(when, sourceGetter, 1, targetGetter);
    }

    default <T1, T2> RV exists(Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter) {
        return this.exists(true, sourceGetter, sourceStorey, targetGetter);
    }

    <T1, T2> RV exists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter);

    default RV notExists(IQuery query) {
        return this.notExists(true, query);
    }

    RV notExists(boolean when, IQuery query);

    default <T1, T2> RV notExists(Getter<T1> sourceGetter, Getter<T2> targetGetter) {
        return this.notExists(sourceGetter, 1, targetGetter);
    }

    default <T1, T2> RV notExists(boolean when, Getter<T1> sourceGetter, Getter<T2> targetGetter) {
        return this.notExists(when, sourceGetter, 1, targetGetter);
    }

    default <T1, T2> RV notExists(Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter) {
        return this.notExists(true, sourceGetter, sourceStorey, targetGetter);
    }

    <T1, T2> RV notExists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter);
}
