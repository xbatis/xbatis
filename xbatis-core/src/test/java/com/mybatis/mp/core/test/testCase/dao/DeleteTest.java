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

package com.xbatis.core.test.testCase.dao;

import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeleteTest extends BaseDaoTest {


    @Test
    public void onDBTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = DeleteChain.of(sysUserMapper)
                    .dbAdapt((deleteChain, selector) -> {
                        selector.when(DbType.H2, () -> {
                                    deleteChain.eq(SysUser::getId, 3);
                                })
                                .when(DbType.MYSQL, () -> {
                                    deleteChain.eq(SysUser::getId, 2);
                                })
                                .otherwise(() -> {
                                    deleteChain.eq(SysUser::getId, 1);
                                });
                    })
                    .execute();
            assertEquals(cnt, 1);
            if (TestDataSource.DB_TYPE == DbType.H2) {
                assertNull(getDao(sysUserMapper).getById(3));
            } else if (TestDataSource.DB_TYPE == DbType.MYSQL) {
                assertNull(getDao(sysUserMapper).getById(2));
            } else {
                assertNull(getDao(sysUserMapper).getById(1));
            }
        }
    }

    @Test
    public void deleteIdTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = getDao(sysUserMapper).getById(1);
            System.out.println(sysUser);
            int cnt = getDao(sysUserMapper).deleteById(1);
            assertEquals(cnt, 1);
            sysUser = getDao(sysUserMapper).getById(1);
            assertNull(sysUser);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }


    @Test
    public void deleteIdsTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = getDao(sysUserMapper).deleteByIds(1, 2);
            assertEquals(cnt, 2);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 1);
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = getDao(sysUserMapper).deleteByIds(Arrays.asList(1, 2));
            assertEquals(cnt, 2);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 1);
        }
    }

    @Test
    public void deleteEntityTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = getDao(sysUserMapper).delete(getDao(sysUserMapper).getById(1));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }

    @Test
    public void deleteEntityTests() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = getDao(sysUserMapper).delete(Collections.singleton(getDao(sysUserMapper).getById(1)));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }

}
