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

package com.xbatis.core.test.testCase;

import cn.xbatis.core.mybatis.mapper.context.Pager;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.ReqEntity;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.mapper.SysRoleMapper;
import com.xbatis.core.test.vo.XmlNestedResultMap;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class XmlPagingTestCase extends BaseTest {

    @Test
    public void xmlPagingList() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<SysRole> list = sysRoleMapper.xmlPagingList(Pager.of(1), 1, 2);

            assertEquals(1, list.get(0).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);

            list = sysRoleMapper.xmlPagingList(Pager.of(2), 1, 2);

            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(1).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void xmlPaging() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager = sysRoleMapper.xmlPaging(Pager.of(1), 1, 2);
            assertEquals(2, pager.getTotal());
            assertEquals(1, pager.getResults().get(0).getId());
            assertNotNull(pager.getResults().get(0).getCreateTime());


            pager = sysRoleMapper.xmlPaging(Pager.of(2), 1, 2);
            assertEquals(2, pager.getTotal());
            assertEquals(2, pager.getResults().get(1).getId());
            assertNotNull(pager.getResults().get(1).getCreateTime());
            System.out.println(pager);
        }
    }

    @Test
    public void xmlPagingNoQueryCount() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager = sysRoleMapper.xmlPaging(Pager.of(-1), 1, 2);
            assertEquals(2, pager.getTotal());
            assertEquals(1, pager.getResults().get(0).getId());
            assertNotNull(pager.getResults().get(0).getCreateTime());
        }
    }

    @Test
    public void xmlPaging3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager = sysRoleMapper.xmlPaging3(Pager.of(1), 1);
            assertEquals(1, pager.getTotal());
            assertEquals(1, pager.getResults().get(0).getId());
            assertNotNull(pager.getResults().get(0).getCreateTime());
        }
    }

    @Test
    public void xmlNoParamsPaging() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager = sysRoleMapper.xmlPaging(Pager.of(1), 1, 2);

            pager = sysRoleMapper.xmlPaging2(Pager.of(2));
            assertEquals(2, pager.getTotal());
            assertEquals(2, pager.getResults().get(1).getId());
            System.out.println(pager);
            assertNull(pager.getResults().get(1).getCreateTime());
        }
    }

    @Test
    public void xmlDynamicPaging() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager;

            //pager = sysRoleMapper.xmlPaging(Pager.of(1), 1, 1);

            pager = sysRoleMapper.xmlDynamicPaging(Pager.of(2), 1, 2, 1);
            assertEquals(1, pager.getTotal());
            assertEquals(1, pager.getResults().get(0).getId());
            assertNotNull(pager.getResults().get(0).getCreateTime());
        }
    }

    @Test
    public void annotationPaging() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Pager<SysRole> pager = sysRoleMapper.annotationPaging(Pager.of(2), 1, 2);
            assertEquals(2, pager.getTotal());
            assertEquals(2, pager.getResults().get(1).getId());
            assertNotNull(pager.getResults().get(0).getCreateTime());
            System.out.println(pager);
        }
    }

    @Test
    public void selectCustomSql() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<SysRole> list = sysRoleMapper.selectCustomSql(WhereUtil.create().in(SysRole::getId, 1, 2));
            assertEquals(2, list.size());
            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void selectCustomSql2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<SysRole> list = sysRoleMapper.selectCustomSql2(WhereUtil.create().in(SysRole::getId, 1, 2));
            assertEquals(2, list.size());
            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void selectQueryCustomSql() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Query query = Query.create()
                    .select(SysRole.class)
                    .select(Methods.value(true).concat(1))
                    .select(Methods.value(1).as("xx"))
                    .from(SysRole.class)
                    .in(SysRole::getId, 1, 2)
                    .orderBy(SysRole::getId, SysRole::getCreateTime);

            List<SysRole> list = sysRoleMapper.selectQueryCustomSql(query);
            assertEquals(2, list.size());
            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void selectQueryCustomSql2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Query query = Query.create()
                    .select(SysRole.class)
                    .select(Methods.value(1))
                    .from(SysRole.class)
                    .in(SysRole::getId, 1, 2)
                    .orderBy(SysRole::getId, SysRole::getCreateTime);

            List<SysRole> list = sysRoleMapper.selectQueryCustomSql2(query, 1);
            assertEquals(2, list.size());
            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void selectQueryCustomSql3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            Query<?> query = Query.create()
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .in(SysRole::getId, 1, 2);

            if (TestDataSource.DB_TYPE != DbType.SQL_SERVER) {
                query.orderBy(SysRole::getId, SysRole::getCreateTime);
            }

            List<SysRole> list = sysRoleMapper.selectQueryCustomSql3(query);
            assertEquals(2, list.size());
            assertEquals(2, list.get(1).getId());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);
        }
    }

    @Test
    public void withSqlSessionTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<SysRole> list = sysRoleMapper.getBasicMapper().withSqlSession(sqlSession -> {
                return sqlSession.selectList(SysRoleMapper.class.getName() + ".xmlPaging", new HashMap() {{
                    put("id", 1);
                    put("id2", 2);
                }});
            });
            assertEquals(2, list.size());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);

            list = sysRoleMapper.getBasicMapper().withSqlSession(SysRoleMapper.class.getName() + ".xmlPaging", (statement, sqlSession) -> {
                return sqlSession.selectList(statement, new HashMap() {{
                    put("id", 1);
                    put("id2", 2);
                }});
            });
            assertEquals(2, list.size());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);

            list = sysRoleMapper.getBasicMapper().withSqlSession(".SysRole:basicXmlPaging", new HashMap() {{
                put("id", 1);
                put("id2", 2);
            }}, (statement, params, sqlSession) -> {
                return sqlSession.selectList(statement, params);
            });
            assertEquals(2, list.size());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);


            list = sysRoleMapper.getBasicMapper().withSqlSession(".SysRole:basicXmlPaging2", new HashMap() {{
                put("id", 1);
                put("id2", 2);
            }}, (statement, params, sqlSession) -> {
                return sqlSession.selectList(statement, params);
            });
            assertEquals(2, list.size());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);

            list = sysRoleMapper.getBasicMapper().withSqlSession(SysRole.class, "basicXmlPaging2", new HashMap() {{
                put("id", 1);
                put("id2", 2);
            }}, (statement, params, sqlSession) -> {
                return sqlSession.selectList(statement, params);
            });
            assertEquals(2, list.size());
            assertNotNull(list.get(0).getCreateTime());
            System.out.println(list);


            Integer count = sysRoleMapper.getBasicMapper().withSqlSession(SysRole.class, "count", (statement, sqlSession) -> {
                return sqlSession.selectOne(statement);
            });
            assertEquals(2, count);
        }
    }

    @Test
    public void testXmlNestedResultMap() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<XmlNestedResultMap> list = sysRoleMapper.selectXmlNestedResultMap();
            System.out.println(list);
        }
    }

    @Test
    public void testXmlReqEntityMap() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            ReqEntity reqEntity = new ReqEntity();
            reqEntity.getParams().put("type", 1);
            List<Map> list = sysRoleMapper.testSuperMapParams(reqEntity);
            System.out.println(list);
        }
    }

    @Test
    public void testselectAll2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<Map> list = sysRoleMapper.selectAll2();
            System.out.println(list);
        }
    }

}
