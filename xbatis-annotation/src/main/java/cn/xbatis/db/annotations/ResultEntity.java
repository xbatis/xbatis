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
 * 实体类映射，会自动关键 实体类于注解类的关系
 * 无法自动映射的 可使用 @EntityField 注解（精准）
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResultEntity {

    /**
     * 对应的实体类
     * 如果不想指定，而是作为一个普通的 映射类可以指定为 Void.class
     * @return 对应的实体类
     */
    Class value();

    /**
     * 存储层级，用于自动select场景
     *
     * @return 存储层级
     */
    int storey() default 1;
}
