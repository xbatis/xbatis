/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package db.sql.api.function;

import java.util.Objects;

@FunctionalInterface
public interface ThreeConsumer<T, U, U2> {

    void accept(T t, U u, U2 u2);

    default ThreeConsumer<T, U, U2> andThen(ThreeConsumer<? super T, ? super U, ? super U2> after) {
        Objects.requireNonNull(after);

        return (l, r, r2) -> {
            accept(l, r, r2);
            after.accept(l, r, r2);
        };
    }
}
