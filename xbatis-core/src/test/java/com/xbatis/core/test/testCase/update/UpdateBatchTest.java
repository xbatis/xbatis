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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.model.SysUserModel;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UpdateBatchTest extends BaseTest {

    @Test
    public void entityBatchUpdate() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listAll();
            list.stream().forEach(sysUser -> {
                sysUser.setPassword("123456789");
            });
            sysUserMapper.updateBatch(list);
            list = sysUserMapper.listAll();
            list.stream().forEach(sysUser -> {
                assertEquals(sysUser.getPassword(), "123456789");
            });
        }
    }

    @Test
    public void entityBatchUpdateIgnore() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listAll();
            list.stream().forEach(sysUser -> {
                sysUser.setPassword("123456789");
            });
            sysUserMapper.updateBatch(list, SysUser::getUserName);
            list = sysUserMapper.listAll();
            list.stream().forEach(sysUser -> {
                assertNotEquals(sysUser.getPassword(), "123456789");
            });
        }
    }

    @Test
    public void modelBatchUpdate() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUserModel> list = QueryChain.of(sysUserMapper).returnType(SysUserModel.class).list();
            list.stream().forEach(sysUser -> {
                sysUser.setPassword("123456789");
            });
            sysUserMapper.updateBatchModel(list);
            list = QueryChain.of(sysUserMapper).returnType(SysUserModel.class).list();
            list.stream().forEach(sysUser -> {
                assertEquals(sysUser.getPassword(), "123456789");
            });
        }
    }

    @Test
    public void modelBatchUpdateIgnore() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUserModel> list = QueryChain.of(sysUserMapper).returnType(SysUserModel.class).list();
            list.stream().forEach(sysUser -> {
                sysUser.setPassword("123456789");
            });
            sysUserMapper.updateBatchModel(list, SysUserModel::getUserName);
            list = QueryChain.of(sysUserMapper).returnType(SysUserModel.class).list();
            list.stream().forEach(sysUser -> {
                assertNotEquals(sysUser.getPassword(), "123456789");
            });
        }
    }
}
