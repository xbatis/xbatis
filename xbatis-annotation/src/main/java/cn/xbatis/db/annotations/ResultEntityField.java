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
 * 结果字段 用于解决字段冲突问题
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResultEntityField {

    /**
     * 对应的实体类
     * 可不配置 默认继承上层类上的 target
     * @return 对应的实体类
     */
    Class target() default Void.class;

    /**
     * 对应target的属性
     * 空时直接去该字段名字
     *
     * @return 对应target 的属性
     */
    String property() default "";

    /**
     * 存储层级，用于自动select场景
     * -1 时 ，如果相同的实体类，则继承上层的 storey值，否则默认为1
     * @return 存储层级
     */
    int storey() default -1;
}
