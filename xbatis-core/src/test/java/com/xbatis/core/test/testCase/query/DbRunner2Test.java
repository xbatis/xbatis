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

import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbRunner2Test extends BaseTest {

    @Test
    public void noParamTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper dbRunner = session.getMapper(SysUserMapper.class);
            int cnt = dbRunner.execute("update t_sys_user set role_id=1 where id=1");
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void oneParamTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper dbRunner = session.getMapper(SysUserMapper.class);
            int cnt = dbRunner.execute("update t_sys_user set user_name=? where id=1", "xxx");
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void multiParamTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper dbRunner = session.getMapper(SysUserMapper.class);
            int cnt = dbRunner.execute("update t_sys_user set user_name=? where id=?", "xxx", 1);
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void truncateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper dbRunner = session.getMapper(SysUserMapper.class);
            dbRunner.truncate();
            assertEquals(dbRunner.getById(1), null);
        }
    }

}
