/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.MultiPk;
import com.xbatis.core.test.mapper.MultiPkMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static db.sql.api.impl.cmd.Methods.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiRowInTest extends BaseTest {

    @Test
    public void multiRowInTest() {
        if (TestDataSource.DB_TYPE == DbType.H2 || TestDataSource.DB_TYPE == DbType.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);
            List<MultiPk> list = new ArrayList<>();

            MultiPk multiPk = new MultiPk();
            multiPk.setId1(1);
            multiPk.setId2(1);
            multiPk.setName("x1");
            list.add(multiPk);

            multiPk = new MultiPk();
            multiPk.setId1(2);
            multiPk.setId2(2);
            multiPk.setName("x2");
            list.add(multiPk);

            mapper.save(list);

            List<MultiPk> list2 = QueryChain.of(mapper).select("*")
                    //多列in操作，针对list里是实体类的方式
                    .in(list, MultiPk::getId1, MultiPk::getId2)
                    //多列in操作，针对list里非实体类的方式
                    .and(getters(MultiPk::getId1, MultiPk::getId2), cs -> row(cs).in(rowValues(list, MultiPk::getId1, MultiPk::getId2)))
                    .list();

            assertEquals(list, list2);
        }
    }

    @Test
    public void multiRowNotInTest() {
        if (TestDataSource.DB_TYPE == DbType.H2 || TestDataSource.DB_TYPE == DbType.PGSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);
            List<MultiPk> list = new ArrayList<>();

            MultiPk multiPk = new MultiPk();
            multiPk.setId1(1);
            multiPk.setId2(1);
            multiPk.setName("x1");
            list.add(multiPk);

            multiPk = new MultiPk();
            multiPk.setId1(2);
            multiPk.setId2(2);
            multiPk.setName("x2");
            list.add(multiPk);

            mapper.save(list);

            final List<MultiPk> notInList = Collections.singletonList(multiPk);

            List<MultiPk> list2 = QueryChain.of(mapper).select("*")
                    //多列in操作，针对list里是实体类的方式
                    .in(list, MultiPk::getId1, MultiPk::getId2)
                    //多列in操作，针对list里非实体类的方式
                    .and(getters(MultiPk::getId1, MultiPk::getId2), cs -> row(cs).in(rowValues(list, MultiPk::getId1, MultiPk::getId2)))

                    .notIn(notInList, MultiPk::getId1, MultiPk::getId2)
                    .and(getters(MultiPk::getId1, MultiPk::getId2), cs -> row(cs).notIn(rowValues(notInList, MultiPk::getId1, MultiPk::getId2)))

                    .list();

            list.remove(multiPk);
            assertEquals(list, list2);
        }
    }
}
