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

package cn.xbatis.core.mybatis.configuration;

import cn.xbatis.core.dbType.DbTypeUtil;
import db.sql.api.DbType;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;

import javax.sql.DataSource;
import java.util.Properties;

public class MybatisDatabaseIdProvider extends VendorDatabaseIdProvider {

    private final boolean useDbTypeAutoProvider;

    public MybatisDatabaseIdProvider() {
        this(true);
    }

    public MybatisDatabaseIdProvider(boolean useDbTypeAutoProvider) {
        this.useDbTypeAutoProvider = useDbTypeAutoProvider;
        if (!useDbTypeAutoProvider) {
            super.setProperties(this.createDefaultProperties());
        }
    }

    @Override
    public void setProperties(Properties p) {
        super.setProperties(p);
    }

    @Override
    public String getDatabaseId(DataSource dataSource) {
        if (useDbTypeAutoProvider) {
            return DbTypeUtil.getDbType(dataSource).name();
        }
        return super.getDatabaseId(dataSource);
    }

    protected Properties createDefaultProperties() {
        Properties properties = new Properties();
        properties.put("H2", DbType.H2.name());
        properties.put("Oracle", DbType.ORACLE.name());
        properties.put("MySQL", DbType.MYSQL.name());
        properties.put("MariaDB", DbType.MARIA_DB.name());
        properties.put("SQL Server", DbType.SQL_SERVER.name());
        properties.put("PostgreSQL", DbType.PGSQL.name());
        properties.put("DM DBMS", DbType.DM.name());
        properties.put("Kingbase", DbType.KING_BASE.name());
        properties.put("SQLite", DbType.SQLITE.name());
        properties.put("DB2", DbType.DB2.name());
        properties.put("openGauss", DbType.OPEN_GAUSS.name());
        properties.put("click", DbType.CLICK_HOUSE.name());
        return properties;
    }
}
