/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package cn.xbatis.core.mybatis.typeHandler;

import cn.xbatis.core.util.CollectionNewUtil;
import cn.xbatis.core.util.TypeConvertUtil;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 多个以逗号分隔的值
 * 支持集合类型 和 数组
 */
public class MultiValueTypeHandler extends GenericTypeHandler<Object> {

    private final boolean isArray;

    private final Class<?> genericType;
    private final Class<?> clazz;

    public MultiValueTypeHandler(Type type) {
        super((Class) type, null);
        this.clazz = getTypeClass(type);
        this.isArray = isArray(type);
        this.genericType = getGenericType(type);
    }

    private static boolean isArray(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).isArray();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return isArray(parameterizedType.getRawType());
        } else return type instanceof GenericArrayType;
    }

    public static Class getTypeClass(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return ((Class) parameterizedType.getActualTypeArguments()[0]);
        } else if (type instanceof Class && ((Class<?>) type).isArray()) {
            return ((Class<?>) type).getComponentType();
        }
        if (type instanceof GenericArrayType) {
            return (Class) ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
    }

    public static Class getGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return ((Class) parameterizedType.getActualTypeArguments()[0]);
        } else if (type instanceof Class && ((Class<?>) type).isArray()) {
            return ((Class<?>) type).getComponentType();
        }
        if (type instanceof GenericArrayType) {
            return getGenericType(((GenericArrayType) type).getGenericComponentType());
        }
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int index, Object parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(parameter)) {
            ps.setNull(index, jdbcType.TYPE_CODE);
        } else {
            Object[] values = parameter instanceof Collection ? ((Collection) parameter).toArray() : (Object[]) parameter;
            StringBuilder sb = new StringBuilder();
            for (Object v : values) {
                if (v == null) {
                    continue;
                } else if (v instanceof String && ((String) v).isEmpty()) {
                    continue;
                }
                sb.append(",").append(v);
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(0);
            }
            ps.setString(index, sb.toString());
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    protected Object parse(String values) {
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        String[] arr = values.split(",");


        if (isArray) {
            if (genericType == null || String.class == genericType || Object.class == genericType) {
                return arr;
            }
            Class componentType = this.genericType;
            List<Object> tmp = new ArrayList<>(arr.length);
            for (String s : arr) {
                if (s.isEmpty()) {
                    continue;
                }
                tmp.add(TypeConvertUtil.convert(s, componentType));
            }
            Object[] newArr = (Object[]) Array.newInstance(componentType, tmp.size());
            return tmp.toArray(newArr);
        }

        Collection list = CollectionNewUtil.newInstance(this.clazz);
        if (genericType == null || String.class == genericType || Object.class == genericType) {
            Collections.addAll(list, arr);
            return list;
        }
        for (String s : arr) {
            list.add(TypeConvertUtil.convert(s, this.genericType));
        }
        return list;
    }
}
