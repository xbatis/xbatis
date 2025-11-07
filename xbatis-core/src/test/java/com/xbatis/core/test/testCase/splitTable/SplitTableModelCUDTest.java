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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SplitTableTest;
import com.xbatis.core.test.mapper.SplitTableTestMapper;
import com.xbatis.core.test.model.SplitTableTestModel;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SplitTableModelCUDTest extends BaseTest {


    @Test
    public void testSplitTableEntityUpdate() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTestModel splitTableTestModel = new SplitTableTestModel();
            splitTableTestModel.setSplitId(1);
            splitTableTestModel.setName("1245");
            splitTableTestModel.setId(1);
            int cnt = mapper.update(splitTableTestModel);

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
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
            SplitTableTestModel splitTableTestModel = new SplitTableTestModel();
            splitTableTestModel.setSplitId(1);
            splitTableTestModel.setName("1245");
            splitTableTestModel.setId(1);
            int cnt = mapper.updateModel(Arrays.asList(splitTableTestModel));

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
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
            SplitTableTestModel splitTableTestModel = new SplitTableTestModel();
            splitTableTestModel.setName("1245");
            splitTableTestModel.setId(1);
            int cnt = mapper.update(splitTableTestModel, where -> {
                where.eq(SplitTableTest::getSplitId, 1);
            });

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
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
    public void testSplitTableEntityUpdate3() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            SplitTableTestModel splitTableTestModel = new SplitTableTestModel();
            splitTableTestModel.setName("1245");
            splitTableTestModel.setId(1);
            splitTableTestModel.setSplitId(1);
            int cnt = mapper.update(splitTableTestModel, where -> {
                //where.eq(SplitTableTest::getSplitId, 1);
            });

            assertEquals(1, cnt);

            SplitTableTest splitTableTest = QueryChain.of(mapper).isNotNull(SplitTableTest::getName).andNested(conditionChain -> {
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
            SplitTableTestModel splitTableTest = new SplitTableTestModel();
            splitTableTest.setSplitId(3);
            splitTableTest.setName("124");
            mapper.save(splitTableTest);

            assertNotNull(splitTableTest.getId());
            assertEquals(splitTableTest.getSplitId(), 3);
            assertEquals(splitTableTest.getName(), "124");
        }
    }

    @Test
    public void testSplitTableInsertBatch() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);

            SplitTableTestModel splitTableTest1 = new SplitTableTestModel();
            splitTableTest1.setSplitId(3);
            splitTableTest1.setName("1111");

            SplitTableTestModel splitTableTest2 = new SplitTableTestModel();

            splitTableTest2.setSplitId(4);
            splitTableTest2.setName("2222");

            mapper.saveModelBatch(Arrays.asList(splitTableTest1, splitTableTest2));

            assertNotNull(splitTableTest1.getId());
            assertEquals(splitTableTest1.getSplitId(), 3);
            assertEquals(splitTableTest1.getName(), "1111");

            assertNotNull(splitTableTest2.getId());
            assertEquals(splitTableTest2.getSplitId(), 4);
            assertEquals(splitTableTest2.getName(), "2222");

            SplitTableTest obj1 = mapper.get(where -> where.eq(SplitTableTest::getSplitId, 3).eq(SplitTableTest::getName, "1111"));
            assertNotNull(obj1);

            SplitTableTest obj2 = mapper.get(where -> where.eq(SplitTableTest::getSplitId, 4).eq(SplitTableTest::getName, "2222"));
            assertNotNull(obj2);
        }
    }

}
