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

package com.xbatis.core.test.testCase.version;

import com.xbatis.core.test.DO.VersionTest2;
import com.xbatis.core.test.mapper.VersionTest2Mapper;
import com.xbatis.core.test.model.Version2Model;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class VersionTest2Case extends BaseTest {

    private void eqDate(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) {
            assertEquals(a, b);
        }
        Duration d = Duration.between(b, a); // 取绝对值，避免 a 早于 b 时为负
        System.out.println(a + "  -  " + b + "  ===>  " + d.toMillis());
        assertTrue(d.toMillis() <= 500 && d.toMillis() > 0);
    }

    @Test
    public void insertTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTest2Mapper VersionTest2Mapper = session.getMapper(VersionTest2Mapper.class);
            VersionTest2 versionTest = new VersionTest2();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            VersionTest2Mapper.save(versionTest);
            System.out.println(versionTest);
            assertNotNull(versionTest.getId());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTest2Mapper VersionTest2Mapper = session.getMapper(VersionTest2Mapper.class);
            VersionTest2 versionTest = new VersionTest2();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            VersionTest2Mapper.save(versionTest);

            versionTest.setName("我是2");
            VersionTest2Mapper.update(versionTest);
            versionTest = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());

            versionTest.setName("我是3");
            VersionTest2Mapper.update(versionTest);
            versionTest = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateForceTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTest2Mapper VersionTest2Mapper = session.getMapper(VersionTest2Mapper.class);
            VersionTest2 versionTest = new VersionTest2();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            VersionTest2Mapper.save(versionTest);

            versionTest.setName("我是2");
            VersionTest2Mapper.update(versionTest);
            versionTest = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());

            versionTest.setName("我是3");
            VersionTest2Mapper.update(versionTest, true);
            versionTest = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());


            versionTest.setName("我是4");
            VersionTest2Mapper.updateBatch(Arrays.asList(versionTest));
            versionTest = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void insertWithModelTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTest2Mapper VersionTest2Mapper = session.getMapper(VersionTest2Mapper.class);
            Version2Model versionTest = new Version2Model();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            VersionTest2Mapper.save(versionTest);
            System.out.println(versionTest);
            assertNotNull(versionTest.getId());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateWithModelTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTest2Mapper VersionTest2Mapper = session.getMapper(VersionTest2Mapper.class);
            Version2Model versionTest = new Version2Model();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            VersionTest2Mapper.save(versionTest);
            System.out.println(versionTest);

            versionTest.setName("我是2");
            VersionTest2Mapper.update(versionTest);
            VersionTest2 versionTest2 = VersionTest2Mapper.getById(versionTest.getId());

            System.out.println(versionTest);
            eqDate(LocalDateTime.now(), versionTest2.getVersion());
            eqDate(LocalDateTime.now(), VersionTest2Mapper.getById(versionTest2.getId()).getVersion());
        }
    }
}