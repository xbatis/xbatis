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
import com.xbatis.core.test.DO.SysUser;
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
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            int cnt = dbRunner.execute("update t_sys_user set role_id=1 where id=1");
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
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            String user_name = dbRunner.execute(String.class, "update t_sys_user set user_name=? where id=1 RETURNING user_name", "xxx");
            assertEquals("xxx", user_name);
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
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            SysUser user = dbRunner.select(SysUser.class, "select * from t_sys_user where id =?", 1);
            assertEquals("admin", user.getUserName());

            Map map = dbRunner.select(Map.class, "select * from t_sys_user where id=?", 1);
            assertEquals("admin", map.get("user_name"));
        }
    }

    @Test
    public void paramSelectListTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DbRunner dbRunner = session.getMapper(DbRunner.class);
            List<SysUser> list = dbRunner.selectList(SysUser.class, "select * from t_sys_user where id in(?,?) order by id asc", 1, 2);
            assertEquals("admin", list.get(0).getUserName());
            assertEquals("test1", list.get(1).getUserName());

            List<Map> list2 = dbRunner.selectList(Map.class, "select * from t_sys_user where id in(?,?) order by id asc", 1, 2);
            assertEquals("admin", list2.get(0).get("user_name"));

            assertEquals("admin", list2.get(0).get("user_name"));
            assertEquals("test1", list2.get(1).get("user_name"));
        }
    }
}
