/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.db.annotations;

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
     *
     * @return
     */
    Class target() default Void.class;

    /**
     * 对应target的属性
     * 空时直接去该字段名字
     *
     * @return
     */
    String property() default "";

    /**
     * 存储层级，用于自动select场景
     *
     * @return
     */
    int storey() default 1;
}
