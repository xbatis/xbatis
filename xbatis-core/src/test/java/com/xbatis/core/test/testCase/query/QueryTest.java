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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.mybatis.mapper.context.Pager;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.executor.SubQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import com.xbatis.core.test.vo.SysUserHandlerVo;
import db.sql.api.DbType;
import db.sql.api.Getters;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.dbFun.FunctionInterface;
import db.sql.api.impl.tookit.Objects;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class QueryTest extends BaseTest {


    @Test
    public void queryWithTypeHandler() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUserHandlerVo> list = QueryChain.of(sysUserMapper).returnType(SysUserHandlerVo.class).orderBy(SysUser::getId).list();
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 3);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
            assertEquals(list.get(2).getId(), 3);

            assertEquals(list.get(0).getUserName(), null);
            assertEquals(list.get(1).getUserName(), null);
            assertEquals(list.get(2).getUserName(), null);
        }
    }

    @Test
    public void listByIds() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listByIds(1, 2);
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 2);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
        }
    }

    @Test
    public void listByIds2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listByIds(Arrays.asList(1, 2));
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 2);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
        }
    }

    @Test
    public void listByIds3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listByIds(new HashSet<>(Arrays.asList(1, 2)));
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 2);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
        }
    }

    @Test
    public void listWithLimit() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.list(2, where -> where.in(SysUser::getId, 1, 2, 3));
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 2);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
        }
    }

    @Test
    public void listWithLimit2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.list(2, WhereUtil.create().in(SysUser::getId, 1, 2, 3));
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 2);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
        }
    }

    @Test
    public void listAll() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = sysUserMapper.listAll();
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 3);
        }
    }

    @Test
    public void onDBTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SysUser sysUser = sysUserMapper.dbAdapt(selector -> {
                selector.when(DbType.H2, (dbType) -> {
                    return sysUserMapper.getById(1);
                }).when(DbType.MYSQL, (dbType) -> {
                    return sysUserMapper.getById(2);
                }).otherwise((dbType) -> {
                    return sysUserMapper.getById(3);
                });
            });

            if (TestDataSource.DB_TYPE == DbType.H2) {
                assertEquals(sysUser.getId(), 1);
            } else if (TestDataSource.DB_TYPE == DbType.MYSQL) {
                assertEquals(sysUser.getId(), 2);
            } else {
                assertEquals(sysUser.getId(), 3);
            }
        }
    }

    @Test
    public void simpleSelect() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1).and()
                    .get();
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setRole_id(0);
            assertEquals(eqSysUser, sysUser, "单表部分select检测");

            sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2).and()
                    .get();
            assertEquals(2, sysUser.getId(), "单表部分select检测");
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1).and()
                    .get();
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setRole_id(0);
            assertEquals(eqSysUser, sysUser, "单表部分select检测");

            sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2).and()
                    .get();
            assertEquals(2, sysUser.getId(), "单表部分select检测");
        }
    }

    @Test
    public void getByIdTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setRole_id(0);
            eqSysUser.setPassword("123");
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));
            assertEquals(eqSysUser, sysUser, "getById检测");
        }
    }

    @Test
    public void getWithWhere() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.get(where -> {
                where.eq(SysUser::getId, 2);
            });
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setRole_id(1);
            eqSysUser.setPassword("123456");
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));
            assertEquals(eqSysUser, sysUser, "getWithWhere检测");
        }
    }

    @Test
    public void existsWithWhere() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            boolean exists = sysUserMapper.exists(where -> {
                where.eq(SysUser::getId, 2);
            });
            assertEquals(exists, true);
        }
    }

    @Test
    public void cursorWithWhere() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            sysUserMapper.cursor(where -> {
                where.eq(SysUser::getId, 2);
            }).forEach(item -> assertEquals(item.getId(), 2));
        }
    }

    @Test
    public void innerJoinTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(Getters.of(SysUser::getId, SysUser::getUserName, SysUser::getRole_id))
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .get();

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setRole_id(1);
            assertEquals(eqSysUser, sysUser, "返回单表，innerJoin检测");
        }
    }

    @Test
    public void innerJoinCursorTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            try (Cursor<SysUser> sysUserCursor = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .join(SysUser::getRole_id, SysRole::getId)
                    .eq(SysUser::getId, 2)
                    .cursor()) {
                assertInstanceOf(Cursor.class, sysUserCursor);
                SysUser sysUser = null;
                for (SysUser entity : sysUserCursor) {
                    assertNull(sysUser);
                    sysUser = entity;
                }
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(2);
                eqSysUser.setUserName("test1");
                eqSysUser.setRole_id(1);
                assertEquals(eqSysUser, sysUser);
            } catch (IOException e) {

            }
        }
    }

    @Test
    public void groupByTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<Integer> counts = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.count())
                    .from(SysUser.class)
                    .groupBy(SysUser::getRole_id)
                    .orderBy(SysUser::getRole_id)
                    .returnType(Integer.class)
                    .list();

            assertEquals(counts.get(0), Integer.valueOf(1), "groupBy");
            assertEquals(counts.get(1), Integer.valueOf(2), "groupBy");
        }
    }

    @Test
    public void orderbyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .orderByDesc(SysUser::getRole_id, SysUser::getId)
                    .limit(1)
                    .get();
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(3);
            eqSysUser.setUserName("test2");
            eqSysUser.setRole_id(1);
            assertEquals(sysUser, eqSysUser, "orderby");
        }
    }

    @Test
    public void orderbyNullTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .orderBy(OrderByDirection.DESC_NULLS_FIRST, SysUser::getRole_id, SysUser::getId)
                    .limit(1)
                    .get();
            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(3);
            eqSysUser.setUserName("test2");
            eqSysUser.setRole_id(1);
            assertEquals(sysUser, eqSysUser, "orderby");
        }
    }

    @Test
    public void havingTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id, FunctionInterface::count)
                    .from(SysUser.class)
                    .groupBy(SysUser::getRole_id)
                    .having(SysUser::getRole_id, c -> c.gt(0))
                    .returnType(Integer.class)
                    .get();

            assertEquals(count, Integer.valueOf(2), "having");
        }
    }

    @Test
    public void count1Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            final LongAdder adder = new LongAdder();
            Integer count = QueryChain.of(sysUserMapper)
                    .selectCount1(c -> c.as(SysUser::getId))
                    .from(SysUser.class)
                    .returnType(Integer.class, (cnt) -> {
                        System.out.println(">>>" + cnt);
                        adder.add(cnt);
                    })
                    .count();
            assertEquals(count, Integer.valueOf(3), "count1");
            assertEquals(adder.intValue(), Integer.valueOf(3), "count1");
        }
    }

    @Test
    public void testDistinctCount() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            Pager pager = Pager.of(1);


            Pager<SysUser> sysUserPager = QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .orderBy(SysUser::getId)
                    .paging(pager);
            assertEquals(sysUserPager.getTotal(), Integer.valueOf(1), "count1");


            Integer count = QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .orderBy(SysUser::getId)
                    .count();
            assertEquals(count, Integer.valueOf(1), "count1");
        }

    }

    @Test
    public void countAllTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count = QueryChain.of(sysUserMapper)
                    .selectCountAll()
                    .from(SysUser.class)
                    .count();
            assertEquals(count, Integer.valueOf(3), "countAll");
        }
    }

    @Test
    public void pagingTestTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = QueryChain.of(sysUserMapper)
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .orderBy(SysUser::getId)
                    .paging(Pager.of(1, 2));

            assertEquals(pager.getTotal(), Integer.valueOf(3), "paging Total");
            assertEquals(pager.getResults().size(), 2, "paging Results size");
            assertEquals(pager.getTotalPage(), Integer.valueOf(2), "paging TotalPage");
        }
    }

    @Test
    public void pagingTestTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = QueryChain.of(sysUserMapper)
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .orderBy(SysUser::getId)
                    .optimizeOptions(optimizeOptions -> optimizeOptions.optimizeJoin(false).optimizeOrderBy(false))
                    .paging(Pager.of(2));

            assertEquals(pager.getTotal(), Integer.valueOf(3), "paging Total");
            assertEquals(pager.getResults().size(), 2, "paging Results size");
            assertEquals(pager.getTotalPage(), Integer.valueOf(2), "paging TotalPage");
        }
    }

    @Test
    public void existsMethodTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .connect(query -> {
                        query.exists(SubQuery.create()
                                .select1()
                                .from(SysUser.class)
                                .eq(SysUser::getId, query.$().field(SysUser::getId))
                                .isNotNull(SysUser::getPassword)
                                .orderBy(SysUser::getId)
                                .limit(1)
                        );
                    })
                    .list();


            assertEquals(list.size(), 2, "exists size");

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setRole_id(1);
            assertEquals(list.get(1), eqSysUser, "exists");
        }
    }

    @Test
    public void inSubQueryTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .connect(queryChain -> {
                        queryChain.in(SysUser::getId, new SubQuery()
                                .select(SysUser::getId)
                                .select(SysUser::getUserName)
                                .selectIgnore(SysUser::getUserName)
                                .from(SysUser.class)
                                .connect(subQuery -> {
                                    subQuery.eq(SysUser::getId, queryChain.$().field(SysUser::getId));
                                    subQuery.eq(SysUser::getId, queryChain.$(SysUser::getId));
                                    subQuery.eq(SysUser::getId, queryChain.$(SysUser.class, "id"));
                                })
                                .andNested(where -> {
                                    where.eq(SysUser::getId, null, Objects::nonNull);
                                    where.andNested(t -> {
                                        t.eq(SysUser::getId, null, Objects::nonNull);
                                    });
                                })
                                .isNotNull(SysUser::getPassword)
                                .connect(subQuery -> {
                                    if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
                                        subQuery.orderBy(SysUser::getId);
                                        subQuery.limit(1);
                                    }
                                })

                        );
                    })
                    .list();

            if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
                assertEquals(list.size(), 2, "inSubQuery size");
            } else {
                assertEquals(list.size(), 2, "inSubQuery size");
            }


            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setRole_id(1);
            assertEquals(list.get(1), eqSysUser, "inSubQuery");
        }
    }


    @Test
    public void selectDistinctTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<Integer> roleIds = QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getRole_id)
                    .from(SysUser.class)
                    .orderBy(SysUser::getRole_id)
                    .returnType(Integer.class)
                    .list();
            assertEquals(roleIds.size(), 2, "selectDistinct");
            assertEquals(roleIds.get(0), Integer.valueOf(0), "selectDistinct");
            assertEquals(roleIds.get(1), Integer.valueOf(1), "selectDistinct");
        }
    }

    @Test
    public void selectDistinctMutiTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getRole_id, c -> c.as("role_id"))
                    .select(SysUser::getId, c -> c.as("id"))
                    .from(SysUser.class)
                    .orderBy(SysUser::getId)
                    .list();
            assertEquals(list.size(), 3, "selectDistinctMuti");
            {
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(1);
                eqSysUser.setRole_id(0);
                assertEquals(list.get(0), eqSysUser, "selectDistinctMuti");
            }
        }
    }

    @Test
    public void unionTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id, SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .union(Query.create()
                            .select(SysUser::getRole_id, SysUser::getId)
                            .from(SysUser.class)
                            .lt(SysUser::getId, 3)
                    )
                    .list();
            assertEquals(list.size(), 2, "union");
            {
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(1);
                eqSysUser.setRole_id(0);
                assertEquals(list.get(0), eqSysUser, "union");
            }

            {
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(2);
                eqSysUser.setRole_id(1);
                assertEquals(list.get(1), eqSysUser, "union");
            }
        }
    }

    @Test
    public void unionAllTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id, SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .unionAll(Query.create()
                            .select(SysUser::getRole_id, SysUser::getId)
                            .from(SysUser.class)
                            .lt(SysUser::getId, 3)
                    )
                    .list();

            list = list.stream().sorted(Comparator.comparing(SysUser::getId)).collect(Collectors.toList());

            assertEquals(list.size(), 3, "unionAll");
            {
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(1);
                eqSysUser.setRole_id(0);
                assertEquals(list.get(0), eqSysUser, "unionAll");
                assertEquals(list.get(1), eqSysUser, "unionAll");
            }

            {
                SysUser eqSysUser = new SysUser();
                eqSysUser.setId(2);
                eqSysUser.setRole_id(1);
                assertEquals(list.get(2), eqSysUser, "unionAll");
            }
        }
    }

    @Test
    public void existsTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            boolean exists = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getRole_id)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .orderBy(SysUser::getId)
                    .like(SysUser::getUserName, "test")
                    .exists();
            assertTrue(exists, "existsTest检测");
        }
    }

    @Test
    public void selectSubQueryTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SubQuery subQuery = SubQuery.create("xx")
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2);


            List<Integer> list = QueryChain.of(sysUserMapper)
                    .select(subQuery)
                    .from(SysUser.class)
                    .orderBy(SysUser::getId)
                    .limit(1)

                    .returnType(Integer.class)
                    .list();

            assertEquals(2, (int) list.get(0), "selectSubQueryTest");
        }
    }

    //@Test
    public Map<String, Object> selectReturnToMap() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            configuration.setMapUnderscoreToCamelCase(true);
            Map<String, Object> map = QueryChain.of(sysUserMapper)
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .returnType(Map.class)
                    .orderBy(SysUser::getId)
                    .limit(1)
                    .get();
            System.out.println(map);
            assertNotNull(map);
            assertEquals(map.get("userName"), "admin");
            assertTrue(map instanceof Map);
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            configuration.setMapUnderscoreToCamelCase(false);
            return QueryChain.of(sysUserMapper)
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .returnType(Map.class)
                    .orderBy(SysUser::getId)
                    .limit(1)
                    .get();

        }
    }

    @Test
    public void queryConfigTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .timeout(1)
                    .fetchSize(1)
                    .fetchDirection(ResultSet.FETCH_FORWARD)
                    .list();
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 3);
        }
    }


    @Test
    public void orderByTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<SysUser> list =
                    QueryChain.of(sysUserMapper)
                            .orderBy(SysUser::getUserName, c -> c.concat("aa"))
                            .list();
            list.stream().forEach(System.out::println);
            assertEquals(list.size(), 3);
        }
    }

    @Test
    public void listByIdsNullTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer[] ids = null;
            assertEquals(sysUserMapper.getByIds(ids), Collections.emptyList());
            assertEquals(sysUserMapper.getByIds(Collections.emptyList()), Collections.emptyList());
        }
    }
}
