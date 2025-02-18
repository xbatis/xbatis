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

package db.sql.api.cmd.struct;

import java.util.function.Consumer;

public interface Nested<SELF, CHAIN> {

    SELF andNested(Consumer<CHAIN> consumer);

    SELF orNested(Consumer<CHAIN> consumer);

    default SELF andNested(boolean when, Consumer<CHAIN> consumer) {
        if (!when) {
            return (SELF) this;
        }
        return andNested(consumer);
    }

    default SELF orNested(boolean when, Consumer<CHAIN> consumer) {
        if (!when) {
            return (SELF) this;
        }
        return orNested(consumer);
    }
}
