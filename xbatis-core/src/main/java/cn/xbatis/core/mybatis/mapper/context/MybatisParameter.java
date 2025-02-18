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

package cn.xbatis.core.mybatis.mapper.context;

import db.sql.api.impl.tookit.Objects;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

public class MybatisParameter implements Serializable {

    private final Object value;

    private final Class<? extends TypeHandler<?>> typeHandler;

    private final JdbcType jdbcType;

    public MybatisParameter(Object value, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType) {
        this.typeHandler = typeHandler;
        this.jdbcType = jdbcType;
        this.value = value;
    }

    public static MybatisParameter create(Object value, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType) {
        return new MybatisParameter(value, typeHandler, jdbcType);
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.isNull(value) ? "null" : value.toString();
    }
}
