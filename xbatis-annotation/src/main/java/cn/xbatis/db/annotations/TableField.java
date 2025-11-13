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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {

    /**
     * 列名
     *
     * @return
     */
    String value() default "";

    /**
     * 是否进行 select
     * <p>
     * 大字段可设置为 false 不加入 select 查询范围
     *
     * @return 默认 true
     */
    boolean select() default true;

    /**
     * 是否进行 insert
     *
     * @return 默认 true
     */
    boolean insert() default true;

    /**
     * 是否进行 update
     * 只对实体类、Model类修改的方法生效；UpdateChain等不会生效；强制时生效
     *
     * @return 默认 true
     */
    boolean update() default true;

    /**
     * 是否永远不修改
     * 只对实体类、Model类修改的方法生效；UpdateChain等不会生效；即使强制也不生效
     *
     * @return 默认 false
     */
    boolean neverUpdate() default false;

    /**
     * 是否表字段
     *
     * @return 默认 true
     */
    boolean exists() default true;

    /**
     * 配置 列的 jdbcType
     *
     * @return
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * 类型处理 针对特殊 类型的列使用
     *
     * @return
     */
    Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;

    /**
     * 插入默认值 默认为NULL，“”表示NULL，如需表达 空字符，填 {BLANK}
     */
    String defaultValue() default "";

    /**
     * 插入默认值是否总是填充,开启则不管有没有值都是填充
     */
    boolean defaultValueFillAlways() default false;

    /**
     * 修改默认值 默认为NULL，“”表示NULL，如需表达 空字符，填 {BLANK}
     */
    String updateDefaultValue() default "";

    /**
     * 修改默认值 是否总是填充,开启则不管有没有值都是填充
     */
    boolean updateDefaultValueFillAlways() default false;

    /**
     * 列定义;不用于orm操作，用于未来表生成
     *
     * @return @ColumnDefinition
     */
    ColumnDefinition definition() default @ColumnDefinition;
}
