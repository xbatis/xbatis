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

package db.sql.api.cmd.struct;

import java.util.function.Consumer;

public interface Nested<SELF, CHAIN> {
    /**
     * 嵌套（不改变逻辑符）
     * 就是用于括号包裹
     *
     * @param consumer 条件链路
     * @return 自己
     */
    SELF nested(Consumer<CHAIN> consumer);

    /**
     * AND 嵌套（执行后逻辑符为AND）
     * 就是用于括号包裹
     * @param consumer 条件链路
     * @return 自己
     */
    SELF andNested(Consumer<CHAIN> consumer);

    /**
     * OR 嵌套（执行后逻辑符为OR）
     * 就是用于括号包裹
     * @param consumer 条件链路
     * @return 自己
     */
    SELF orNested(Consumer<CHAIN> consumer);

    /**
     * 嵌套（不改变逻辑符）
     * 就是用于括号包裹
     *
     * @param when     是否生效
     * @param consumer 条件链路
     * @return 自己
     */
    default SELF nested(boolean when, Consumer<CHAIN> consumer) {
        if (!when) {
            return (SELF) this;
        }
        return nested(consumer);
    }

    /**
     * AND 嵌套（执行后逻辑符为AND）
     * 就是用于括号包裹
     * @param when 是否生效
     * @param consumer 条件链路
     * @return 自己
     */
    default SELF andNested(boolean when, Consumer<CHAIN> consumer) {
        if (!when) {
            return (SELF) this;
        }
        return andNested(consumer);
    }

    /**
     * OR 嵌套（执行后逻辑符为OR）
     * 就是用于括号包裹
     * @param when 是否生效
     * @param consumer 条件链路
     * @return 自己
     */
    default SELF orNested(boolean when, Consumer<CHAIN> consumer) {
        if (!when) {
            return (SELF) this;
        }
        return orNested(consumer);
    }
}
