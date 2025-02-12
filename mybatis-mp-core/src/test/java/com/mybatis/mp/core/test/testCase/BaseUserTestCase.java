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

package com.mybatis.mp.core.test.testCase;

import cn.mybatis.mp.core.sql.executor.chain.QueryChain;
import com.mybatis.mp.core.test.DO.BaseIDSysUser;
import com.mybatis.mp.core.test.DO.BaseRoleIdSysUserVo;
import com.mybatis.mp.core.test.DO.BaseRoleIdSysUserVo2;
import com.mybatis.mp.core.test.mapper.SysUserIDMapper;
import com.mybatis.mp.core.test.model.BaseIDSysUserModel;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseUserTestCase extends BaseTest {

    private void save(SqlSession session, BaseIDSysUser baseIDSysUser) {
        try {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            sysUserMapper.save(baseIDSysUser);
        } finally {
            session.close();
        }

    }

    private void save(SqlSession session, BaseIDSysUserModel baseIDSysUser) {
        try {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            sysUserMapper.save(baseIDSysUser);
        } finally {
            session.close();
        }
    }

    @Test
    public void save() {
        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
            return;
        }

        BaseIDSysUser baseIDSysUser = new BaseIDSysUser();
        baseIDSysUser.setUserName("aaa2");
        baseIDSysUser.setRole_id(1);
        baseIDSysUser.setPassword("xx");
        baseIDSysUser.setCreate_time(LocalDateTime.now());
        for (int i = 0; i < 20; i++) {
            try {
                save(this.sqlSessionFactory.openSession(false), baseIDSysUser);
                break;
            } catch (Exception e) {
                if (i > 10) {
                    e.printStackTrace();
                }
                System.out.println(i);
            }
        }

        System.out.println(baseIDSysUser);
        System.out.println(baseIDSysUser.getId().getClass());
        assertEquals(baseIDSysUser.getId().getClass(), Long.class);
    }

    @Test
    public void update() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            BaseIDSysUser baseIDSysUser = new BaseIDSysUser();
            baseIDSysUser.setId(1L);
            baseIDSysUser.setUserName("aaa2");
            baseIDSysUser.setRole_id(1);
            baseIDSysUser.setPassword("xx");
            baseIDSysUser.setCreate_time(LocalDateTime.now());
            sysUserMapper.update(baseIDSysUser);
            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            assertEquals(baseIDSysUser.getId().getClass(), Long.class);
        }
    }

    @Test
    public void saveModel() {
        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
            return;
        }

        BaseIDSysUserModel baseIDSysUser = new BaseIDSysUserModel();
        baseIDSysUser.setUserName("aaa2");
        baseIDSysUser.setRole_id(1);
        baseIDSysUser.setPassword("xx");
        baseIDSysUser.setCreate_time(LocalDateTime.now());
        for (int i = 0; i < 20; i++) {
            try {
                save(this.sqlSessionFactory.openSession(false), baseIDSysUser);
                break;
            } catch (Exception e) {
                System.out.println(i);
            }
        }

        System.out.println(baseIDSysUser);
        System.out.println(baseIDSysUser.getId().getClass());
        assertEquals(baseIDSysUser.getId().getClass(), Long.class);

    }

    @Test
    public void updateModel() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            BaseIDSysUserModel baseIDSysUser = new BaseIDSysUserModel();
            baseIDSysUser.setId(1L);
            baseIDSysUser.setUserName("aaa2");
            baseIDSysUser.setRole_id(1);
            baseIDSysUser.setPassword("xx");
            baseIDSysUser.setCreate_time(LocalDateTime.now());
            sysUserMapper.update(baseIDSysUser);
            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            assertEquals(baseIDSysUser.getId().getClass(), Long.class);
        }
    }

    @Test
    public void test1() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            BaseIDSysUser baseIDSysUser = QueryChain.of(sysUserMapper)
                    .from(BaseIDSysUser.class)
                    .eq(BaseIDSysUser::getId, 2)
                    .get();

            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            assertEquals(baseIDSysUser.getId(), 2);
        }
    }

    @Test
    public void test2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserIDMapper sysUserMapper = session.getMapper(SysUserIDMapper.class);
            BaseRoleIdSysUserVo baseRoleIdSysUserVo = QueryChain.of(sysUserMapper)
                    .from(BaseIDSysUser.class)
                    .eq(BaseIDSysUser::getId, 2)
                    .returnType(BaseRoleIdSysUserVo.class)
                    .get();

            System.out.println(baseRoleIdSysUserVo);
            System.out.println(baseRoleIdSysUserVo.getId().getClass());
            assertEquals(baseRoleIdSysUserVo.getId(), 2);
            assertEquals(baseRoleIdSysUserVo.getRole_id(), "1");

            BaseIDSysUser baseIDSysUser = baseRoleIdSysUserVo.getBaseIDSysUser();
            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            assertEquals(baseIDSysUser.getId(), 2);

            baseIDSysUser = baseRoleIdSysUserVo.getBaseIDSysUser2();
            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            assertEquals(baseIDSysUser.getId(), 2);


            baseIDSysUser = baseRoleIdSysUserVo.getBaseIDSysUsers().get(0);
            System.out.println(baseIDSysUser);
            System.out.println(baseIDSysUser.getId().getClass());
            System.out.println(baseIDSysUser.getRole_id().getClass());
            assertEquals(baseIDSysUser.getId(), 2);

            BaseRoleIdSysUserVo2 baseRoleIdSysUserVo2 = baseRoleIdSysUserVo.getBaseRoleIdSysUserVo();
            System.out.println(baseRoleIdSysUserVo2.getId().getClass());
            System.out.println(baseRoleIdSysUserVo2.getRole_id().getClass());
            assertEquals(baseRoleIdSysUserVo2.getId(), 2);
            assertEquals(baseRoleIdSysUserVo2.getRole_id(), "1");


            baseRoleIdSysUserVo2 = baseRoleIdSysUserVo.getBaseRoleIdSysUserVo2();
            System.out.println(baseRoleIdSysUserVo2.getId().getClass());
            System.out.println(baseRoleIdSysUserVo2.getRole_id().getClass());
            assertEquals(baseRoleIdSysUserVo2.getId(), 2);
            assertEquals(baseRoleIdSysUserVo2.getRole_id(), "1");

        }
    }
}
