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
 * 排序类，配合 @OrderBy注解使用
 * 注意字段顺序，排在前面的优先排序
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderByTarget {
    /**
     * 目标实体类
     *
     * @return 实体类
     */
    Class<?> value();

    /**
     * 是否严格模式：开启则只匹配带有@OrderBy|@OrderByColumn|OrderByAsField注解字段
     *
     * @return 是否严格模式
     */
    boolean strict() default false;

    /**
     * 存储层级
     *
     * @return 存储层级
     */
    int storey() default 1;
}
