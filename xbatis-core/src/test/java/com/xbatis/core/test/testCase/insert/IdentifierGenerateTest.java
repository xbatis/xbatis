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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.IdTest;
import com.xbatis.core.test.DO.IdTest2;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.DO.UUIDTest;
import com.xbatis.core.test.mapper.IdTest2Mapper;
import com.xbatis.core.test.mapper.IdTestMapper;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.mapper.UUIDMapper;
import com.xbatis.core.test.model.IdTestModel;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierGenerateTest extends BaseTest {

    @Test
    public void insertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTest idTest = new IdTest();
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            assertNotNull(idTest.getId());
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTest idTest = new IdTest();
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.getBasicMapper().save(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getId());

            idTestMapper.saveOrUpdate(idTest);
            idTestMapper.saveOrUpdate(idTest);
        }
    }

    @Test
    public void insertModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTestModel idTest = new IdTestModel();
            idTest.setCreateTime2(LocalDateTime.now());
            idTestMapper.save(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getXxid());
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTestModel idTest = new IdTestModel();
            idTest.setCreateTime2(LocalDateTime.now());
            idTestMapper.getBasicMapper().save(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getXxid());
        }
    }

    @Test
    public void insertOrUpdateModelTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTestModel idTest = new IdTestModel();
            idTest.setXxid(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 1L);
            idTest.setCreateTime2(LocalDateTime.now());
            idTestMapper.saveOrUpdate(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getXxid());

            idTestMapper.saveOrUpdate(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getXxid());
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTestModel idTest = new IdTestModel();
            idTest.setCreateTime2(LocalDateTime.now());
            idTestMapper.getBasicMapper().save(idTest);
            //System.out.println(idTest);
            assertNotNull(idTest.getXxid());
        }
    }

    @Test
    public void insertWithIdTest() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTest idTest = new IdTest();
            idTest.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 1L);
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            System.out.println(idTest);
            assertEquals(1L, (long) idTest.getId());

            assertNotNull(idTestMapper.getById(idTest.getId()));
        }
    }

    @Test
    public void batchInsertWithSelectIdTest() {
        if (TestDataSource.DB_TYPE != DbType.ORACLE && TestDataSource.DB_TYPE != DbType.PGSQL && TestDataSource.DB_TYPE == DbType.GAUSS) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTestMapper idTestMapper = session.getMapper(IdTestMapper.class);
            IdTest idTest = new IdTest();
            idTest.setCreateTime(LocalDateTime.now());

            IdTest idTest2 = new IdTest();
            idTest2.setCreateTime(LocalDateTime.now());

            List<IdTest> idTestList = Arrays.asList(idTest, idTest2);
            idTestMapper.save(idTestList);
            System.out.println(idTestList);

            assertEquals(1L, (long) idTest.getId());
            assertEquals(2L, (long) idTest2.getId());
            assertNotNull(idTestMapper.getById(idTest.getId()));
        }
    }

    @Test
    public void insertWithId2Test() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            IdTest2Mapper idTestMapper = session.getMapper(IdTest2Mapper.class);
            IdTest2 idTest = new IdTest2();
            idTest.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 1L);
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            System.out.println(idTest);
            assertEquals(1L, (long) idTest.getId());


            assertNotNull(QueryChain.of(idTestMapper).eq(IdTest2::getId, 1).get());
        }
    }

    @Test
    public void insertUUIDTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            UUIDMapper idTestMapper = session.getMapper(UUIDMapper.class);
            UUIDTest idTest = new UUIDTest();
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            assertNotNull(idTest.getId());
        }
    }

    @Test
    public void insertUUID2Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            UUIDMapper idTestMapper = session.getMapper(UUIDMapper.class);
            UUIDTest idTest = new UUIDTest();
            idTest.setId(" ");
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            assertNotEquals(idTest.getId(), "");
            assertNotEquals(idTest.getId(), " ");
        }
    }

    @Test
    public void insertUUID3Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            UUIDMapper idTestMapper = session.getMapper(UUIDMapper.class);
            UUIDTest idTest = new UUIDTest();
            idTest.setId("a");
            idTest.setCreateTime(LocalDateTime.now());
            idTestMapper.save(idTest);
            assertEquals(idTest.getId(), "a");
        }
    }

    @Test
    public void insertEmpty() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser old = QueryChain.of(sysUserMapper).orderByDesc(SysUser::getId).limit(1).get();
            Integer maxId = old.getId();

            if (TestDataSource.DB_TYPE != DbType.SQL_SERVER
                    && TestDataSource.DB_TYPE != DbType.DB2) {
                old.setId(maxId + 1);
            } else {
                old.setId(null);
            }

            old.setUserName("");
            sysUserMapper.save(old);

            SysUser sysUser = sysUserMapper.getById(maxId + 1);
            if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                assertEquals(null, sysUser.getUserName());
            } else {
                assertEquals("", sysUser.getUserName());
            }

        }
    }

}
