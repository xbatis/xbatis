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

public @interface ColumnDefinition {

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 长度
     *
     * @return
     */
    int length() default 0;

    /**
     * 是否唯一
     *
     * @return
     */
    boolean unique() default false;

    /**
     * 是否可以为NULL
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * 列的精度，仅对十进制数值有效，表示有效数值的总位数。默认为0。
     *
     * @return
     */
    int precision() default 0;

    /**
     * 列的精度，仅对十进制数值有效，表示小数位的总位数。默认为0。
     *
     * @return
     */
    int scale() default 0;

    /**
     * 生成列的 DDL 时使用的 SQL 片段。默认使用推断的类型来生成 SQL 片段以创建此列。
     *
     * @return
     */
    String definition() default "";

    /**
     * 备注
     *
     * @return
     */
    String comment() default "";

}
