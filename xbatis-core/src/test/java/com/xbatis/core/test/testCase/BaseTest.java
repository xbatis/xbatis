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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.configuration.MybatisConfiguration;
import cn.xbatis.core.mybatis.configuration.MybatisDatabaseIdProvider;
import cn.xbatis.core.mybatis.mapper.DbRunner;
import com.xbatis.core.test.db2.typeHandler.LocalDateTimeTypeHandler;
import com.xbatis.core.test.fetch.FetchCacheTestImpl;
import com.xbatis.core.test.mapper.*;
import com.xbatis.core.test.typeHandler.SQLiteLocalDateTimeHandler;
import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.impl.paging.OracleRowNumPagingProcessor;
import db.sql.api.impl.paging.SQLServerRowNumberOverPagingProcessor;
import db.sql.api.impl.tookit.SQLPrinter;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BaseTest {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    protected SqlSessionFactory sqlSessionFactory;
    protected DataSource dataSource;

    protected MybatisConfiguration configuration;

    public Resource[] resolveMapperLocations(String mapperLocations) {
        return Stream.of(mapperLocations).flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    }

    private Resource[] getResources(String location) {
        try {
            return resourceResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }


    @BeforeEach
    public void init() {
        System.out.println("init");
        XbatisGlobalConfig.setPagingProcessor(DbType.ORACLE, new OracleRowNumPagingProcessor());
        XbatisGlobalConfig.setPagingProcessor(DbType.SQL_SERVER, new SQLServerRowNumberOverPagingProcessor());
        XbatisGlobalConfig.setFetchCache(new FetchCacheTestImpl());
        dataSource = TestDataSource.getDataSource();

        // 1 创建 事务管理工厂
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        // 2 创建mybatis 环境
        Environment environment = new Environment("Test", transactionFactory, dataSource);

        // 3 创建xbatis 配置类
        configuration = new MybatisConfiguration(environment);

        if (TestDataSource.DB_TYPE == DbType.DB2) {
            //因为DB2的 jdbc 兼容不好
            configuration.getTypeHandlerRegistry().register(LocalDateTime.class, LocalDateTimeTypeHandler.class);
        }

        if (TestDataSource.DB_TYPE == DbType.SQLITE) {
            configuration.getTypeHandlerRegistry().register(LocalDateTime.class, SQLiteLocalDateTimeHandler.class);
        }

        configuration.setLogImpl(StdOutImpl.class);
        configuration.setMapUnderscoreToCamelCase(false);

        // 4 手动增加 Mapper 接口
        configuration.addMapper(SysRoleMapper.class);
        configuration.addMapper(NoneMapper.class);
        configuration.addMapper(SysUserMapper.class);

        configuration.addMapper(SysUserScoreMapper.class);
        configuration.addMapper(IdTestMapper.class);
        configuration.addMapper(VersionTestMapper.class);
        configuration.addMapper(TenantTestMapper.class);
        configuration.addMapper(LogicDeleteTestMapper.class);
        configuration.addMapper(DefaultValueTestMapper.class);
        configuration.addMapper(IdTest2Mapper.class);
        configuration.addMapper(CompositeTestMapper.class);

        configuration.addMapper(NestedFirstMapper.class);
        configuration.addMapper(NestedMutiFirstMapper.class);
        configuration.addMapper(UUIDMapper.class);
        configuration.addMapper(SysUserEncryptMapper.class);

        configuration.addMapper(MultiPkMapper.class);
        configuration.addMapper(DbRunner.class);
        configuration.addMapper(SysUserIDMapper.class);
        configuration.addMapper(SplitTableTestMapper.class);
        configuration.addMapper(AddrArchiveMapper.class);


        String mapperLocations = "classpath:/mappers/**.xml";

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setMapperLocations(getResources(mapperLocations));
        factory.setConfiguration(configuration);


        //设置多数据库 DatabaseIdProvider xml 多数据库 判断时开启
        factory.setDatabaseIdProvider(new MybatisDatabaseIdProvider(true));

        factory.setDataSource(dataSource);
        // 5 创建 mybatis sqlSessionFactory
        try {
            sqlSessionFactory = factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        // 6 获取对应的Mapper 实例
//        SqlSession session=sqlSessionFactory.openSession(true);
//        LogicDeleteTestMapper mapper=session.getMapper(LogicDeleteTestMapper.class);
//
//        // 7 Mapper使用
//        mapper.getById(1);

        for (DbType dbType : DbType.values()) {
            dbType.addKeyword("value3");
        }
    }


    @AfterEach
    public void close() {
        if (dataSource instanceof Closeable) {
            try {
                ((Closeable) dataSource).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (dataSource instanceof EmbeddedDatabase) {
            ((EmbeddedDatabase) dataSource).shutdown();
        }
    }

    public void check(String message, String targetSql, Cmd cmd) {
        this.check(message, targetSql, SQLPrinter.sql(cmd));
    }

    public void check(String message, String targetSql, String sql) {
        String sql1 = trim(targetSql);
        String sql2 = trim(sql);
        System.out.println("sql1:  " + sql1);
        System.out.println("sql2:  " + sql2);
        assertEquals(sql1, sql2, message);
    }

    private String trim(String sql) {
        return sql.replaceAll("  ", " ")
                .replaceAll(" ,", ",")
                .replaceAll(", ", ",")
                .replaceAll(" =", "=")
                .replaceAll("= ", "=")


                .replaceAll("\\( ", "(")

                .replaceAll(" \\)", ")")

                .replaceAll("> ", ">")
                .replaceAll(" >", ">")
                .replaceAll("< ", "<")
                .replaceAll(" <", "<")

                .toLowerCase().trim();
    }

}
