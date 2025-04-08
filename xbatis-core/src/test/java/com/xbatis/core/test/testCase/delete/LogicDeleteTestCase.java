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

package com.xbatis.core.test.testCase.delete;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import com.xbatis.core.test.DO.LogicDeleteTest;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.LogicDeleteTestMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import db.sql.api.cmd.JoinMode;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogicDeleteTestCase extends BaseTest {

    public LogicDeleteTestCase() {
        XbatisGlobalConfig.setLogicDeleteSwitch(true);
    }

    @Test
    public void deleteIdTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.deleteById(1);
            List<LogicDeleteTest> list = QueryChain.of(logicDeleteTestMapper).list();
            assertEquals(list.size(), 2);

            QueryChain.of(logicDeleteTestMapper).join(LogicDeleteTest.class, 1, LogicDeleteTest.class, 2, on -> on.eq(LogicDeleteTest::getId, 1, LogicDeleteTest::getId, 2)).list();
        }
    }


    @Test
    public void deleteIdTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.deleteById(1);

            LogicDeleteTest logicDeleteTest = LogicDeleteUtil.execute(false, () -> logicDeleteTestMapper.getById(1));
            assertEquals(logicDeleteTest.getDeleted(), Byte.valueOf("1"));
        }
    }

    @Test
    public void deleteEntityTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.delete(logicDeleteTestMapper.getById(1));
            List<LogicDeleteTest> list = QueryChain.of(logicDeleteTestMapper).list();
            assertEquals(list.size(), 2);
        }
    }

    @Test
    public void deleteWithIdTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            int deleteCnt = logicDeleteTestMapper.update(logicDeleteTestMapper.getById(1));
            assertEquals(deleteCnt, 1);
        }
    }

    @Test
    public void deleteWithWhereTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            int deleteCnt = logicDeleteTestMapper.delete(where -> where.eq(LogicDeleteTest::getId, 1));
            assertEquals(deleteCnt, 1);

            List<LogicDeleteTest> list = QueryChain.of(logicDeleteTestMapper).list();
            assertEquals(list.size(), 2);
        }
    }

    @Test
    public void updateWithWhereTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            int updateCnt = logicDeleteTestMapper.update(logicDeleteTestMapper.getById(1), where -> where.eq(LogicDeleteTest::getId, 1).or().eq(LogicDeleteTest::getName, "abc"));
            assertEquals(updateCnt, 1);
        }
    }

    @Test
    public void updateWithWhereTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            int updateCnt = logicDeleteTestMapper.update(logicDeleteTestMapper.getById(1));
            assertEquals(updateCnt, 1);
            LogicDeleteTest logicDeleteTest = logicDeleteTestMapper.getById(1);
            logicDeleteTestMapper.delete(logicDeleteTest);
            updateCnt = logicDeleteTestMapper.update(logicDeleteTest);
            assertEquals(updateCnt, 0);
        }
    }

    @Test
    public void updateWithWhereTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.deleteById(1);
            assertNull(logicDeleteTestMapper.getById(1));
            int updateCnt = UpdateChain.of(logicDeleteTestMapper)
                    .set(LogicDeleteTest::getName, "abc")
                    .eq(LogicDeleteTest::getId, 1)
                    .or()
                    .eq(LogicDeleteTest::getName, "abc")
                    .execute();
            assertEquals(updateCnt, 0);
        }
    }

    @Test
    public void updateWithWhereTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);

            int cnt = logicDeleteTestMapper.update(logicDeleteTestMapper.getById(1), where -> where.ne(LogicDeleteTest::getId, 0));
            assertEquals(cnt, 1);

            logicDeleteTestMapper.delete(where -> where.eq(LogicDeleteTest::getId, 1));

            assertNull(logicDeleteTestMapper.getById(1));
            int updateCnt = UpdateChain.of(logicDeleteTestMapper)
                    .set(LogicDeleteTest::getName, "abc")
                    .eq(LogicDeleteTest::getId, 1)
                    .or()
                    .eq(LogicDeleteTest::getName, "abc")
                    .execute();
            assertEquals(updateCnt, 0);
        }
    }


    @Test
    public void leftJoinTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.deleteById(1);
            int count = QueryChain.of(logicDeleteTestMapper)
                    .from(SysUser.class)
                    .join(JoinMode.LEFT, SysUser.class, LogicDeleteTest.class, on -> on.eq(LogicDeleteTest::getId, SysUser::getId))
                    .eq(SysUser::getId, 1)
                    .count();
            assertEquals(count, 1);
        }
    }

    @Test
    public void rightJoinTest4() {
        if (TestDataSource.DB_TYPE == DbType.SQLITE) {
            //SQLITE 不支持 right join
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            LogicDeleteTestMapper logicDeleteTestMapper = session.getMapper(LogicDeleteTestMapper.class);
            logicDeleteTestMapper.deleteById(1);
            int count = QueryChain.of(logicDeleteTestMapper)
                    .from(SysUser.class)
                    .join(JoinMode.RIGHT, SysUser.class, LogicDeleteTest.class, on -> on.eq(LogicDeleteTest::getId, SysUser::getId).eq(LogicDeleteTest::getId, 1))
                    .count();
            assertEquals(count, 3);
        }
    }

}
