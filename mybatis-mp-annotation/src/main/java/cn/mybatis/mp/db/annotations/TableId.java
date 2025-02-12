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

import cn.mybatis.mp.db.IdAutoType;
import db.sql.api.DbType;

import java.lang.annotation.*;

/**
 * ID 自增
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(TableId.List.class)
public @interface TableId {

    /**
     * 自增类型
     *
     * @return
     */
    IdAutoType value() default IdAutoType.AUTO;

    /**
     * 数据库类型
     *
     * @return
     */
    DbType dbType() default DbType.MYSQL;

    /**
     * 自增器的名字
     * 自定义生成器 需要 实现 cn.mybatis.mp.core.incrementer.IdentifierGenerator
     * 然后 注册到ID生成器工厂 cn.mybatis.mp.core.incrementer.IdentifierGeneratorFactory.register(name,ID自增器实例)
     *
     * @return
     */
    String generatorName() default "";

    /**
     * id 自增的sql语句
     *
     * @return
     */
    String sql() default "";

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface List {
        TableId[] value();
    }
}
