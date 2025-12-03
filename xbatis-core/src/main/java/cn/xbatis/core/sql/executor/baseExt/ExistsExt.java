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

package cn.xbatis.core.sql.executor.baseExt;

import cn.xbatis.core.sql.executor.SubQuery;
import db.sql.api.Getter;
import db.sql.api.cmd.executor.method.condition.IExistsMethod;

import java.util.function.BiConsumer;

public interface ExistsExt<T> extends IExistsMethod<T> {

    <E> SubQuery buildExistsOrNotExistsSubQuery(Class<E> entity, BiConsumer<T, SubQuery> consumer);

    <E1, E2> SubQuery buildExistsOrNotExistsSubQuery(Getter<E1> sourceGetter, int sourceStorey, Getter<E2> targetGetter, BiConsumer<T, SubQuery> consumer);

    default  <E> T exists(Class<E> entity, BiConsumer<T, SubQuery> consumer) {
        return this.exists(true, entity, consumer);
    }

    default <E> T exists(boolean when, Class<E> entity, BiConsumer<T, SubQuery> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.exists(this.buildExistsOrNotExistsSubQuery(entity, consumer));
    }

    default <E1, E2> T exists(Getter<E1> sourceGetter, Getter<E2> targetGetter, BiConsumer<T, SubQuery> consumer) {
        return this.exists(sourceGetter, 1, targetGetter, consumer);
    }

    default <E1, E2> T exists(boolean when, Getter<E1> sourceGetter, Getter<E2> targetGetter, BiConsumer<T, SubQuery> consumer) {
        return this.exists(when, sourceGetter, 1, targetGetter, consumer);
    }

    default <E1, E2> T exists(Getter<E1> sourceGetter, int sourceStorey, Getter<E2> targetGetter, BiConsumer<T, SubQuery> consumer) {
        return this.exists(true, sourceGetter, sourceStorey, targetGetter, consumer);
    }

    default <E1, E2> T exists(boolean when, Getter<E1> sourceGetter, int sourceStorey, Getter<E2> targetGetter, BiConsumer<T, SubQuery> consumer) {
        if (!when) {
            return (T) this;
        }
        return this.exists(this.buildExistsOrNotExistsSubQuery(sourceGetter, sourceStorey, targetGetter, consumer));
    }
}
