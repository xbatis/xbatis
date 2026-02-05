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

package com.xbatis.core.test.testCase.delete;

import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.MyDbType;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.IDbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeleteTest extends BaseTest {

    @Test
    public void deleteAll() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            sysUserMapper.deleteAll();
            int cnt = sysUserMapper.count(where -> {
            });
            assertEquals(0, cnt);
        }
    }

    @Test
    public void onDBTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = DeleteChain.of(sysUserMapper)
                    .dbAdapt((deleteChain, selector) -> {
                        selector.when(DbType.H2, () -> {
                                    deleteChain.eq(SysUser::getId, 3);
                                })
                                .when(new IDbType[]{DbType.MYSQL, MyDbType.LIKE_MYSQL}, () -> {
                                    deleteChain.eq(SysUser::getId, 2);
                                })
                                .otherwise((dbType) -> {
                                    deleteChain.eq(SysUser::getId, 1);
                                });
                    })
                    .execute();
            assertEquals(cnt, 1);
            if (TestDataSource.DB_TYPE == DbType.H2) {
                assertNull(sysUserMapper.getById(3));
            } else if (TestDataSource.DB_TYPE == DbType.MYSQL || TestDataSource.DB_TYPE.getDbModel() == DbModel.MYSQL) {
                assertNull(sysUserMapper.getById(2));
            } else {
                assertNull(sysUserMapper.getById(1));
            }
        }
    }

    @Test
    public void deleteIdTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            System.out.println(sysUser);
            int cnt = sysUserMapper.deleteById(1);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertNull(sysUser);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }


    @Test
    public void deleteIdsTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.deleteByIds(1, 2);
            assertEquals(cnt, 2);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 1);
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.deleteByIds(Arrays.asList(1, 2));
            assertEquals(cnt, 2);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 1);
        }
    }

    @Test
    public void deleteEntityTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.delete(sysUserMapper.getById(1));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }

    @Test
    public void deleteEntityTests() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.delete(Collections.singleton(sysUserMapper.getById(1)));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }


    @Test
    public void deleteWithWhereTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.delete(where -> where.eq(SysUser::getId, 1));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }

    @Test
    public void deleteWithWhere2Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            int cnt = sysUserMapper.delete(WhereUtil.create().eq(SysUser::getId, 1));
            assertEquals(cnt, 1);
            List<SysUser> list = QueryChain.of(sysUserMapper).list();
            assertEquals(list.size(), 2);
        }
    }
}
