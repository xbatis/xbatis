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

package com.xbatis.core.test.listerner;

import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OnUpdateTest extends BaseTest {

    @Test
    public void onUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserOnInsert sysUserOnInsert = new SysUserOnInsert();
            sysUserOnInsert.setId(1);
            sysUserOnInsert.setRole_id(1);
            sysUserOnInsert.setUserName("xx");
            sysUserOnInsert.setPassword("xx");

            sysUserMapper.getBasicMapper().update(sysUserOnInsert);

            assertEquals("onUpdate", sysUserOnInsert.getUserName());
            assertNotNull(sysUserOnInsert.getCreateTime());
        }
    }

    @Test
    public void onUpdatesTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserOnInsert sysUserOnInsert = new SysUserOnInsert();
            sysUserOnInsert.setId(1);
            sysUserOnInsert.setRole_id(1);
            sysUserOnInsert.setUserName("xx");
            sysUserOnInsert.setPassword("xx");

            SysUserOnInsert sysUserOnInsert2 = new SysUserOnInsert();
            sysUserOnInsert2.setId(2);
            sysUserOnInsert2.setRole_id(1);
            sysUserOnInsert2.setUserName("xx");
            sysUserOnInsert2.setPassword("xx");

            sysUserMapper.getBasicMapper().update(Arrays.asList(sysUserOnInsert, sysUserOnInsert2));

            assertEquals("onUpdate", sysUserOnInsert.getUserName());
            assertNotNull(sysUserOnInsert.getCreateTime());

            assertEquals("onUpdate", sysUserOnInsert2.getUserName());
            assertNotNull(sysUserOnInsert2.getCreateTime());
        }
    }

}
