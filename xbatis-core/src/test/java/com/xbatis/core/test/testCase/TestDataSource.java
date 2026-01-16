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

package com.xbatis.core.test.testCase;

import com.xbatis.core.test.MyDbType;
import com.zaxxer.hikari.HikariDataSource;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.IDbType;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TestDataSource {

    public static final IDbType DB_TYPE = DbType.H2;

    public static final String TIME_ZONE = "Asia/Shanghai";

    private static final String DB_NAME = "test3";

    public static DataSource getDataSource() {
        DataSource dataSource = selectDataSource();
        initData(dataSource);
        return dataSource;
    }

    public static DataSource selectDataSource() {
        if (DB_TYPE instanceof MyDbType) {
            MyDbType dbType = (MyDbType) DB_TYPE;
            switch (dbType) {
                case LIKE_MYSQL: {
                    return createMySQLDataSource();
                }
                case LIKE_PGSQL: {
                    return createPostgresDataSource();
                }
                case LIKE_ORACLE: {
                    return createOracleDataSource();
                }
                default: {
                    throw new RuntimeException("NOT support");
                }
            }
        }
        DbType dbType = (DbType) DB_TYPE;
        switch (dbType) {
            case H2: {
                return createH2DataSource();
            }
            case MYSQL: {
                return createMySQLDataSource();
            }
            case MARIA_DB: {
                return createMariadbDataSource();
            }
            case PGSQL: {
                return createPostgresDataSource();
            }
            case ORACLE: {
                return createOracleDataSource();
            }
            case DM: {
                return createDmDataSource();
            }
            case SQL_SERVER: {
                return createSqlServerDataSource();
            }
            case DB2: {
                return createDB2DataSource();
            }
            case KING_BASE: {
                return createKingbaseDataSource();
            }

            case SQLITE: {
                return createSqliteDataSource();
            }

            case GAUSS: {
                return createOpenGaussDataSource();
            }

            case CLICK_HOUSE: {
                return createClickhouseDataSource();
            }

            case OCEAN_BASE: {
                return createOceanBaseDataSource();
            }

            case HIGHGO: {
                return createHighgoDataSource();
            }

            default: {
                throw new RuntimeException("NOT support");
            }
        }
    }

    private static DataSource createH2DataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:h2:mem:" + DB_NAME + ";DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setDriverClassName("org.h2.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createMySQLDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/" + DB_NAME + "?createDatabaseIfNotExist=true&characterEncoding=utf-8&serverTimezone=" + TIME_ZONE);
        ds.setUsername("root");
        ds.setPassword("123456");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createMariadbDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:mariadb://localhost:3307/" + DB_NAME + "?createDatabaseIfNotExist=true&characterEncoding=utf-8&serverTimezone=" + TIME_ZONE);
        ds.setUsername("root");
        ds.setPassword("123456");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createPostgresDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        ds.setUsername("postgres");
        ds.setPassword("123456");
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createOpenGaussDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:opengauss://localhost:5437/postgres");
        ds.setUsername("gaussdb");
        ds.setPassword("Enmo@123");
        ds.setDriverClassName("org.opengauss.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createOracleDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/xe");
        ds.setUsername("system");
        ds.setPassword("oracle");
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createKingbaseDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:kingbase8://localhost:54321/test3");
        ds.setUsername("system");
        ds.setPassword("123456");
        ds.setDriverClassName("com.kingbase8.Driver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createClickhouseDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:clickhouse://localhost:8123/test3");
        //ds.setUsername("system");
        //ds.setPassword("123456");
        ds.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createSqlServerDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:sqlserver://localhost:1433;DatabaseName=master;encrypt=false;useUnicode=true;characterEncoding=utf-8;genKeyNameCase=2;serverTimezone=" + TIME_ZONE);
        ds.setUsername("SA");
        ds.setPassword("AbC@128723");
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createDmDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:dm://localhost:5236/SYSDBA?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf-8&columnNameUpperCase=false");
        ds.setUsername("SYSDBA");
        ds.setPassword("SYSDBA001");
        ds.setDriverClassName("dm.jdbc.driver.DmDriver");
        ds.setAutoCommit(false);
        ds.setMaximumPoolSize(1);
        return ds;
    }

    private static DataSource createDB2DataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:db2://localhost:50000/" + DB_NAME);
        ds.setUsername("db2inst1");
        ds.setPassword("123456");
        ds.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        ds.setAutoCommit(false);
        ds.setMaximumPoolSize(1);
        return ds;
    }

    private static DataSource createSqliteDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:sqlite:target/" + DB_NAME + ".db?date_string_format=yyyy-MM-hh HH:mm:ss");
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createOceanBaseDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:oceanbase://localhost:2881/" + DB_NAME + "?rewriteBatchedStatements=TRUE&allowMultiQueries=TRUE&useLocalSessionState=TRUE&useUnicode=TRUE&characterEncoding=utf-8&socketTimeout=10000&connectTimeout=30000");
        ds.setDriverClassName("com.oceanbase.jdbc.Driver");
        ds.setUsername("root");
        ds.setPassword("test@123");
        ds.setAutoCommit(false);
        return ds;
    }

    private static DataSource createHighgoDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(1);
        ds.setJdbcUrl("jdbc:highgo://localhost:5866/highgo");
        ds.setUsername("highgo");
        ds.setPassword("Highgo@123");
        ds.setDriverClassName("com.highgo.jdbc.Driver");
        ds.setAutoCommit(false);
        return ds;
    }


    private static void initData(DataSource ds) {
        String ex_sql = null;
        try (Connection conn = ds.getConnection()) {
            //runSql(conn,"CREATE DATABASE "+DB_NAME+" COLLATE Chinese_PRC_CI_AS");
            String[] sqls = getSqlFromFile();
            for (String sql : sqls) {
                ex_sql = sql.replaceAll("[\r?\n]", " ");
                if (DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE.getDbModel() == DbModel.ORACLE) {
                    if (ex_sql.contains("DROP TABLE ")) {
                        String tableName = ex_sql.replace("DROP TABLE ", "").trim();
                        if (!existsOracleDropTable(conn, tableName)) {
                            continue;
                        }
                    } else if (ex_sql.contains("DROP SEQUENCE")) {
                        String seqName = ex_sql.replace("DROP SEQUENCE", "").trim();
                        if (!existsOracleSeq(conn, seqName)) {
                            continue;
                        }
                    }
                } else if (DB_TYPE == DbType.KING_BASE) {
                    if (ex_sql.contains("DROP TABLE ")) {
                        String tableName = ex_sql.replace("DROP TABLE ", "").trim();
                        if (!existsOracleDropTable(conn, tableName)) {
                            continue;
                        }
                    } else if (ex_sql.contains("DROP SEQUENCE")) {
                        String seqName = ex_sql.replace("DROP SEQUENCE", "").trim();
                        if (!existsOracleSeq(conn, seqName)) {
                            continue;
                        }
                    }
                }

                runSql(conn, ex_sql);
            }
        } catch (SQLException e) {
            System.out.println(ex_sql);
            throw new RuntimeException(e);
        }
    }

    private static boolean existsOracleDropTable(Connection conn, String table) {
        try {
            ResultSet resultSet = conn.prepareStatement("select count(1) from user_tables where table_name = upper('" + table + "')").executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean existsOracleSeq(Connection conn, String seqName) {
        try {
            ResultSet resultSet = conn.prepareStatement("SELECT 1 FROM user_sequences WHERE sequence_name =  upper('" + seqName + "')").executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static String[] getSqlFromFile() {
        String name = DB_TYPE.getName();
        if (DB_TYPE == MyDbType.LIKE_PGSQL || DB_TYPE == DbType.HIGHGO) {
            name = DbType.PGSQL.getName();
        } else if (DB_TYPE == MyDbType.LIKE_ORACLE) {
            name = DbType.ORACLE.getName();
        }
        try {
            return getSqlFromFile("/schema_" + name + ".sql");
        } catch (IOException e) {
            try {
                return getSqlFromFile("/schema.sql");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static String[] getSqlFromFile(String path) throws IOException {
        try (InputStream ins = TestDataSource.class.getResourceAsStream(path);) {

            if (Objects.isNull(ins)) {
                throw new FileNotFoundException();
            }
            int len = ins.available();
            byte[] bs = new byte[len];
            ins.read(bs);
            String str = new String(bs, "UTF-8");
            String[] sql = str.split(";");
            return sql;
        }

    }

    public static void runSql(Connection conn, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.commit();
        ps.close();
    }
}
