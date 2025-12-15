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

import db.sql.api.DbType;

public class DefaultDbTypeParser implements DbTypeParser {

    /**
     * 默认实例
     */
    public static final DbTypeParser INSTANCE = new DefaultDbTypeParser();

    protected DbType getDbType(String jdbcUrl) {
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DbType.MARIA_DB;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DbType.PGSQL;
        } else if (jdbcUrl.contains(":sqlserver:")) {
            return DbType.SQL_SERVER;
        } else if (jdbcUrl.contains(":h2:")) {
            return DbType.H2;
        } else if (jdbcUrl.contains(":dm:")) {
            return DbType.DM;
        } else if (jdbcUrl.contains(":db2:")) {
            return DbType.DB2;
        } else if (jdbcUrl.contains(":kingbase8:")) {
            return DbType.KING_BASE;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (jdbcUrl.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":opengauss:")) {
            return DbType.GAUSS;
        } else if (jdbcUrl.contains(":gaussdb:")) {
            return DbType.GAUSS;
        }
        return null;
    }

    @Override
    public DbType getDbTypeByUrl(String jdbcUrl) {
        DbType dbType = getDbType(jdbcUrl);
        if (dbType != null) {
            return dbType;
        }
        throw new DbTypeUtil.DbTypeParseException("Unrecognized database type:" + jdbcUrl);
    }
}
