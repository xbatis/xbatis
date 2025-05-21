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
@Target(ElementType.FIELD)
public @interface PutEnumValue {

    /**
     * 对应的实体类
     * 默认时：从当前作用域自动获取
     *
     * @return
     */
    Class source() default Void.class;

    /**
     * 对应entity的属性
     *
     * @return
     */
    String property();

    /**
     * 存储层级，用于自动select场景
     *
     * @return
     */
    int storey() default 1;

    /**
     * 枚举类
     *
     * @return
     */
    Class target();

    /**
     * 枚举code字段名字
     *
     * @return
     */
    String code() default "code";

    /**
     * 枚举值的字段名字
     *
     * @return
     */
    String value() default "name";

    /**
     * 是否必须有值
     *
     * @return
     */
    boolean required() default false;

    /**
     * 未匹配时的默认值
     *
     * @return
     */
    String defaultValue() default "";
}
