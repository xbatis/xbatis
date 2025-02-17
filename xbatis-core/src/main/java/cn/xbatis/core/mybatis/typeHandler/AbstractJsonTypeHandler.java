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

import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class AbstractJsonTypeHandler extends GenericTypeHandler<Object> {

    public AbstractJsonTypeHandler(Class<?> type) {
        super(type);
    }

    public AbstractJsonTypeHandler(Class<?> type, Type genericType) {
        super(type, genericType);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(parameter)) {
            ps.setNull(i, jdbcType.TYPE_CODE);
        } else {
            ps.setString(i, toJson(parameter));
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

    private Object parse(String json) {
        return Objects.isNull(json) ? null : parseJson(json);
    }

    /**
     * 获取需要序列号的类型
     *
     * @return 类型
     */
    Type getDeserializeType() {
        return Objects.isNull(this.genericType) ? this.type : this.genericType;
    }

    /**
     * 入库 对象转json
     *
     * @param obj
     * @return
     */
    abstract String toJson(Object obj);

    /**
     * 出库 json 转对象
     *
     * @param json
     * @return 对象
     */
    abstract Object parseJson(String json);
}
