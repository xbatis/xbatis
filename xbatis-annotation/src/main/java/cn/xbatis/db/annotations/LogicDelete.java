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

package cn.xbatis.db.annotations;

import java.lang.annotation.*;

/**
 * 逻辑删除
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogicDelete {

    /**
     * 删除前的值
     * 无法设置动态值，空时表示 初始值为NULL
     *
     * @return 初始值
     */
    String beforeValue() default "0";

    /**
     * 删除后的值
     * 可设置动态值，动态值格式为 {key} 例如：当前时间-{NOW}
     *
     * @return 删除后的值
     */
    String afterValue() default "1";

}
