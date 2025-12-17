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

package cn.xbatis.core.mybatis.typeHandler;

import cn.xbatis.core.util.CollectionNewUtil;
import cn.xbatis.core.util.TypeConvertUtil;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 多个以逗号分隔的值
 * 支持集合类型 和 数组
 */
public class MultiValueTypeHandler extends GenericTypeHandler<Object> {

    public MultiValueTypeHandler(Class<?> type) {
        this(type, null);
    }

    public MultiValueTypeHandler(Class<?> type, Class<?> genericType) {
        super(type, genericType);
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
        if (String[].class.isAssignableFrom(this.type)) {
            return arr;
        }

        if (this.type.isArray()) {
            Class componentType = this.genericType == null ? this.type.getComponentType() : this.genericType;
            Object[] newArr = (Object[]) Array.newInstance(componentType, arr.length);
            int i = 0;
            for (String s : arr) {
                if (s.isEmpty()) {
                    continue;
                }
                newArr[i] = TypeConvertUtil.convert(arr[i], componentType);
                i++;
            }
            return newArr;
        }

        Collection list = CollectionNewUtil.newInstance(this.type);
        if (this.genericType == null) {
            Collections.addAll(list, arr);
            return list;
        }
        for (String s : arr) {
            list.add(TypeConvertUtil.convert(s, this.getGenericType()));
        }
        return list;
    }
}
