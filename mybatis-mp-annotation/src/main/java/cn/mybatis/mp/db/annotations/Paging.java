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
 * 分页注解，针对xml，自动帮助你分页
 * 分页有几个要求，
 * 第1个：参数 必须是Pager类
 * 第2个：被注解的方法不能实现
 * 第3个：返回 必须是Pager类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Paging {

    /**
     * 是否优化
     *
     * @return
     */
    boolean optimize() default true;
}
