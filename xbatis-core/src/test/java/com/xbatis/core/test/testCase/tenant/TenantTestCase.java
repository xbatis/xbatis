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

package com.xbatis.core.test.testCase.tenant;

import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.tenant.TenantContext;
import cn.xbatis.core.tenant.TenantId;
import com.xbatis.core.test.DO.TenantTest;
import com.xbatis.core.test.mapper.TenantTestMapper;
import com.xbatis.core.test.model.TenantModel;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TenantTestCase extends BaseTest {

    @BeforeEach
    public void before() {
        TenantContext.registerTenantGetter(() -> {
            return 1;
        });
    }

    @Test
    public void insertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);
            System.out.println(tenantTest);
            assertNotNull(tenantTest.getId());
            assertEquals(1, (int) tenantTestMapper.getById(tenantTest.getId()).getTenantId());
        }
    }

    @Test
    public void updateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);

            tenantTest.setName("我是2");
            int updateCnt = tenantTestMapper.update(tenantTest);
            System.out.println(tenantTest);
            assertEquals(1, (int) tenantTest.getTenantId());
            assertEquals(1, updateCnt);


            updateCnt = tenantTestMapper.update(tenantTest, where -> where.eq(TenantTest::getTenantId, 1).or().eq(TenantTest::getName, "abc"));
            System.out.println(tenantTest);
            assertEquals(1, (int) tenantTest.getTenantId());
            assertEquals(1, updateCnt);


            int deleteCnt = tenantTestMapper.delete(where -> where.eq(TenantTest::getTenantId, 1).or().eq(TenantTest::getName, "abc"));
            assertEquals(1, deleteCnt);


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            tenantTest.setName("我是3");
            updateCnt = tenantTestMapper.update(tenantTest);
            assertEquals(updateCnt, 0);
        }
    }


    @Test
    public void insertWithModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantModel tenantTest = new TenantModel();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);
            System.out.println(tenantTest);
            assertNotNull(tenantTest.getId());
            assertEquals(1, (int) tenantTestMapper.getById(tenantTest.getId()).getTenantId());
        }
    }

    @Test
    public void updateWithModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantModel tenantTest = new TenantModel();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);

            tenantTest.setName("我是2");
            tenantTestMapper.update(tenantTest);
            System.out.println(tenantTest);
            assertEquals(1, (int) tenantTest.getTenantId());


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            tenantTest.setName("我是3");
            int updateCnt = tenantTestMapper.update(tenantTest);
            assertEquals(updateCnt, 0);
        }
    }


    @Test
    public void deleteTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);


            check("", "delete FROM tenant_test WHERE  (id = '" + tenantTest.getId() + "') AND (tenant_id = 1)", DeleteChain.of(tenantTestMapper).delete(TenantTest.class).from(TenantTest.class).eq(TenantTest::getId, tenantTest.getId()));
            int count = QueryChain.of(tenantTestMapper).count();
            assertEquals(count, 1);

            TenantContext.registerTenantGetter(() -> {
                return 2;
            });

            count = QueryChain.of(tenantTestMapper).count();
            assertEquals(count, 0);

            check("", "delete FROM tenant_test WHERE (id = '" + tenantTest.getId() + "') AND (tenant_id = 2)", DeleteChain.of(tenantTestMapper).delete(TenantTest.class).from(TenantTest.class).eq(TenantTest::getId, tenantTest.getId()));

            TenantContext.registerTenantGetter(() -> {
                return 1;
            });

            count = QueryChain.of(tenantTestMapper).count();
            assertEquals(count, 1);

            tenantTestMapper.deleteById(tenantTest.getId());

            count = QueryChain.of(tenantTestMapper).count();
            assertEquals(count, 0);


        }
    }

    @Test
    public void deleteEntity() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);

            int cnt = tenantTestMapper.deleteById(tenantTest.getId());
            assertEquals(cnt, 1);

            cnt = tenantTestMapper.delete(where -> where.eq(TenantTest::getTenantId, 123).or().eq(TenantTest::getName, "abc"));
            assertEquals(cnt, 0);
        }
    }


    @Test
    public void query() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            tenantTest = new TenantTest();
            tenantTest.setName("我是2");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);


            List<TenantTest> list = QueryChain.of(tenantTestMapper).list();
            assertEquals(list.size(), 1);
            assertEquals(list.get(0).getName(), "我是2");
        }
    }

    @Test
    public void queryMulti() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            TenantTestMapper tenantTestMapper = session.getMapper(TenantTestMapper.class);
            TenantTest tenantTest = new TenantTest();
            tenantTest.setName("我是1");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            tenantTest = new TenantTest();
            tenantTest.setName("我是2");
            tenantTest.setCreateTime(LocalDateTime.now());
            tenantTestMapper.save(tenantTest);

            TenantContext.registerTenantGetter(() -> {
                return new TenantId(1, 2, 3);
            });
            List<?> list = QueryChain.of(tenantTestMapper).list();
            assertEquals(list.size(), 2);
        }
    }
}
