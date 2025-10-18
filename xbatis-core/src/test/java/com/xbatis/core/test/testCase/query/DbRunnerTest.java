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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.mybatis.mapper.DbRunner;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbRunnerTest extends BaseTest {

    @Test
    public void noParamUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.execute("update t_sys_user set role_id=1 where id=1");
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void oneParamUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            int cnt = dbRunner.execute("update t_sys_user set user_name=? where id=1", "xxx");
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void oneParamUpdateAndSelectTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String user_name = sysUserMapper.executeAndReturning(String.class, "update t_sys_user set user_name=? where id=1 RETURNING user_name", "xxx");
            assertEquals("xxx", user_name);
        }
    }

    @Test
    public void multiParamUpdateAndSelectTest() {
        if (TestDataSource.DB_TYPE != DbType.SQLITE && TestDataSource.DB_TYPE != DbType.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<String> user_names = sysUserMapper.executeAndReturningList(String.class, "update t_sys_user set user_name=? where id in (1,2) RETURNING user_name", "xxx");
            assertEquals("xxx", user_names.get(0));
            assertEquals("xxx", user_names.get(1));
        }
    }

    @Test
    public void multiParamTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            int cnt = dbRunner.execute("update t_sys_user set user_name=? where id=?", "xxx", 1);
            assertEquals(cnt, 1);
        }
    }


    @Test
    public void oneParamSelectTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser user = sysUserMapper.select(SysUser.class, "select * from t_sys_user where id =?", 1);
            assertEquals("admin", user.getUserName());

            Map map = sysUserMapper.select(Map.class, "select * from t_sys_user where id=?", 1);
            assertEquals("admin", map.get("user_name"));
        }
    }

    @Test
    public void paramSelectListTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.selectList(SysUser.class, "select * from t_sys_user where id in(?,?) order by id asc", 1, 2);
            assertEquals("admin", list.get(0).getUserName());
            assertEquals("test1", list.get(1).getUserName());

            List<Map> list2 = sysUserMapper.selectList(Map.class, "select * from t_sys_user where id in(?,?) order by id asc", 1, 2);
            assertEquals("admin", list2.get(0).get("user_name"));

            assertEquals("admin", list2.get(0).get("user_name"));
            assertEquals("test1", list2.get(1).get("user_name"));
        }
    }

    @Test
    public void cmdSelectTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Map map = sysUserMapper.select(Map.class, "select * from t_sys_user t where ? ", WhereUtil.create().eq(SysUser::getId, 1));
            System.out.println(map);
            assertEquals("admin", map.get("user_name"));

            List<Map> list = sysUserMapper.selectList(Map.class, "select * from t_sys_user t where ? ", WhereUtil.create());
            System.out.println(list);
            assertEquals(3, list.size());
        }
    }

    @Test
    public void cmdSelectTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Map map = sysUserMapper.select(Map.class, "select * from t_sys_user t where id=? and ? ", 1, WhereUtil.safeWhere().eq(SysUser::getId, 1));
            System.out.println(map);
            assertEquals("admin", map.get("user_name"));
        }
    }
}
