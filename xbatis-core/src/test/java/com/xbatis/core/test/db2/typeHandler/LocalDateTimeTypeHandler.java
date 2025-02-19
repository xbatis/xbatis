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

package com.xbatis.core.test.db2.typeHandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
    public LocalDateTimeTypeHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String v = rs.getString(columnName);
        if (Objects.isNull(v)) {
            return null;
        }
        return (LocalDateTime) rs.getObject(columnName, LocalDateTime.class);
    }

    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String v = rs.getString(columnIndex);
        if (Objects.isNull(v)) {
            return null;
        }
        return (LocalDateTime) rs.getObject(columnIndex, LocalDateTime.class);
    }

    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String v = cs.getString(columnIndex);
        if (Objects.isNull(v)) {
            return null;
        }
        return (LocalDateTime) cs.getObject(columnIndex, LocalDateTime.class);
    }
}
