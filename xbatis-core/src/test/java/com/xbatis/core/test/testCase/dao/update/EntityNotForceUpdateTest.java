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

package com.xbatis.core.test.testCase.dao.update;

import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.dao.BaseDaoTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityNotForceUpdateTest extends BaseDaoTest {

    @Test
    public void notForceInsertTest1() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).update(sysUserModel, false);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void notForceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).update(sysUserModel);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void notForceInsertTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).update(sysUserModel, SysUser::getRole_id);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void notForceInsertTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).saveOrUpdate(sysUserModel, false);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void notForceInsertTest5() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).saveOrUpdate(sysUserModel);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void notForceInsertTest6() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = getDao(sysUserMapper).getById(1);
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).saveOrUpdate(sysUserModel, SysUser::getId);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "admin");
            assertEquals(sysUser.getRole_id(), 0);
        }
    }
}
