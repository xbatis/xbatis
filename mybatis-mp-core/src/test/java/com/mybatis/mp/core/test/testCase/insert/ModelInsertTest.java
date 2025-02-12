/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package com.mybatis.mp.core.test.testCase.insert;

import com.mybatis.mp.core.test.DO.DefaultValueTest;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.DefaultValueTestMapper;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.model.DefaultValueTestModel;
import com.mybatis.mp.core.test.model.SysUserModel;
import com.mybatis.mp.core.test.testCase.BaseTest;
import com.mybatis.mp.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelInsertTest extends BaseTest {

    @Test
    public void forceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.save(sysUserModel, true);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void forceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.save(sysUserModel, false);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
        }
    }

    @Test
    public void batchForceInsertTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveModel(Collections.singletonList(sysUserModel), true);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), null);
        }
    }

    @Test
    public void batchForceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveModel(Collections.singletonList(sysUserModel), false);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
        }
    }


    @Test
    public void batchInsert() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            DefaultValueTestMapper mapper = session.getMapper(DefaultValueTestMapper.class);
            {
                DefaultValueTestModel defaultValueTest1 = new DefaultValueTestModel();
                DefaultValueTestModel defaultValueTest2 = new DefaultValueTestModel();
                DefaultValueTestModel defaultValueTest3 = new DefaultValueTestModel();

                List<DefaultValueTestModel> list = Arrays.asList(defaultValueTest1, defaultValueTest2, defaultValueTest3);

                if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
                    defaultValueTest1.setId(11);
                    defaultValueTest2.setId(12);
                    defaultValueTest3.setId(13);
                    mapper.saveModelBatch(list, DefaultValueTest::getId, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                } else {
                    mapper.saveModelBatch(list, DefaultValueTest::getValue1, DefaultValueTest::getValue2, DefaultValueTest::getCreateTime);
                    System.out.println(list);
                    if (TestDataSource.DB_TYPE == DbType.SQLITE || TestDataSource.DB_TYPE == DbType.MARIA_DB) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        assertEquals(list.get(i).getId(), i + 1);
                    }
                }
            }


            DefaultValueTest defaultValueTest;
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

            DefaultValueTest defaultValueTest2;
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
}
