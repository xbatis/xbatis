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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * 用户返回非表真实列，例如 max，min后产生的列或者 新的列
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResultField {

    /**
     * 列名
     *
     * @return
     */
    String value() default "";

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
}
