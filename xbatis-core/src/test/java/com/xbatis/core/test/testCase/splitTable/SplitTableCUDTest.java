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

package com.xbatis.core.test.testCase.splitTable;

import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.InsertChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import com.xbatis.core.test.DO.SplitTableTest;
import com.xbatis.core.test.mapper.SplitTableTestMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SplitTableCUDTest extends BaseTest {

    @Test
    public void testSplitTableUpdate() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            int cnt = UpdateChain.of(mapper)
                    .eq(SplitTableTest::getSplitId, 1)
                    .set(SplitTableTest::getName, "123")
                    .execute();

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "123");

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).orNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "123");
        }
    }


    @Test
    public void testSplitTableEntityUpdate() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(1);
            splitTableTest.setName("1245");
            splitTableTest.setId(1);
            int cnt = mapper.update(splitTableTest);

            assertEquals(1, cnt);

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).orNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");
        }
    }

    @Test
    public void testSplitTableEntityListUpdate() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(1);
            splitTableTest.setName("1245");
            splitTableTest.setId(1);
            int cnt = mapper.update(Arrays.asList(splitTableTest));

            assertEquals(1, cnt);

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).orNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");
        }
    }


    @Test
    public void testSplitTableEntityUpdate2() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setName("1245");
            splitTableTest.setId(1);
            int cnt = mapper.update(splitTableTest, where -> {
                where.eq(SplitTableTest::getSplitId, 1);
            });

            assertEquals(1, cnt);

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");

            splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).orNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 1);
            }).get();

            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
            assertEquals(splitTableTest.getName(), "1245");
        }
    }


    @Test
    public void testSplitTableInsert() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(3);
            splitTableTest.setName("124");
            mapper.save(splitTableTest);

            assertNotNull(splitTableTest.getId());
            assertEquals(splitTableTest.getSplitId(), 3);
            assertEquals(splitTableTest.getName(), "124");
        }
    }

    @Test
    public void testSplitTableInsertSelect() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            List<SplitTableTest> list = QueryChain.of(mapper).eq(SplitTableTest::getSplitId, 2).list();
            System.out.println(list);


            int cnt = InsertChain.of(mapper)
                    .insertSelect(SplitTableTest::getSplitId, Methods.value(2))
                    .insertSelect(SplitTableTest::getName, Methods.value("12356"))
                    .insertSelectQuery(query -> query.from(SplitTableTest.class).eq(SplitTableTest::getSplitId, 3))
                    .execute();

            assertEquals(cnt, 1);

            list = QueryChain.of(mapper).eq(SplitTableTest::getSplitId, 2).list();
            System.out.println(list);
            assertEquals(list.size(), 2);

            SplitTableTest splitTableTest = mapper.get(where -> where.eq(SplitTableTest::getId, 3).eq(SplitTableTest::getSplitId, 2));

            assertEquals(splitTableTest.getSplitId(), 2);
            assertEquals(splitTableTest.getName(), "12356");
        }
    }

    @Test
    public void testSplitTableDelete() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            int cnt = DeleteChain.of(mapper)
                    .eq(SplitTableTest::getSplitId, 3)
                    .execute();

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 3);
            }).get();

            assertNull(splitTableTest);
        }
    }

    @Test
    public void testSplitTableEntityDelete() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(3);
            splitTableTest.setId(2);
            int cnt = mapper.delete(splitTableTest);

            assertEquals(1, cnt);

            splitTableTest = QueryChain.of(mapper).isNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 3);
            }).get();

            assertNull(splitTableTest);
        }
    }

    @Test
    public void testSplitTableEntityDeleteBatch() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(3);
            splitTableTest.setId(2);
            int cnt = mapper.delete(Arrays.asList(splitTableTest, splitTableTest));

            assertEquals(1, cnt);

            splitTableTest = QueryChain.of(mapper).isNull(SplitTableTest::getName).andNested(conditionChain -> {
                conditionChain.eq(SplitTableTest::getSplitId, 3);
            }).get();

            assertNull(splitTableTest);
        }
    }

    @Test
    public void testSplitTableInsertBatch() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTest splitTableTest = new SplitTableTest();
            splitTableTest.setSplitId(3);
            splitTableTest.setName("124");
            mapper.saveBatch(Arrays.asList(splitTableTest));

            assertNotNull(splitTableTest.getId());
            assertEquals(splitTableTest.getSplitId(), 3);
            assertEquals(splitTableTest.getName(), "124");
        }
    }
}
