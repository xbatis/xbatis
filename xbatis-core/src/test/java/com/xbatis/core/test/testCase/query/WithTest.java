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
import cn.xbatis.core.sql.executor.WithQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.MyDbType;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.DbType;
import db.sql.api.IDbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.WithQueryDataset;
import db.sql.api.impl.tookit.SQLPrinter;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WithTest extends BaseTest {

    @Test
    public void withQuery() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery = WithQuery.create("sub")
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .orderBy(SysRole::getId)
                    .limit(111)
                    .eq(SysRole::getId, 1);

            QueryChain<SysUser> queryChain = QueryChain.of(sysUserMapper);
            queryChain
                    .with(withQuery)
                    .select(withQuery, SysRole::getId, c -> c.as("xx"))
                    .select(withQuery, "id", c -> c.plus(1).as("xx2"))
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .from(withQuery)
                    .eq(SysUser::getRole_id, withQuery.$outerField(SysRole::getId))
                    .orderBy(withQuery, SysRole::getId);

            Pager<SysUser> page = queryChain.paging(Pager.of(100));
            System.out.println(SQLPrinter.sql(queryChain));
            assertEquals(2, page.getResults().size(), "withQuery");
        }
    }

    @Test
    public void withQueryAs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery = WithQuery.create("sub")
                    .select(SysRole::getId, c -> c.as("xid"))
                    .from(SysRole.class)
                    .orderBy(SysRole::getId)
                    .limit(111)
                    .eq(SysRole::getId, 1);

            WithQueryDataset aaa = withQuery.asTable("aaa");
            WithQueryDataset bbb = withQuery.asTable("bbb");

            QueryChain<SysUser> queryChain = QueryChain.of(sysUserMapper);
            queryChain
                    .with(withQuery)
                    .select(aaa.$outerField(SysRole::getId, true))
                    .select(bbb.$outerField(SysRole::getId, true).plus(1).as("xx2"))
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .from(aaa, bbb)

                    .eq(SysUser::getRole_id, aaa.$outerField(SysRole::getId, true))
                    .eq(SysUser::getRole_id, bbb.$outerField(SysRole::getId, true))
                    .orderBy(aaa.$outerField(SysRole::getId, true))
                    .orderBy(bbb.$outerField(SysRole::getId, true));

            Pager<SysUser> page = queryChain.paging(Pager.of(100));
            System.out.println(SQLPrinter.sql(queryChain));
            assertEquals(2, page.getResults().size(), "withQuery");
        }
    }

    @Test
    public void withQueryJoin() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery = WithQuery.create("sub")
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .orderBy(SysRole::getId)
                    .limit(111)
                    .eq(SysRole::getId, 1);

            QueryChain<SysUser> queryChain = QueryChain.of(sysUserMapper);
            queryChain
                    .with(withQuery)
                    .select(withQuery, SysRole::getId, c -> c.as("xx"))
                    .select(withQuery, "id", c -> c.plus(1).as("xx2"))
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .join(SysUser.class, withQuery, on -> on.eq(SysUser::getRole_id, withQuery.$outerField(SysRole::getId)))
                    .orderBy(withQuery, SysRole::getId);

            Pager<SysUser> page = queryChain.paging(Pager.of(100));
            System.out.println(SQLPrinter.sql(queryChain));
            assertEquals(2, page.getResults().size(), "withQuery");


            check("检测with  join", "WITH sub AS ( SELECT  wt.id ,  wt.name ,  wt.create_time FROM sys_role wt WHERE  wt.id = 1 ORDER BY  wt.id ASC LIMIT 111 OFFSET 0) SELECT  sub.id AS xx , ( sub.id + 1) AS xx2 ,  t.id ,  t.password ,  t.role_id ,  t.create_time,  t.user_name  FROM t_sys_user t  INNER JOIN  sub ON  t.role_id =  sub.id ORDER BY  sub.id ASC LIMIT 100 OFFSET 0", queryChain);

        }
    }

    @Test
    public void withQueryMuti() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery1 = WithQuery.create("sub")
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .orderBy(SysRole::getId)
                    .limit(111)
                    .eq(SysRole::getId, 1);

            WithQuery withQuery2 = WithQuery.create("sub2")
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .orderBy(SysRole::getId)
                    .limit(111)
                    .eq(SysRole::getId, 1);


            QueryChain<SysUser> queryChain = QueryChain.of(sysUserMapper);
            queryChain
                    .with(withQuery1, withQuery2)
                    .select(withQuery1, SysRole::getId, c -> c.as("xx"))
                    .select(withQuery2, "id", c -> c.plus(1).as("xx2"))
                    .select(SysUser.class)
                    .from(SysUser.class)
                    .from(withQuery1, withQuery2)
                    .eq(SysUser::getRole_id, withQuery1.$("id"))
                    .eq(SysUser::getRole_id, withQuery2.$("id"))
                    .orderBy(withQuery1, SysRole::getId)
                    .orderBy(withQuery2, SysRole::getId);

            Pager<SysUser> page = queryChain.paging(Pager.of(100));
            System.out.println(SQLPrinter.sql(queryChain));
            assertEquals(2, page.getResults().size(), "withQuery");
        }
    }


    @Test
    public void withRecursiveQuery() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery = WithQuery.create("sub")
                    .recursive("n", "n2")
                    .select("1,1")
                    .dbAdapt((query, selector) -> {
                        selector.when(new IDbType[]{DbType.ORACLE, MyDbType.LIKE_ORACLE}, () -> {
                            query.from(new Table("dual"));
                        }).when(DbType.DB2, () -> {
                            query.from(new Table("SYSIBM.SYSDUMMY1"));
                        }).otherwise();
                    });

            withQuery.unionAll(Query.create()
                    .select("n+1,n2+1")
                    .from(withQuery)
                    .lt(Methods.column("n"), 2)
                    .lt(Methods.column("n2"), 3)
            );

            List<Map<String, Object>> mapList = QueryChain.of(sysUserMapper)
                    .with(withQuery)
                    .selectAll(withQuery)
                    .from(withQuery)
                    .returnMap()
                    .list();

            System.out.println(mapList);
        }
    }


    @Test
    public void withRecursiveQuery2() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            WithQuery withQuery = WithQuery.create("dept_with")
                    .recursive()
                    .select(SysUser::getId, SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getRole_id, 100);

            withQuery.unionAll(Query.create()
                    .select(SysUser::getId, SysUser::getRole_id)
                    .from(SysUser.class)
            );

            List<Map<String, Object>> mapList = QueryChain.of(sysUserMapper)
                    .with(withQuery)
                    .selectAll()
                    .from(withQuery)
                    .returnMap()
                    .list();

            System.out.println(mapList);
        }
    }
}
