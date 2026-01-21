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
 * 结果映射
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NestedResultEntity {

    /**
     * 对应的实体类;默认为Void.class；
     * 当为Void.class，它会读取字段上的vo的@ResultEntity里的target或者该字段类型对应的实体类
     * @return 对应的实体类
     */
    Class target() default Void.class;

    /**
     * 存储层级，用于自动select场景
     *
     * @return 存储层级
     */
    int storey() default 1;
}
