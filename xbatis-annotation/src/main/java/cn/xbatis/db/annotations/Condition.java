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
@Repeatable(Conditions.class)
public @interface Condition {

    /**
     * 条件类型
     *
     * @return Type
     */
    Type value() default Type.EQ;

    /**
     * 目标实体类,如果在实体类里 或者类上指定了，则可不写
     *
     * @return 实体类
     */
    Class<?> target() default Void.class;

    /**
     * 属性
     *
     * @return 属性
     */
    String property() default "";

    /**
     * 存储层级
     *
     * @return
     */
    int storey() default 1;

    /**
     * like的方式 默认 %xx%
     *
     * @return LikeMode
     */
    LikeMode likeMode() default LikeMode.DEFAULT;

    /**
     * 将日期转成到这天的最后1秒
     * 只支持 lte 和 between的第2个参数
     * 支持类型为LocalDate/Date/String/Long/LocalDateTime
     *
     * @return
     */
    boolean toEndDayTime() default false;

    /**
     * 支持基本类型的默认值
     * 支持动态默认值，也可以自定义默认值；
     * 例如 官方的默认值 "{NOW}" "{TODAY}"
     * "{NOW}" 支持单个时间
     * "{TODAY}" 时间范围（数组类型或者集合类型字段）
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 转换成实体类字段的类型
     *
     * @return
     */
    boolean cast() default false;

    enum Type {
        IGNORE,
        EQ,
        NE,
        IN,
        LT,
        LTE,
        GT,
        GTE,
        LIKE,
        NOT_LIKE,
        ILIKE,
        NOT_ILIKE,
        BETWEEN,
        // boolean 或 0 1表达
        NULL,
        // boolean 或 0 1表达
        NOT_NULL,
        // boolean 或 0 1表达
        BLANK,
        // boolean 或 0 1表达
        NOT_BLANK,
    }

    enum LikeMode {
        NONE,
        DEFAULT,
        LEFT,
        RIGHT
    }
}
