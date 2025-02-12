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

package com.mybatis.mp.core.test.testCase.dao.update;

import cn.mybatis.mp.core.sql.executor.SubQuery;
import cn.mybatis.mp.core.sql.executor.chain.QueryChain;
import cn.mybatis.mp.core.sql.executor.chain.UpdateChain;
import com.mybatis.mp.core.test.DO.SysRole;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.model.SysUserModel;
import com.mybatis.mp.core.test.testCase.TestDataSource;
import com.mybatis.mp.core.test.testCase.dao.BaseDaoTest;
import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.TableField;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UpdateTest extends BaseDaoTest {


    @Test
    public void forceUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).update(sysUser, true);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).update(sysUser, SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).saveOrUpdate(sysUser, true);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).saveOrUpdate(sysUser, SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateListTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).update(Collections.singletonList(sysUser), true);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateListTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).update(Collections.singletonList(sysUser), SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateListTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).saveOrUpdate(Collections.singletonList(sysUser), true);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void forceUpdateListTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            sysUser.setUserName(null);
            int cnt = getDao(sysUserMapper).saveOrUpdate(Collections.singletonList(sysUser), SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }


    @Test
    public void nullUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = UpdateChain.of(sysUserMapper)
                    .set(SysUser::getUserName, null, true)
                    .eq(SysUser::getId, 1)
                    .execute();

            assertEquals(cnt, 1);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser.getUserName());
        }
    }

    @Test
    public void onDBTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = UpdateChain.of(sysUserMapper)
                    .set(SysUser::getUserName, "xx123")
                    .set(true, SysUser::getRole_id, 1)
                    .set(SysUser::getPassword, "xx123", StringUtils::hasText)
                    .dbAdapt((updateChain, selector) -> {
                        selector.when(DbType.H2, () -> {
                            updateChain.eq(SysUser::getId, 3);
                        }).when(DbType.MYSQL, () -> {
                            updateChain.eq(SysUser::getId, 2);
                        }).otherwise(() -> {
                            updateChain.eq(SysUser::getId, 1);
                        });
                    })
                    .execute();
            assertEquals(cnt, 1);
            if (TestDataSource.DB_TYPE == DbType.H2) {
                assertEquals(getDao(sysUserMapper).getById(3).getUserName(), "xx123");
            } else if (TestDataSource.DB_TYPE == DbType.MYSQL) {
                assertEquals(getDao(sysUserMapper).getById(2).getUserName(), "xx123");
            } else {
                assertEquals(getDao(sysUserMapper).getById(1).getUserName(), "xx123");
            }
        }
    }


    @Test
    public void updatePlus1() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = getDao(sysUserMapper).getById(1);
            int cnt = UpdateChain.of(sysUserMapper)
                    .set(SysUser::getRole_id, (Function<TableField, Cmd>) c -> c.plus(1))
                    .eq(SysUser::getId, 1)
                    .execute();
            assertEquals(cnt, 1);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            assertEquals(old.getRole_id() + 1, sysUser.getRole_id());
        }
    }

    @Test
    public void updateEntityTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);


            SysUser updateSysUser = new SysUser();
            updateSysUser.setId(1);
            updateSysUser.setUserName("adminxx");
            int cnt = getDao(sysUserMapper).update(updateSysUser);
            assertEquals(cnt, 1);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword("123");
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));

            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体修改");


            cnt = UpdateChain.of(sysUserMapper)
                    .connect(updateChain -> {
                        updateChain.set(SysUser::getRole_id, SubQuery.create()
                                .select(SysRole::getId)
                                .from(SysRole.class)
                                .eq(SysRole::getId, updateChain.$().field(SysUser::getRole_id))
                                .orderBy(SysRole::getCreateTime)
                                .limit(1)
                        );
                    })
                    .eq(SysUser::getId, 1)
                    .execute();
            assertEquals(cnt, 1);
        }
    }

    @Test
    public void updateEntityTestForceUpdate() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);


            SysUser updateSysUser = new SysUser();
            updateSysUser.setId(1);
            updateSysUser.setUserName("adminxx");
            int cnt = getDao(sysUserMapper).update(updateSysUser, SysUser::getPassword);
            assertEquals(cnt, 1);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword(null);
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));

            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体强制修改");
        }
    }


    @Test
    public void updateModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryChain.of(sysUserMapper).list();

            SysUserModel updateSysUser = new SysUserModel();
            updateSysUser.setId(1);
            updateSysUser.setUserName("adminxx");
            int cnt = getDao(sysUserMapper).update(updateSysUser);
            assertEquals(cnt, 1);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword("123");
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));


            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体model修改");
        }
    }

    @Test
    public void updateModelTestForceUpdate() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);


            SysUserModel updateSysUser = new SysUserModel();
            updateSysUser.setId(1);
            updateSysUser.setUserName("adminxx");
            int cnt = getDao(sysUserMapper).update(updateSysUser, SysUserModel::getPassword);
            assertEquals(cnt, 1);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword(null);
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));

            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体model强制修改");
        }
    }


    @Test
    public void updateNull() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = getDao(sysUserMapper).getById(1);
            old.setUserName(null);
            getDao(sysUserMapper).update(old);

            SysUser sysUser = getDao(sysUserMapper).getById(1);
            assertEquals("admin", sysUser.getUserName());
        }
    }

    @Test
    public void updateEmpty() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = getDao(sysUserMapper).getById(1);
            old.setUserName("");
            getDao(sysUserMapper).update(old);

            SysUser sysUser = getDao(sysUserMapper).getById(1);
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertNull(sysUser.getUserName());
            } else {
                assertEquals("", sysUser.getUserName());
            }

        }
    }

    @Test
    public void updateJoin() {
        if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = UpdateChain.of(sysUserMapper)
                    .set(SysUser::getRole_id, SysRole::getId)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .execute();

            assertEquals(cnt, 1);
        }
    }
}
