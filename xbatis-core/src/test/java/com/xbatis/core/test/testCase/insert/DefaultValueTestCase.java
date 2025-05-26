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

package com.xbatis.core.test.testCase.insert;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.MybatisBatchUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.executor.chain.InsertChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.DefaultValue2Test;
import com.xbatis.core.test.DO.DefaultValueTest;
import com.xbatis.core.test.DO.TestEnum;
import com.xbatis.core.test.mapper.DefaultValueTestMapper;
import com.xbatis.core.test.model.DefaultValue2ModelTest;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import db.sql.api.cmd.GetterFields;
import db.sql.api.impl.cmd.Methods;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultValueTestCase extends BaseTest {


    @Test
    public void insertAllowNullTest() {
        if (TestDataSource.DB_TYPE != DbType.H2 && TestDataSource.DB_TYPE != DbType.MYSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);

            InsertChain.of(mapper)
                    .insert(DefaultValueTest.class)
                    .fields(DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getValue4, DefaultValueTest::getCreateTime)
                    .values(Arrays.asList("a", 1, null, LocalDateTime.now()), true)
                    .values(Arrays.asList("a", 2, 1, LocalDateTime.now()))
                    .execute();


        }
    }


    @Test
    public void defaultValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            //defaultValueTest.setValue3(TestEnum.X1);
            DbType.H2.getKeywords().add("value3");
            mapper.save(defaultValueTest);
            assertNotNull(defaultValueTest.getCreateTime());

            System.out.println(defaultValueTest);
            assertNotNull(defaultValueTest.getId());
            assertNotNull(defaultValueTest.getValue1());
            assertNotNull(defaultValueTest.getValue2());

            assertNotNull(defaultValueTest.getCreateTime());
            defaultValueTest = mapper.getById(defaultValueTest.getId());
            System.out.println(defaultValueTest);
            assertEquals(TestEnum.X1, defaultValueTest.getValue3());

            QueryChain.of(mapper).forSearch(true)
                    .in(DefaultValueTest::getValue1, Arrays.asList(TestEnum.X1, TestEnum.X2)).list();
        }
    }


    @Test
    public void updateValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            defaultValueTest.setValue3(TestEnum.X1);


            mapper.save(defaultValueTest);
            defaultValueTest.setValue2(null);
            mapper.update(defaultValueTest);

            assertEquals(2, defaultValueTest.getValue2());
            assertEquals(4, defaultValueTest.getValue4());
            defaultValueTest = mapper.getById(defaultValueTest.getId());
            assertEquals(2, defaultValueTest.getValue2());
            assertEquals(4, defaultValueTest.getValue4());
        }
    }

    @Test
    public void batchInsert() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            DefaultValueTest defaultValueTest2 = new DefaultValueTest();
            DefaultValueTest defaultValueTest3 = new DefaultValueTest();

            List<DefaultValueTest> list = Arrays.asList(defaultValueTest, defaultValueTest2, defaultValueTest3);

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest.setId(11);
                defaultValueTest2.setId(12);
                defaultValueTest3.setId(13);
                mapper.saveBatch(list, DefaultValueTest::getId, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
            } else {
                mapper.saveBatch(list, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                System.out.println(list);
                if (TestDataSource.DB_TYPE == DbType.SQLITE || TestDataSource.DB_TYPE == DbType.MARIA_DB) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(list.get(i).getId(), i + 1);
                }
            }

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest = mapper.getById(11);
            } else {
                defaultValueTest = mapper.getById(1);
            }

            assertNotNull(defaultValueTest.getId());
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertNull(defaultValueTest.getValue1());
            } else {
                assertNotNull(defaultValueTest.getValue1());
            }

            assertNotNull(defaultValueTest.getValue2());
            assertNotNull(defaultValueTest.getCreateTime());

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest2 = mapper.getById(12);
            } else {
                defaultValueTest2 = mapper.getById(2);
            }

            assertNotNull(defaultValueTest2.getId());
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertNull(defaultValueTest2.getValue1());
            } else {
                assertNotNull(defaultValueTest2.getValue1());
            }
            assertNotNull(defaultValueTest2.getValue2());
            assertNotNull(defaultValueTest2.getCreateTime());
        }
    }

    @Test
    public void batchInsert2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            DefaultValueTest defaultValueTest2 = new DefaultValueTest();
            DefaultValueTest defaultValueTest3 = new DefaultValueTest();

            List<DefaultValueTest> list = Arrays.asList(defaultValueTest, defaultValueTest2, defaultValueTest3);

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest.setId(11);
                defaultValueTest2.setId(12);
                defaultValueTest3.setId(13);
                mapper.saveBatch(list);
            } else {
                mapper.saveBatch(list);
                System.out.println(list);
                if (TestDataSource.DB_TYPE == DbType.SQLITE || TestDataSource.DB_TYPE == DbType.MARIA_DB) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(list.get(i).getId(), i + 1);
                }
            }

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest = mapper.getById(11);
            } else {
                defaultValueTest = mapper.getById(1);
            }

            assertNotNull(defaultValueTest.getId());
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertNull(defaultValueTest.getValue1());
            } else {
                assertNotNull(defaultValueTest.getValue1());
            }

            assertNotNull(defaultValueTest.getValue2());
            assertNotNull(defaultValueTest.getCreateTime());

            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                defaultValueTest2 = mapper.getById(12);
            } else {
                defaultValueTest2 = mapper.getById(2);
            }

            assertNotNull(defaultValueTest2.getId());
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertNull(defaultValueTest2.getValue1());
            } else {
                assertNotNull(defaultValueTest2.getValue1());
            }
            assertNotNull(defaultValueTest2.getValue2());
            assertNotNull(defaultValueTest2.getCreateTime());
        }
    }

    @Test
    public void insertSelect() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            //defaultValueTest.setId(1);
            defaultValueTest.setValue2(13);
            defaultValueTest.setValue3(TestEnum.X2);
            mapper.save(defaultValueTest);

            Integer maxId = defaultValueTest.getId();

            System.out.println(defaultValueTest);
            defaultValueTest = mapper.getById(1);
            System.out.println(defaultValueTest);
            InsertChain insert;
            if (TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2) {
                insert = InsertChain.of(mapper)
                        .insert(DefaultValueTest.class)
                        //.insertIgnore()
                        .fields(DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getValue3, DefaultValueTest::getCreateTime)
                        .fromSelect(Query
                                .create()
                                .select(DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getValue3, DefaultValueTest::getCreateTime)
                                .from(DefaultValueTest.class)
                                .eq(DefaultValueTest::getId, 1)
                        );
            } else {
                insert = InsertChain.of(mapper)
                        .insert(DefaultValueTest.class)
                        //.insertIgnore()
                        .fields(DefaultValueTest::getId, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getValue3, DefaultValueTest::getCreateTime)
                        .fromSelect(Query
                                .create()
                                .select(Methods.value(maxId + 1))
                                .select(DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getValue3, DefaultValueTest::getCreateTime)
                                .from(DefaultValueTest.class)
                                .eq(DefaultValueTest::getId, 1)
                        );
            }


            insert.execute();
            defaultValueTest = mapper.getById(maxId + 1);
            assertEquals(defaultValueTest.getValue2(), 13);
            assertEquals(defaultValueTest.getValue3(), TestEnum.X2);
        }
    }

    @Test
    public void insertSelect3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest defaultValueTest = new DefaultValueTest();
            //defaultValueTest.setId(1);
            defaultValueTest.setValue1("1");
            defaultValueTest.setValue2(13);
            defaultValueTest.setValue3(TestEnum.X2);
            defaultValueTest.setValue4(1);
            mapper.save(defaultValueTest);

            Integer maxId = defaultValueTest.getId();

            System.out.println(defaultValueTest);
            defaultValueTest = mapper.getById(1);
            System.out.println(defaultValueTest);

            InsertChain.of(mapper)
                    .insert(DefaultValueTest.class)
                    .dbAdapt((insertChain, selector) -> {
                        selector.when(new DbType[]{DbType.SQL_SERVER, DbType.DB2}, () -> {

                        }).otherwise(() -> {
                            insertChain.insertSelect(DefaultValueTest::getId, Methods.value(maxId + 1));
                        });
                    })

                    .insertSelect(DefaultValueTest::getValue1, GetterFields.of(DefaultValueTest::getValue1, DefaultValueTest::getValue1), cs -> cs[0].concat(cs[1]))
                    .insertSelect(DefaultValueTest::getValue2, DefaultValueTest::getValue2)
                    .insertSelect(DefaultValueTest::getValue3, DefaultValueTest::getValue3)
                    .insertSelect(DefaultValueTest::getValue4, DefaultValueTest::getValue4)
                    .insertSelect(DefaultValueTest::getCreateTime, DefaultValueTest::getCreateTime)
                    .insertSelectQuery(query -> query.from(DefaultValueTest.class).eq(DefaultValueTest::getId, 1))
                    .execute();


            defaultValueTest = mapper.getById(maxId + 1);
            assertEquals(defaultValueTest.getValue1(), "11");
            assertEquals(defaultValueTest.getValue2(), 13);
            assertEquals(defaultValueTest.getValue3(), TestEnum.X2);
        }
    }

    @Test
    public void testBatch() {
        if (TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2) {
            return;
        }
        int length = 100;
        List<DefaultValueTest> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(new DefaultValueTest());
        }

        long startTime = 0;
        startTime = System.currentTimeMillis();
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            for (DefaultValueTest item : list) {
                mapper.save(item);
            }
        }

        long speedTime1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int xx2 = MybatisBatchUtil.batchSave(this.sqlSessionFactory, DefaultValueTestMapper.class, list);
        long speedTime2 = System.currentTimeMillis() - startTime;
        assertEquals(xx2, length);

        startTime = System.currentTimeMillis();
        List<DefaultValueTest> saveBatchList = new ArrayList<>(100);
        int xx3 = MybatisBatchUtil.batch(this.sqlSessionFactory, (session) -> {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            int saveCnt = 0;
            int j = 0;
            for (int i = 0; i < length; i++) {
                saveBatchList.add(list.get(i));
                if (i != 0 && i % 100 == 0) {
                    j++;
                    mapper.saveBatch(saveBatchList, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                    saveBatchList.clear();
                }
                if (i != 0 && j == 5) {
                    j = 0;
                    saveCnt += MybatisBatchUtil.getEffectCnt(session.flushStatements());
                }
            }
            if (!saveBatchList.isEmpty()) {
                mapper.saveBatch(saveBatchList, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                saveBatchList.clear();
                saveCnt += MybatisBatchUtil.getEffectCnt(session.flushStatements());
            }
            assertEquals(length, saveCnt);
            assertEquals(length, QueryChain.of(mapper).count());
            return saveCnt;

        });
        assertEquals(xx3, length);
        long speedTime3 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int xx4 = MybatisBatchUtil.batchMulti(sqlSessionFactory, BasicMapper.class, list, 10, 100, (session, mapper, subList) -> {
            mapper.saveBatch(subList);
        });
        assertEquals(xx4, length);
        long speedTime4 = System.currentTimeMillis() - startTime;

        System.out.println("普通for save：" + (speedTime1));
        System.out.println("MybatisBatchUtil批量：" + (speedTime2));
        System.out.println("MybatisBatchUtil批量+mapper.saveBatch原生批量：" + (speedTime3));
        System.out.println("MybatisBatchUtil批量 batchMulti：" + (speedTime4));
    }

    @Test
    public void testBatch2() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            DefaultValueTest d = new DefaultValueTest();
            d.setCreateTime(LocalDateTime.now());
            d.setValue1("1");
            d.setValue2(1);
            d.setValue3(TestEnum.X1);
            d.setValue4(3);
            mapper.save(d);
            System.out.println(d);
        }
        int length = 1000;
        List<DefaultValueTest> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            DefaultValueTest d = new DefaultValueTest();
            d.setCreateTime(LocalDateTime.now());
            d.setValue1("1");
            d.setValue2(1);
            d.setValue3(TestEnum.X1);
            d.setValue4(3);
            list.add(d);
        }


        long start = System.currentTimeMillis();


        int count = MybatisBatchUtil.batch(sqlSessionFactory, DefaultValueTestMapper.class, list, 1000, (session, mapper, data) -> {
            mapper.save(data);
        });

//        int count=MybatisBatchUtil.batchMulti(sqlSessionFactory,DefaultValueTestMapper.class,list,10,100,(session, mapper, sublist) -> {
//            mapper.saveBatch(sublist);
//        });
        System.out.println((System.currentTimeMillis() - start) + "毫秒：" + count);
    }

    @Test
    public void testSaveBatch() {
        if (TestDataSource.DB_TYPE == DbType.DB2) {
            return;
        }
        int length = 100;
        List<DefaultValueTest> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(new DefaultValueTest());
        }
        List<DefaultValueTest> saveBatchList = new ArrayList<>(100);
        MybatisBatchUtil.batch(this.sqlSessionFactory, (session) -> {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            int saveCnt = 0;
            int j = 0;
            for (int i = 0; i < length; i++) {
                saveBatchList.add(list.get(i));
                if (i != 0 && i % 100 == 0) {
                    j++;
                    mapper.saveBatch(saveBatchList, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                    saveBatchList.clear();
                }
                if (i != 0 && j == 5) {
                    j = 0;
                    saveCnt += MybatisBatchUtil.getEffectCnt(session.flushStatements());
                }
            }
            if (!saveBatchList.isEmpty()) {
                mapper.saveBatch(saveBatchList, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                saveBatchList.clear();
                saveCnt += MybatisBatchUtil.getEffectCnt(session.flushStatements());
            }

            assertEquals(length, saveCnt);
            assertEquals(length, QueryChain.of(mapper).count());
            return saveCnt;

        });
        System.out.println(list);
    }

    @Test
    public void deaultValueCheckSameTest() {
        XbatisGlobalConfig.setDynamicValue("NOW2", (clazz, type) -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Integer((int) (System.currentTimeMillis() / 1000));
        });
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValue2Test defaultValue2Test = new DefaultValue2Test();
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().save(defaultValue2Test);
            assertEquals(defaultValue2Test.getValue2(), defaultValue2Test.getValue4());
            assertNotNull(defaultValue2Test.getValue2());

            defaultValue2Test.setValue2(null);
            defaultValue2Test.setValue4(null);
            mapper.getBasicMapper().update(defaultValue2Test);
            assertEquals(defaultValue2Test.getValue2(), defaultValue2Test.getValue4());
            assertNotNull(defaultValue2Test.getValue2());

        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {


            List<DefaultValue2Test> list = new ArrayList<>();
            list.add(new DefaultValue2Test());
            list.add(new DefaultValue2Test());

            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().save(list);

            Integer time = list.get(0).getValue2();
            LocalDateTime nowTime = list.get(0).getCreateTime();

            int i = 0;
            for (DefaultValue2Test ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                i++;
                if (i > 1) {
                    assertNotSame(ss.getCreateTime(), nowTime);
                }
                assertNotNull(ss.getValue2());

                ss.setValue2(null);
                ss.setValue4(null);
            }

            mapper.getBasicMapper().update(list);
            time = list.get(0).getValue2();
            nowTime = list.get(0).getCreateTime();
            i = 0;
            for (DefaultValue2Test ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                i++;
                if (i > 1) {
                    assertNotSame(ss.getCreateTime(), nowTime);
                }
                assertNotNull(ss.getValue2());
            }
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {


            List<DefaultValue2Test> list = new ArrayList<>();
            list.add(new DefaultValue2Test());
            list.add(new DefaultValue2Test());

            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().saveBatch(list);

            Integer time = list.get(0).getValue2();
            LocalDateTime nowTime = list.get(0).getCreateTime();

            for (DefaultValue2Test ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                assertEquals(ss.getCreateTime(), nowTime);
                assertNotNull(ss.getValue2());

                ss.setValue2(null);
                ss.setValue4(null);
            }
        }

    }


    @Test
    public void deaultValueCheckSameTest2() {
        XbatisGlobalConfig.setDynamicValue("NOW2", (clazz, type) -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Integer((int) (System.currentTimeMillis() / 1000));
        });
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValue2ModelTest defaultValue2Test = new DefaultValue2ModelTest();
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().save(defaultValue2Test);
            assertEquals(defaultValue2Test.getValue2(), defaultValue2Test.getValue4());
            assertNotNull(defaultValue2Test.getValue2());

            defaultValue2Test.setValue2(null);
            defaultValue2Test.setValue4(null);
            mapper.getBasicMapper().update(defaultValue2Test);
            assertEquals(defaultValue2Test.getValue2(), defaultValue2Test.getValue4());
            assertNotNull(defaultValue2Test.getValue2());

        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {


            List<DefaultValue2ModelTest> list = new ArrayList<>();
            list.add(new DefaultValue2ModelTest());
            list.add(new DefaultValue2ModelTest());

            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().saveModel(list);

            Integer time = list.get(0).getValue2();
            LocalDateTime nowTime = list.get(0).getCreateTime();

            int i = 0;
            for (DefaultValue2ModelTest ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                i++;
                if (i > 1) {
                    assertNotSame(ss.getCreateTime(), nowTime);
                }

                assertNotNull(ss.getValue2());

                ss.setValue2(null);
                ss.setValue4(null);
            }

            mapper.getBasicMapper().updateModel(list);
            time = list.get(0).getValue2();
            nowTime = list.get(0).getCreateTime();
            i = 0;
            for (DefaultValue2ModelTest ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                i++;
                if (i > 1) {
                    assertNotSame(ss.getCreateTime(), nowTime);
                }

                assertNotNull(ss.getValue2());
            }
        }


        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {


            List<DefaultValue2ModelTest> list = new ArrayList<>();
            list.add(new DefaultValue2ModelTest());
            list.add(new DefaultValue2ModelTest());

            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            mapper.getBasicMapper().saveModelBatch(list);

            Integer time = list.get(0).getValue2();
            LocalDateTime nowTime = list.get(0).getCreateTime();
            for (DefaultValue2ModelTest ss : list) {
                assertEquals(ss.getValue2(), ss.getValue4());
                assertEquals(ss.getValue2(), time);
                assertNotNull(ss.getValue2());
                assertEquals(ss.getCreateTime(), nowTime);
                ss.setValue2(null);
                ss.setValue4(null);
            }
        }

    }
}
