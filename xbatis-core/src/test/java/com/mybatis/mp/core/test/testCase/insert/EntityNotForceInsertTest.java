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

import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityNotForceInsertTest extends BaseTest {

    @Test
    public void notForceInsertTest1() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.save(sysUserModel, false);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }

    @Test
    public void notForceInsertTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.save(sysUserModel);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }

    @Test
    public void notForceInsertTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.save(sysUserModel, SysUser::getRole_id);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }

    @Test
    public void notForceInsertTest4() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel, false);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }

    @Test
    public void notForceInsertTest5() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }

    @Test
    public void notForceInsertTest6() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUserModel = new SysUser();
            sysUserModel.setId(TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2 ? null : 100);
            sysUserModel.setPassword("!23");
            sysUserModel.setCreate_time(LocalDateTime.now());
            sysUserModel.setRole_id(1);
            sysUserModel.setUserName(null);
            sysUserMapper.saveOrUpdate(sysUserModel, SysUser::getId);
            SysUser sysUser = sysUserMapper.getById(sysUserModel.getId());
            assertEquals(sysUser.getUserName(), "123456");
            assertEquals(sysUser.getRole_id(), 1);
        }
    }
}
