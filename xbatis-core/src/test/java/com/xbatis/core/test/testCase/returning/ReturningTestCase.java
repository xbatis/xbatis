package com.xbatis.core.test.testCase.returning;

import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturningTestCase extends BaseTest {

    @Test
    public void singleUpdateTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL && TestDataSource.DB_TYPE.getDbModel() != DbModel.PGSQL) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SysUser sysUser = UpdateChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)
                    .set(SysUser::getUserName, "abc2")
                    .returning(SysUser.class)
                    .returnType(SysUser.class)
                    .executeAndReturning();

            assertEquals(sysUser.getUserName(), "abc2");
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Map<String, Object> map = UpdateChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)
                    .set(SysUser::getUserName, "abc3")
                    .returning(SysUser.class)
                    .returnType(Map.class)
                    .executeAndReturning();

            assertEquals(map.get("user_name"), "abc3");
        }
    }

    @Test
    public void multiUpdateTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL && TestDataSource.DB_TYPE.getDbModel() != DbModel.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            List<SysUser> list = UpdateChain.of(sysUserMapper)
                    .in(SysUser::getId, 1, 2)
                    .set(SysUser::getUserName, "abc2")
                    .returning(SysUser.class)
                    .returnType(SysUser.class)
                    .executeAndReturningList();

            assertEquals(list.get(0).getUserName(), "abc2");
            assertEquals(list.get(1).getUserName(), "abc2");
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<Map<String, Object>> list = UpdateChain.of(sysUserMapper)
                    .in(SysUser::getId, 1, 2)
                    .set(SysUser::getUserName, "abc3")
                    .returning(SysUser.class)
                    .returnType(Map.class)
                    .executeAndReturningList();

            assertEquals(list.get(0).get("user_name"), "abc3");
            assertEquals(list.get(1).get("user_name"), "abc3");
        }
    }

    @Test
    public void singleDeleteTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL && TestDataSource.DB_TYPE.getDbModel() != DbModel.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SysUser sysUser = DeleteChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)
                    .returning(SysUser.class)
                    .returnType(SysUser.class)
                    .executeAndReturning();

            assertEquals(sysUser.getUserName(), "admin");


            sysUser = DeleteChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)
                    .returning(SysUser.class)
                    .returnType(SysUser.class)
                    .executeAndReturning();

            assertEquals(null, sysUser);
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Map<String, Object> map = DeleteChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)
                    .returning(SysUser.class)
                    .returnType(Map.class)
                    .executeAndReturning();
            assertEquals(map.get("user_name"), "admin");

            map = DeleteChain.of(sysUserMapper)
                    .eq(SysUser::getId, 1)

                    .returning(SysUser.class)
                    .returnType(Map.class)
                    .executeAndReturning();
            assertEquals(map, null);
        }

    }

    @Test
    public void multiDeleteTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL && TestDataSource.DB_TYPE.getDbModel() != DbModel.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            List<SysUser> list = DeleteChain.of(sysUserMapper)
                    .in(SysUser::getId, 1, 2)
                    .returning(SysUser.class)
                    .returnType(SysUser.class)
                    .executeAndReturningList();

            assertEquals(list.get(0).getUserName(), "admin");
            assertEquals(list.get(1).getUserName(), "test1");
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            List<Map> list = DeleteChain.of(sysUserMapper)
                    .in(SysUser::getId, 1, 2)
                    .returning(SysUser.class)
                    .returnType(Map.class)
                    .executeAndReturningList();

            assertEquals(list.get(0).get("user_name"), "admin");
            assertEquals(list.get(1).get("user_name"), "test1");
        }

    }
}