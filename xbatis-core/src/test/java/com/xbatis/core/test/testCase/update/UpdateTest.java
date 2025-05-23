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
import cn.xbatis.core.sql.executor.SubQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.model.SysUserModel;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import db.sql.api.Getter;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateTest extends BaseTest {

    public static int runUpdateSql(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    @Test
    public void forceUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.update(sysUser, true);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.update(sysUser, SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.saveOrUpdate(sysUser, true);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.saveOrUpdate(sysUser, SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.update(Collections.singletonList(sysUser), true);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.update(Collections.singletonList(sysUser), SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.saveOrUpdate(Collections.singletonList(sysUser), true);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceUpdateListTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName(null);
            int cnt = sysUserMapper.saveOrUpdate(Collections.singletonList(sysUser), SysUser::getUserName);
            assertEquals(cnt, 1);
            sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
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
            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals(sysUser.getUserName(), null);
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
                assertEquals(sysUserMapper.getById(3).getUserName(), "xx123");
            } else if (TestDataSource.DB_TYPE == DbType.MYSQL) {
                assertEquals(sysUserMapper.getById(2).getUserName(), "xx123");
            } else {
                assertEquals(sysUserMapper.getById(1).getUserName(), "xx123");
            }
        }
    }

    @Test
    public void dbTypeUpdateTest() {
        if (TestDataSource.DB_TYPE == DbType.H2) {
            int updateCnt = -1;
            try (Connection conn = this.dataSource.getConnection()) {

                updateCnt = runUpdateSql(conn, "update t_sys_user set user_name='123' where id=2");
                assertEquals(updateCnt, 1);

                updateCnt = runUpdateSql(conn, "update t_sys_user t set t.user_name='123' where t.id=2");
                assertEquals(updateCnt, 1);
                // h2  不支持 update from 语法

                // h2 不支持 update join
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (TestDataSource.DB_TYPE == DbType.MYSQL) {
            int updateCnt = -1;
            try (Connection conn = this.dataSource.getConnection()) {

                updateCnt = runUpdateSql(conn, "update t_sys_user set user_name='123' where id=2");
                assertEquals(updateCnt, 1);

                updateCnt = runUpdateSql(conn, "update t_sys_user t set t.user_name='123' where t.id=2");
                assertEquals(updateCnt, 1);

                // mysql  不支持 update from 语法
                // mysql 支持 update join
                // mysql 支持 同时修改 多张表 ，但是 join 语句是紧跟 update table 之后的
                updateCnt = runUpdateSql(conn, "update t_sys_user t inner join sys_role t2 on t.role_id=t2.id  set t.user_name='123',t2.name='1234' where t.id=2");
                assertEquals(updateCnt, 2);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (TestDataSource.DB_TYPE == DbType.PGSQL || TestDataSource.DB_TYPE == DbType.OPEN_GAUSS) {
            int updateCnt = -1;
            // pg 只支持 修改单个表数据

            try (Connection conn = this.dataSource.getConnection()) {
                // pg 不支持 update set 别名.列 = '123'
                updateCnt = runUpdateSql(conn, "update t_sys_user set user_name='123' where id=2");
                assertEquals(updateCnt, 1);

                updateCnt = runUpdateSql(conn, "update t_sys_user t set user_name='123'  where t.id=2");
                assertEquals(updateCnt, 1);

                //PG 支持 from ( from 表 和 update 一般意义为不同表)
                updateCnt = runUpdateSql(conn, "update t_sys_user t set user_name=t2.name from sys_role t2  where t.id=2");
                assertEquals(updateCnt, 1);

                //PG 支持 join 但是 必须加上 from
                updateCnt = runUpdateSql(conn, "update t_sys_user t set user_name='123' from sys_role t2 inner join sys_role t3 on t2.id=t3.id  where t.id=2 and t.role_id=t2.id");
                assertEquals(updateCnt, 1);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (TestDataSource.DB_TYPE == DbType.ORACLE) {
            int updateCnt = -1;
            // ORACLE 只支持 修改单个表数据  不支持 from 不支持join 可以通过 子查询修改

            try (Connection conn = this.dataSource.getConnection()) {

                updateCnt = runUpdateSql(conn, "update t_sys_user set user_name='123' where id=2");
                assertEquals(updateCnt, 1);

                // ORACLE 支持 update set 别名.列 = '123'
                updateCnt = runUpdateSql(conn, "update t_sys_user t set t.user_name='123'  where t.id=2");
                assertEquals(updateCnt, 1);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (TestDataSource.DB_TYPE == DbType.SQL_SERVER) {
            int updateCnt = -1;
            // sqlserver 只支持 修改单个表数据

            try (Connection conn = this.dataSource.getConnection()) {

                updateCnt = runUpdateSql(conn, "update t_sys_user set user_name='123' where id=2");
                assertEquals(updateCnt, 1);

                // sqlserver 支持 update set 别名.列 = '123' 但是 需要在from 一下
                updateCnt = runUpdateSql(conn, "update t  set t.user_name='123' from  t_sys_user t where id=2");
                assertEquals(updateCnt, 1);

                // sqlserver 支持连表 from多表的方式 相当于 inner join
                updateCnt = runUpdateSql(conn, "update t set t.user_name=t2.name from  t_sys_user t,sys_role t2 where t.id=2 and t.role_id=t2.id");
                assertEquals(updateCnt, 1);

                //sqlserver 支持连表 from+join的方式
                updateCnt = runUpdateSql(conn, "update  t set user_name='123' from t_sys_user t  inner join sys_role t2 on t.role_id=t2.id  where t.id=2  ");
                assertEquals(updateCnt, 1);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void updatePlus1() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = sysUserMapper.getById(1);
            int cnt = UpdateChain.of(sysUserMapper)
                    .set(SysUser::getRole_id, c -> c.plus(1))
                    .eq(SysUser::getId, 1)
                    .execute();
            assertEquals(cnt, 1);
            SysUser sysUser = sysUserMapper.getById(1);
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
            int cnt = sysUserMapper.update(updateSysUser);
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
            int cnt = sysUserMapper.update(updateSysUser, SysUser::getPassword);
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
            int cnt = sysUserMapper.update(updateSysUser);
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
            int cnt = sysUserMapper.update(updateSysUser, SysUserModel::getPassword);
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
    public void updateEntityWithWhereTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SysUser updateSysUser = new SysUser();
            updateSysUser.setUserName("adminxx");
            int cnt = sysUserMapper.update(updateSysUser, where -> where.eq(SysUser::getId, 1));
            assertEquals(cnt, 1);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword("123");
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));


            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体with where修改");
        }
    }

    @Test
    public void updateEntityTestForceUpdateWithWhere() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SysUser updateSysUser = new SysUser();
            updateSysUser.setId(null);
            updateSysUser.setUserName("adminxx");

            UpdateStrategy<SysUser> updateStrategy = new UpdateStrategy<>();
            updateStrategy.on(where -> where.eq(SysUser::getId, 1)).forceFields(SysUser::getPassword);

            int cnt = sysUserMapper.update(updateSysUser, updateStrategy);
            assertEquals(cnt, 1);


            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(1);
            eqSysUser.setUserName("adminxx");
            eqSysUser.setPassword(null);
            eqSysUser.setRole_id(0);
            eqSysUser.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));


            List<SysUser> list = QueryChain.of(sysUserMapper).eq(SysUser::getUserName, "adminxx").list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0), eqSysUser, "实体with where 强制修改");
        }
    }

    @Test
    public void mutiTableUpdateTest() {

        if (TestDataSource.DB_TYPE == DbType.H2 || TestDataSource.DB_TYPE == DbType.SQLITE) {
            //H2 不支持
            return;
        } else if (TestDataSource.DB_TYPE == DbType.ORACLE) {
            //ORACLE 不支持
            return;
        } else if (TestDataSource.DB_TYPE == DbType.DB2) {
            //ORACLE 不支持
            return;
        } else {
            try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
                SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
                UpdateChain updateChain = UpdateChain.of(sysUserMapper)
                        .update(SysUser.class)
                        .set(SysUser::getUserName, "joinUpdateTest")
                        // .set(SysRole::getName,"joinUpdateTest")
                        //.from(SysUser.class)
                        .eq(SysUser::getId, 2);

                if (TestDataSource.DB_TYPE == DbType.PGSQL || TestDataSource.DB_TYPE == DbType.OPEN_GAUSS || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                    updateChain.from(SysRole.class)
                            .eq(SysUser::getId, SysRole::getId);
                } else if (TestDataSource.DB_TYPE == DbType.SQL_SERVER) {
                    updateChain.from(SysUser.class);
                    updateChain.join(SysUser.class, SysRole.class);

                    //or
                    //updateChain.from(SysUser.class, SysRole.class);
                    //updateChain.eq(SysUser::getRole_id,SysRole::getId);
                } else {
                    updateChain.join(SysUser.class, SysRole.class);
                }

                int updateCnt = updateChain.execute();
                assertEquals(updateCnt, 1);
            }
        }
    }

    @Test
    public void updateNull() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = sysUserMapper.getById(1);
            old.setUserName(null);
            sysUserMapper.update(old);

            SysUser sysUser = sysUserMapper.getById(1);
            assertEquals("admin", sysUser.getUserName());
        }
    }

    @Test
    public void updateEmpty() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = sysUserMapper.getById(1);
            old.setUserName("");
            sysUserMapper.update(old);

            SysUser sysUser = sysUserMapper.getById(1);
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertEquals(null, sysUser.getUserName());
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
                    .set(SysUser::getRole_id, (Getter<SysRole>) SysRole::getId)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .execute();

            assertEquals(cnt, 1);
        }
    }
}
