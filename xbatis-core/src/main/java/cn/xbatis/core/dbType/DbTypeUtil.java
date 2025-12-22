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

package cn.xbatis.core.dbType;

import cn.xbatis.core.XbatisGlobalConfig;
import db.sql.api.DbType;
import org.apache.ibatis.session.Configuration;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Objects;

public final class DbTypeUtil {

    public static DbType getDbType(Configuration configuration) {
        return getDbType(configuration.getDatabaseId(), configuration.getEnvironment().getDataSource());
    }

    public static DbType getDbType(String databaseId, DataSource dataSource) {
        if (Objects.isNull(databaseId) || databaseId.isEmpty()) {
            return DbTypeUtil.getDbType(dataSource);
        }
        return DbType.getByName(databaseId);
    }

    public static DbType getDbType(DataSource dataSource) {
        return getDbType(getJdbcUrl(dataSource));
    }

    public static DbType getDbType(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        return XbatisGlobalConfig.getDbTypeParser().getDbTypeByUrl(jdbcUrl);
    }

    public static String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception ignored) {
                //ignore
            }
        }
        try (Connection connection = dataSource.getConnection()) {
            return getJdbcUrl(connection);
        } catch (Exception e) {
            throw new DbTypeParseException("无法解析到 数据库的url", e);
        }
    }

    public static String getJdbcUrl(Connection connection) {
        try {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw new DbTypeParseException("无法解析到 数据库的url", e);
        }
    }

    public static DbType getDbType(Connection connection) {
        return getDbType(getJdbcUrl(connection));
    }

    public static class DbTypeParseException extends RuntimeException {

        public DbTypeParseException(String message) {
            super(message);
        }

        public DbTypeParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
