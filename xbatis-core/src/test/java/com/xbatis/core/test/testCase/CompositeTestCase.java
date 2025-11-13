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

import cn.xbatis.core.tenant.TenantContext;
import com.alibaba.fastjson.JSON;
import com.xbatis.core.test.DO.CompositeTest;
import com.xbatis.core.test.mapper.CompositeTestMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CompositeTestCase extends BaseTest {

    @BeforeEach
    public void before() {
        TenantContext.registerTenantGetter(() -> {
            return 1;
        });
    }

    @Test
    public void save() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            CompositeTestMapper compositeTestMapper = session.getMapper(CompositeTestMapper.class);
            this.save(compositeTestMapper);
        }
    }

    private void save(CompositeTestMapper compositeTestMapper) {
        CompositeTest compositeTest = new CompositeTest();
        //compositeTest.setId(1L);

        compositeTestMapper.save(compositeTest);
        assertEquals(compositeTest.getVersion(), 1);
        assertEquals(compositeTest.getTenantId(), 1);
        assertEquals(compositeTest.getDeleted().intValue(), 0);
        compositeTest = compositeTestMapper.getById(compositeTest.getId());
        System.out.println(JSON.toJSONString(compositeTest));
    }

    @Test
    public void update() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            CompositeTestMapper compositeTestMapper = session.getMapper(CompositeTestMapper.class);
            this.save(compositeTestMapper);

            CompositeTest compositeTest = compositeTestMapper.getById(1);
            compositeTestMapper.update(compositeTest);

            compositeTest = compositeTestMapper.getById(1);
            assertEquals(compositeTest.getVersion(), 2);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);


            compositeTestMapper.update(compositeTest);

            compositeTest = compositeTestMapper.getById(1);
            assertEquals(compositeTest.getVersion(), 3);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            compositeTest = compositeTestMapper.getById(1);
            assertNull(compositeTest);
        }
    }


    @Test
    public void updateWhere() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            CompositeTestMapper compositeTestMapper = session.getMapper(CompositeTestMapper.class);
            this.save(compositeTestMapper);

            CompositeTest compositeTest = compositeTestMapper.getById(1);
            compositeTestMapper.update(compositeTest);

            compositeTest = compositeTestMapper.getById(1);
            assertEquals(compositeTest.getVersion(), 2);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);


            compositeTest.setId(null);
            compositeTestMapper.update(compositeTest, where -> where.eq(CompositeTest::getId, 1));

            compositeTest = compositeTestMapper.getById(1);
            assertEquals(compositeTest.getVersion(), 3);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);


            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            compositeTest = compositeTestMapper.getById(1);
            assertNull(compositeTest);
        }
    }

    @Test
    public void delete() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            CompositeTestMapper compositeTestMapper = session.getMapper(CompositeTestMapper.class);
            this.save(compositeTestMapper);

            CompositeTest compositeTest = compositeTestMapper.getById(1);
            assertEquals(compositeTest.getVersion(), 1);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);

            TenantContext.registerTenantGetter(() -> {
                return 2;
            });
            compositeTestMapper.delete(compositeTest);

            compositeTest.setId(null);
            compositeTestMapper.delete(where -> where.eq(CompositeTest::getId, 1));

            TenantContext.registerTenantGetter(() -> {
                return null;
            });
            compositeTest = compositeTestMapper.getById(1);

            assertEquals(compositeTest.getVersion(), 1);
            assertEquals(compositeTest.getTenantId(), 1);
            assertEquals(compositeTest.getDeleted().intValue(), 0);

            TenantContext.registerTenantGetter(() -> {
                return 1;
            });
            compositeTestMapper.delete(compositeTest);
            compositeTest = compositeTestMapper.getById(1);
            assertNull(compositeTest);
        }
    }
}
