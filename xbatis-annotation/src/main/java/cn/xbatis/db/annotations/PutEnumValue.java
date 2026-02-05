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

package cn.xbatis.db.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface PutEnumValue {

    /**
     * 对应的实体类
     * 默认时：从当前作用域自动获取
     *
     * @return 对应的实体类
     */
    Class source() default Void.class;

    /**
     * 对应entity 的属性
     *
     * @return 对应entity 的属性
     */
    String property();

    /**
     * 存储层级，用于自动select场景
     * -1 时 ，如果相同的实体类，则继承上层的 storey值，否则默认为1
     *
     * @return 存储层级
     */
    int storey() default -1;

    /**
     * 枚举类
     *
     * @return 枚举类
     */
    Class target();

    /**
     * 枚举code 字段名字
     *
     * @return 枚举code 字段名字
     */
    String code() default "code";

    /**
     * 枚举值的字段名字
     *
     * @return 枚举值的字段名字
     */
    String value() default "name";

    /**
     * 是否必须有值
     *
     * @return 是否必须有值
     */
    boolean required() default false;

    /**
     * 未匹配时的默认值
     *
     * @return 未匹配时的默认值
     */
    String defaultValue() default "";
}
