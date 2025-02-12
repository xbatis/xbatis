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

import cn.mybatis.mp.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.mybatis.mp.core.sql.util.WhereUtil;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityForceWhereUpdateTest extends BaseTest {

    @Test
    public void forceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setUserName(null);
            sysUserMapper.update(sysUserModel, true, where -> where.eq(SysUser::getId, sysUserModel.getId()));
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);

            sysUserModel.setId(null);
            sysUserModel.setUserName(null);

            UpdateStrategy<SysUser> updateStrategy = new UpdateStrategy<>();
            updateStrategy.on(where -> where.eq(SysUser::getId, 1))
                    .forceFields(SysUser::getUserName);

            sysUserMapper.update(sysUserModel, updateStrategy);

            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }


    @Test
    public void forceInsertTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);
            sysUserMapper.update(sysUserModel, true, WhereUtil.create().eq(SysUser::getId, 1));
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);

            UpdateStrategy<SysUser> updateStrategy = new UpdateStrategy<>();
            updateStrategy.on(WhereUtil.create().eq(SysUser::getId, 1))
                    .forceFields(SysUser::getUserName);

            sysUserMapper.update(sysUserModel, updateStrategy);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

    @Test
    public void forceInsertTest7() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = sysUserMapper.getById(1);
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel, updateStrategy -> {
                updateStrategy.forceFields(SysUser::getUserName);
                updateStrategy.on(where -> where.eq(SysUser::getId, 1));
            });
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }

}
