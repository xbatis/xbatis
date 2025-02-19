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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.model.SysUserModel;
import com.xbatis.core.test.testCase.dao.BaseDaoTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelForceUpdateTest extends BaseDaoTest {

    @Test
    public void forceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).update(sysUserModel, true);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertNull(sysUser.getUserName());
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).update(sysUserModel, SysUserModel::getUserName);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertNull(sysUser.getUserName());
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).saveOrUpdate(sysUserModel, true);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertNull(sysUser.getUserName());
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            getDao(sysUserMapper).saveOrUpdate(sysUserModel, SysUserModel::getUserName);
            SysUser sysUser = getDao(sysUserMapper).getById(sysUserModel.getId());
            assertNull(sysUser.getUserName());
            assertEquals(sysUser.getRole_id(), 0);
        }
    }
}
