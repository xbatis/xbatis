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

package cn.mybatis.mp.core.mybatis.typeHandler;

import cn.mybatis.mp.core.util.TypeConvertUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private final static Map<Class<?>, Class<?>> CODE_TYPE_CACHE = new ConcurrentHashMap<>();
    private final static Map<Class<?>, Map<Object, EnumSupport<?>>> TYPE_CODE_ENUM_CACHE = new ConcurrentHashMap<>();
    private final Class<E> type;
    private final boolean enumSupport;
    private final Class enumSupportType;

    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        enumSupport = EnumSupport.class.isAssignableFrom(type);
        if (enumSupport) {
            enumSupportType = CODE_TYPE_CACHE.computeIfAbsent(type, key -> {
                try {
                    return type.getDeclaredMethod("getCode").getReturnType();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            enumSupportType = null;
        }
    }

    private Object getCode(E parameter) {
        if (enumSupport) {
            return ((EnumSupport<?>) parameter).getCode();
        }
        return parameter.name();
    }

    private E readValue(ResultSet rs, String columnName) throws SQLException {
        Object value;
        if (enumSupport) {
            value = rs.getObject(columnName);
            value = TypeConvertUtil.convert(value, this.enumSupportType);
        } else {
            value = rs.getString(columnName);
        }
        return this.convert(value);
    }

    private E readValue(ResultSet rs, int columnIndex) throws SQLException {
        Object value;
        if (enumSupport) {
            value = rs.getObject(columnIndex);
            value = TypeConvertUtil.convert(value, this.enumSupportType);
        } else {
            value = rs.getString(columnIndex);
        }
        return this.convert(value);
    }

    private E readValue(CallableStatement cs, int columnIndex) throws SQLException {
        Object value;
        if (enumSupport) {
            value = cs.getObject(columnIndex, this.enumSupportType);
        } else {
            value = cs.getString(columnIndex);
        }
        return this.convert(value);
    }

    private E convert(Object value) {
        if (value == null) {
            return null;
        }
        if (enumSupport) {
            EnumSupport ev = TYPE_CODE_ENUM_CACHE.computeIfAbsent(type, key -> {
                EnumSupport[] es = (EnumSupport[]) type.getEnumConstants();
                Map<Object, EnumSupport<?>> map = new HashMap<>();
                for (EnumSupport e : es) {
                    map.put(e.getCode(), e);
                }
                return map;
            }).get(value);

            if (Objects.isNull(ev)) {
                throw new IllegalArgumentException(
                        "No enum constant " + type.getCanonicalName() + " not included code : " + value);
            }
            return (E) ev;
        } else {
            return Enum.valueOf(this.type, value.toString());
        }
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object code = this.getCode(parameter);
        if (jdbcType == null) {
            ps.setObject(i, code);
        } else {
            ps.setObject(i, code, jdbcType.TYPE_CODE);
        }
    }


    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.readValue(rs, columnName);
    }

    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.readValue(rs, columnIndex);
    }

    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.readValue(cs, columnIndex);
    }
}
