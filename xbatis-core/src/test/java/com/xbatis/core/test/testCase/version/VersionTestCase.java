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

package com.xbatis.core.test.testCase.version;

import com.xbatis.core.test.DO.VersionTest;
import com.xbatis.core.test.mapper.VersionTestMapper;
import com.xbatis.core.test.model.VersionModel;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VersionTestCase extends BaseTest {

    @Test
    public void insertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTestMapper versionTestMapper = session.getMapper(VersionTestMapper.class);
            VersionTest versionTest = new VersionTest();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            versionTestMapper.save(versionTest);
            System.out.println(versionTest);
            assertNotNull(versionTest.getId());
            assertEquals(1, (int) versionTestMapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTestMapper versionTestMapper = session.getMapper(VersionTestMapper.class);
            VersionTest versionTest = new VersionTest();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            versionTestMapper.save(versionTest);

            versionTest.setName("我是2");
            versionTestMapper.update(versionTest);
            versionTest = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(2, (int) versionTest.getVersion());
            assertEquals(2, (int) versionTestMapper.getById(versionTest.getId()).getVersion());

            versionTest.setName("我是3");
            versionTestMapper.update(versionTest);
            versionTest = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(3, (int) versionTest.getVersion());
            assertEquals(3, (int) versionTestMapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateForceTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTestMapper versionTestMapper = session.getMapper(VersionTestMapper.class);
            VersionTest versionTest = new VersionTest();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            versionTestMapper.save(versionTest);

            versionTest.setName("我是2");
            versionTestMapper.update(versionTest);
            versionTest = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(2, (int) versionTest.getVersion());
            assertEquals(2, (int) versionTestMapper.getById(versionTest.getId()).getVersion());

            versionTest.setName("我是3");
            versionTest.setVersion(null);
            versionTestMapper.update(versionTest, true);
            versionTest = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(3, (int) versionTest.getVersion());
            assertEquals(3, (int) versionTestMapper.getById(versionTest.getId()).getVersion());


            versionTest.setName("我是4");
            versionTestMapper.updateBatch(Arrays.asList(versionTest));
            versionTest = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(4, (int) versionTest.getVersion());
            assertEquals(4, (int) versionTestMapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void insertWithModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTestMapper versionTestMapper = session.getMapper(VersionTestMapper.class);
            VersionModel versionTest = new VersionModel();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            versionTestMapper.save(versionTest);
            System.out.println(versionTest);
            assertNotNull(versionTest.getId());
            assertEquals(1, (int) versionTestMapper.getById(versionTest.getId()).getVersion());
        }
    }

    @Test
    public void updateWithModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            VersionTestMapper versionTestMapper = session.getMapper(VersionTestMapper.class);
            VersionModel versionTest = new VersionModel();
            versionTest.setName("我是1");
            versionTest.setCreateTime(LocalDateTime.now());
            versionTestMapper.save(versionTest);
            System.out.println(versionTest);

            versionTest.setName("我是2");
            versionTestMapper.update(versionTest);
            VersionTest versionTest2 = versionTestMapper.getById(versionTest.getId());

            System.out.println(versionTest);
            assertEquals(2, (int) versionTest2.getVersion());
            assertEquals(2, (int) versionTestMapper.getById(versionTest2.getId()).getVersion());
        }
    }
}
