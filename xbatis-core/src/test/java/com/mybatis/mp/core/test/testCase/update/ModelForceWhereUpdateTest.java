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

package com.xbatis.core.test.testCase.update;

import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.model.SysUserModel;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelForceWhereUpdateTest extends BaseTest {

    @Test
    public void forceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);
            sysUserMapper.update(sysUserModel, true, where -> where.eq(SysUser::getId, 1));
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }


    @Test
    public void forceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);
            UpdateStrategy<SysUserModel> updateStrategy = new UpdateStrategy();
            updateStrategy.on(where -> where.eq(SysUser::getId, 1));
            updateStrategy.forceFields(SysUserModel::getUserName);
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
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
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
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setId(null);
            sysUserModel.setUserName(null);
            UpdateStrategy<SysUserModel> updateStrategy = new UpdateStrategy();
            updateStrategy.on(WhereUtil.create().eq(SysUser::getId, 1));
            updateStrategy.forceFields(SysUserModel::getUserName);
            sysUserMapper.update(sysUserModel, updateStrategy);

            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
            assertEquals(sysUser.getRole_id(), 0);
        }
    }
}
