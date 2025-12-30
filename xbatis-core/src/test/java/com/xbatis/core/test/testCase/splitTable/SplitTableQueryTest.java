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

import cn.xbatis.core.mybatis.mapper.context.MybatisParameter;
import com.xbatis.core.test.DO.SplitTableTest;
import com.xbatis.core.test.mapper.SplitTableTestMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SplitTableQueryTest extends BaseTest {

    @Test
    public void testSplitTableEq() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);

            SplitTableTest splitTableTest = mapper.get(where -> where.eq(SplitTableTest::getSplitId, 1));
            assertNotNull(splitTableTest);
            assertEquals(splitTableTest.getSplitId(), 1);
        }
    }

    @Test
    public void testSplitTableInArr() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);

            List<SplitTableTest> list = mapper.list(where -> where.in(SplitTableTest::getSplitId, new MybatisParameter(1, (TypeHandler<?>) null, JdbcType.UNDEFINED), 2));
            for (SplitTableTest splitTableTest : list) {
                assertNotNull(splitTableTest);
                assertEquals(splitTableTest.getSplitId(), 1);
                assertEquals(splitTableTest.getSplitId(), 1);
            }

        }
    }

    @Test
    public void testSplitTableInList() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);

            List<SplitTableTest> list = mapper.list(where -> where.in(SplitTableTest::getSplitId, Arrays.asList(1, 2)));
            for (SplitTableTest splitTableTest : list) {
                assertNotNull(splitTableTest);
                assertEquals(splitTableTest.getSplitId(), 1);
            }
        }
    }

    @Test
    public void testSplitTableBetween() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SplitTableTestMapper mapper = session.getMapper(SplitTableTestMapper.class);
            List<SplitTableTest> list = mapper.list(where -> where.between(SplitTableTest::getSplitId, 1, 2));
            for (SplitTableTest splitTableTest : list) {
                assertNotNull(splitTableTest);
                assertEquals(splitTableTest.getSplitId(), 1);
                assertEquals(splitTableTest.getSplitId(), 1);
            }
        }
    }
}
