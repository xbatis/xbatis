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

/**
 * 此注解会根据值，method，factory做session级的缓存，请注意
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PutValue {

    /**
     * 对应的实体类
     *
     * @return
     */
    Class source();

    /**
     * 对应entity的属性，多个逗号分隔
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
     * 目标类 需要实现 指定method名字的静态方法
     *
     * @return
     */
    Class factory();

    /**
     * @return
     */
    String method();

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
