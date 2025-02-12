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

import cn.mybatis.mp.core.sql.executor.chain.QueryChain;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.model.SysUserModel;
import com.mybatis.mp.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelUpdateTest extends BaseTest {

    @Test
    public void forceUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.update(sysUserModel, true);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.update(sysUserModel, SysUserModel::getUserName);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel, true);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel, SysUserModel::getUserName);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }


    @Test
    public void forceUpdateListTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.updateModel(Collections.singletonList(sysUserModel), true);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.updateModel(Collections.singletonList(sysUserModel), SysUserModel::getUserName);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdateModel(Collections.singletonList(sysUserModel), true);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = QueryChain.of(sysUserMapper).returnType(SysUserModel.class)
                    .eq(SysUser::getId, 1).get();
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdateModel(Collections.singletonList(sysUserModel), SysUserModel::getUserName);
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }
}
