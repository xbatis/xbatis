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

package com.xbatis.core.test.testCase.cmdTemplete;

import cn.xbatis.core.sql.executor.SubQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import com.xbatis.core.test.vo.SysUserRoleAutoSelectVo;
import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.cmd.GetterFields;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.CmdTemplate;
import db.sql.api.impl.cmd.basic.ConditionTemplate;
import db.sql.api.impl.cmd.basic.FunTemplate;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CmdTemplateTestCase extends BaseTest {

    public static final java.util.function.Function<Cmd, FunTemplate> COUNT_FUN = c -> FunTemplate.create("count({0})", c);

    @Test
    public void templateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id, c -> CmdTemplate.create("count({0})+{1}", c, 1).as("cnt"))
                    .from(SysUser.class)
                    .and(GetterFields.of(SysUser::getId, SysUser::getId), cs -> ConditionTemplate.create("{0}+{1}={2}", cs[0], cs[1], 2).as("xx"))
                    .returnType(String.class)
                    .get();

            assertTrue(str.equals("2") || str.equals("2.0"));
        }
    }

    @Test
    public void templateTest2() {
        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.SQL_SERVER) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id, c -> COUNT_FUN.apply(c).as("xx").plus(1).concat(1, "2", 3).length())
                    .from(SysUser.class)
                    .and(GetterFields.of(SysUser::getId, SysUser::getId), cs -> ConditionTemplate.create("{0}+{1}={2}", cs[0], cs[1], 2).as("xx2"))
                    .returnType(String.class)
                    .get();

            assertTrue(str.equals("4") || str.equals("4.0"));
        }
    }

    @Test
    public void templateTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserRoleAutoSelectVo vo = QueryChain.of(sysUserMapper)
                    .select(SysUserRoleAutoSelectVo.class)
                    .select(SubQuery.create("aa").select(SysUser::getUserName).from(SysUser.class).eq(SysUser::getId, 1))
                    .select(SysRole::getId, c -> CmdTemplate.create(" RANK() OVER( ORDER BY {0}) ", c).as("RANK2"))
                    .select(SysRole::getId, c -> CmdTemplate.create(" RANK() OVER( ORDER BY {0}) as RANK3", c))
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .returnType(SysUserRoleAutoSelectVo.class)
                    .orderBy(SysUser::getId)
                    .limit(1)
                    .get();

            assertEquals(vo.getId(), 2);
        }
    }


    @Test
    public void templateWrappingTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .select(Methods.tpl(true, "'123'").as("xxx"))
                    .from(SysUser.class)
                    .limit(1)
                    .returnType(String.class)
                    .get();

            assertTrue(str.equals("123"));
        }
    }
}
