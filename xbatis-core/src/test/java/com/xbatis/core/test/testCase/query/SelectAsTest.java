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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.vo.NestedSysRoleVo;
import com.xbatis.core.test.vo.SysUserRoleAutoSelectVo;
import com.xbatis.core.test.vo.SysUserRoleVo;
import com.xbatis.core.test.vo.SysUserVo;
import db.sql.api.cmd.GetterFields;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectAsTest extends BaseTest {

    @Test
    public void simpleReturnAs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserVo sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(SysUserVo.class)
                    .get();
            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            assertEquals(eqSysUser, sysUser, "@ResultEntity注解测试");
        }
    }


    @Test
    public void simpleReturnResultEntityFieldAs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserVo sysUser = QueryChain.create()
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getPassword)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(SysUserVo.class)
                    .withMapper(sysUserMapper)
                    .get();
            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setPwd("123");
            assertEquals(eqSysUser, sysUser, "@ResultEntityField注解测试");
        }
    }

    @Test
    public void simpleReturnResultFieldAs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserVo sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getPassword)
                    .select(SysUser::getId, c -> c.concat("kk").as("kk"))
                    .select(SysUser::getId, c -> c.concat("kk").as(SysUserVo::getKkName2))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(SysUserVo.class)
                    .get();
            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setPwd("123");
            eqSysUser.setKkName("1kk");
            eqSysUser.setKkName2("1kk");
            assertEquals(eqSysUser, sysUser, "@ResultEntity 之 @ResultField注解 ，返回Vo测试");
        }
    }

    @Test
    public void simpleReturnResultFieldAs2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserVo sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName, SysUser::getPassword)
                    .select(GetterFields.of(SysUser::getId), cs -> cs[0].concat("kk").as("kk"))
                    .select(GetterFields.of(SysUser::getId), cs -> cs[0].concat("kk").as(SysUserVo::getKkName2))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(SysUserVo.class)
                    .get();
            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(1);
            eqSysUser.setUserName("admin");
            eqSysUser.setPwd("123");
            eqSysUser.setKkName("1kk");
            eqSysUser.setKkName2("1kk");
            assertEquals(eqSysUser, sysUser, "@ResultEntity 之 @ResultField注解 ，返回Vo测试");
        }
    }

    @Test
    public void nestedResultEntity() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            QueryChain<SysUserVo> queryChain = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName)
                    .select(SysRole.class)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserVo.class);

            SysUserVo sysUser = queryChain
                    .get();

            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            SysRole sysRole = new SysRole();
            sysRole.setId(1);
            sysRole.setName("测试");
            sysRole.setCreateTime(LocalDateTime.parse("2022-10-10T00:00"));
            eqSysUser.setRole(sysRole);

            assertEquals(eqSysUser, sysUser, "@NestedResultEntity注解，返回实体类测试");

            assertEquals(eqSysUser, queryChain.list().get(0), "@NestedResultEntity注解，返回实体类测试");
        }
    }


    @Test
    public void nestedResultEntity21() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            QueryChain<SysUserVo> queryChain = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName)
                    .select(SysRole.class)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserVo.class);

            SysUserVo sysUser = queryChain
                    .get();

            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            SysRole sysRole = new SysRole();
            sysRole.setId(1);
            sysRole.setName("测试");
            sysRole.setCreateTime(LocalDateTime.parse("2022-10-10T00:00"));
            eqSysUser.setRole(sysRole);

            assertEquals(eqSysUser, sysUser, "@NestedResultEntity注解，返回实体类测试");

            assertEquals(eqSysUser, queryChain.list().get(0), "@NestedResultEntity注解，返回实体类测试");
        }
    }

    @Test
    public void nestedResultEntity2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryChain<SysUserRoleVo> queryChain = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName)
                    .select(SysRole.class)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserRoleVo.class);
            SysUserRoleVo sysUser = queryChain.get();
            SysUserRoleVo eqSysUser = new SysUserRoleVo();
            eqSysUser.setId(2);
            eqSysUser.setName("测试");
            eqSysUser.setUserName("test1");

            NestedSysRoleVo sysRole = new NestedSysRoleVo();
            sysRole.setId(1);
            sysRole.setXxName("测试");
            eqSysUser.setRole(sysRole);
            assertEquals(eqSysUser, sysUser, "@NestedResultEntity注解，返回Vo测试");
            assertEquals(eqSysUser, queryChain.list().get(0), "@NestedResultEntity注解，返回Vo测试");
        }
    }

    @Test
    public void nestedResultEntity3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserRoleVo sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, SysUser::getUserName)
                    .select(SysUser::getUserName, c -> c.concat("aa").as("cc"))
                    .select(SysRole.class)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserRoleVo.class)
                    .get();
            SysUserRoleVo eqSysUser = new SysUserRoleVo();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setName("测试");


            NestedSysRoleVo sysRole = new NestedSysRoleVo();
            sysRole.setId(1);
            sysRole.setXxName("测试");
            sysRole.setCc("test1aa");
            eqSysUser.setRole(sysRole);
            assertEquals(eqSysUser, sysUser, "@NestedResultEntity 之 @ResultField注解 ，返回Vo测试");
        }
    }


    @Test
    public void nestedResultEntityWithSelectVo() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryChain<SysUserRoleAutoSelectVo> queryChain = QueryChain.of(sysUserMapper)
                    //.select(SysUserRoleAutoSelectVo.class)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserRoleAutoSelectVo.class);
            SysUserRoleAutoSelectVo sysUser = queryChain.get();
            SysUserRoleAutoSelectVo eqSysUser = new SysUserRoleAutoSelectVo();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");

            SysRole sysRole = new SysRole();
            sysRole.setId(1);
            sysRole.setName("测试");
            sysRole.setCreateTime(LocalDateTime.parse("2022-10-10T00:00"));
            eqSysUser.setSysRole(sysRole);

            assertEquals(eqSysUser, sysUser, "@NestedResultEntity注解，返回Vo测试");
            assertEquals(eqSysUser, queryChain.list().get(0), "@NestedResultEntity注解，返回Vo测试");
        }
    }
}
