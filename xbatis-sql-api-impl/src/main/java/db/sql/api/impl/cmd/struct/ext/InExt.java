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

package db.sql.api.impl.cmd.struct.ext;

import db.sql.api.Getter;
import db.sql.api.cmd.executor.method.condition.compare.IInGetterCompare;
import db.sql.api.impl.cmd.executor.AbstractSubQuery;

import java.util.function.BiConsumer;

public interface InExt<T, SUBQUERY extends AbstractSubQuery<?, ?>> extends IInGetterCompare<T> {

    <E> SUBQUERY buildInOrNotInSubQuery(Getter<E> selectGetter, BiConsumer<T, SUBQUERY> consumer);

    <E1, E2> SUBQUERY buildInOrNotInSubQuery(Getter<E2> selectGetter, Getter<E1> sourceEqGetter, int sourceStorey, Getter<E2> targetEqGetter, BiConsumer<T, SUBQUERY> consumer);

    default <E1, E2> T in(Getter<E1> sourceGetter, Getter<E2> selectGetter) {
        return this.in(true, sourceGetter, selectGetter, null);
    }

    default <E1, E2> T in(Getter<E1> sourceGetter, Getter<E2> selectGetter, BiConsumer<T, SUBQUERY> consumer) {
        return this.in(true, sourceGetter, selectGetter, consumer);
    }

    default <E1, E2> T in(boolean when, Getter<E1> sourceGetter, Getter<E2> selectGetter, BiConsumer<T, SUBQUERY> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.in(sourceGetter, this.buildInOrNotInSubQuery(selectGetter, consumer));
    }

    default <E1, E2> T in(Getter<E1> sourceGetter, int sourceStorey, Getter<E2> selectGetter, BiConsumer<T, SUBQUERY> consumer) {
        return this.in(true, sourceGetter, sourceStorey, selectGetter, consumer);
    }

    default <E1, E2> T in(boolean when, Getter<E1> sourceGetter, int sourceStorey, Getter<E2> selectGetter, BiConsumer<T, SUBQUERY> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.in(sourceGetter, sourceStorey, this.buildInOrNotInSubQuery(selectGetter, consumer));
    }

    //--------------------------4个getter字段的in-------------------------------
    default <E1, E2> T in(Getter<E1> sourceGetter, Getter<E2> selectGetter, Getter<E1> sourceEqGetter, Getter<E2> targetEqGetter) {
        return this.in(true, sourceGetter, selectGetter, sourceEqGetter, targetEqGetter, null);
    }

    default <E1, E2> T in(Getter<E1> sourceGetter, Getter<E2> selectGetter, Getter<E1> sourceEqGetter, Getter<E2> targetEqGetter, BiConsumer<T, SUBQUERY> consumer) {
        return this.in(true, sourceGetter, selectGetter, sourceEqGetter, targetEqGetter, consumer);
    }

    default <E1, E2> T in(boolean when, Getter<E1> sourceGetter, Getter<E2> selectGetter, Getter<E1> sourceEqGetter, Getter<E2> targetEqGetter, BiConsumer<T, SUBQUERY> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.in(sourceGetter, this.buildInOrNotInSubQuery(selectGetter, sourceEqGetter, 1, targetEqGetter, consumer));
    }

    default <E1, E2> T in(Getter<E1> sourceGetter, int sourceStorey, Getter<E2> selectGetter, Getter<E1> sourceEqGetter, Getter<E2> targetEqGetter, BiConsumer<T, SUBQUERY> consumer) {
        return this.in(true, sourceGetter, sourceStorey, selectGetter, sourceEqGetter, targetEqGetter, consumer);
    }

    default <E1, E2> T in(boolean when, Getter<E1> sourceGetter, int sourceStorey, Getter<E2> selectGetter, Getter<E1> sourceEqGetter, Getter<E2> targetEqGetter, BiConsumer<T, SUBQUERY> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.in(sourceGetter, this.buildInOrNotInSubQuery(selectGetter, sourceEqGetter, sourceStorey, targetEqGetter, consumer));
    }
}
