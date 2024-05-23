package cn.mybatis.mp.core.mybatis.configuration;

import cn.mybatis.mp.core.util.DbTypeUtil;
import db.sql.api.DbType;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;

import javax.sql.DataSource;
import java.util.Properties;

public class MybatisDatabaseIdProvider extends VendorDatabaseIdProvider {

    private boolean defaultConfig = true;

    public MybatisDatabaseIdProvider() {
        super.setProperties(this.createDefaultProperties());
    }

    @Override
    public void setProperties(Properties p) {
        super.setProperties(p);
        this.defaultConfig = false;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) {
        if (defaultConfig) {
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
        return properties;
    }
}
