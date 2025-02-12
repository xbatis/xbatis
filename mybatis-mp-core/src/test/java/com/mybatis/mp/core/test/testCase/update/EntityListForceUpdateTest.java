/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package com.mybatis.mp.core.test.testCase.update;

import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityListForceUpdateTest extends BaseTest {

    @Test
    public void batchForceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setUserName(null);
            sysUserMapper.update(Collections.singletonList(sysUserModel), true);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void batchForceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setUserName(null);
            sysUserMapper.update(Collections.singletonList(sysUserModel), SysUser::getUserName);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void batchForceInsertTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(Collections.singletonList(sysUserModel), true);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void batchForceInsertTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(Collections.singletonList(sysUserModel), SysUser::getUserName);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }
}
