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
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import com.xbatis.core.test.vo.GroupConcatVo;
import db.sql.api.DbType;
import db.sql.api.cmd.GetterFields;
import db.sql.api.impl.cmd.Methods;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbFunTest extends BaseTest {

    @Test
    public void whereAndGetterTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            sysUserMapper.getById(1);
            Integer id = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .and(SysUser::getId, c -> c.concat("x1").eq("2x1"))
                    .returnType(Integer.class)
                    .get();

            assertEquals(id, 2);

            SysUser sysUser = sysUserMapper.get(where -> where.and(SysUser::getId, c -> c.concat("x1").eq("2x1")));
            assertEquals(sysUser.getId(), 2);
        }
    }


    @Test
    public void whereAndGetterTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer id = QueryChain.of(sysUserMapper)

                    .from(SysUser.class)
                    .and(GetterFields.of(SysUser::getId, SysUser::getUserName), c -> c[0].eq(1))
                    .orderBy(GetterFields.of(SysUser::getId), c -> c[0])
                    .groupBy(GetterFields.of(SysUser::getId), c -> c[0])
                    .havingAnd(GetterFields.of(SysUser::getId, SysUser::getUserName), c -> c[0].count().gt(0))
                    .returnType(Integer.class)
                    .count();

            assertEquals(id, 1);
        }
    }

    @Test
    public void whereAndGetterTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer id = QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .and(GetterFields.of(SysUser::getId, SysUser::getUserName), cs -> cs[0].eq(1))
                    .orderBy(GetterFields.of(SysUser::getId), cs -> cs[0])
                    .groupBy(GetterFields.of(SysUser::getId), cs -> cs[0])
                    .havingAnd(GetterFields.of(SysUser::getId, SysUser::getUserName), cs -> cs[0].count().gt(0))
                    .returnType(Integer.class)
                    .count();

            assertEquals(id, 1);
        }
    }

    @Test
    public void ifTest() {
        if (TestDataSource.DB_TYPE == DbType.DB2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer id = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> Methods.if_(c.eq(1), 2, 3))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();

            assertEquals(id, 2);
        }
    }

    @Test
    public void instrTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer index = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.instr("m"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();

            assertEquals(index, 3);
        }
    }

    @Test
    public void groupConcatTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<GroupConcatVo> resultMap = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id)
                    .select(SysUser::getId, c -> c.groupConcat().as(GroupConcatVo::getGroupIds))
                    .from(SysUser.class)
                    .groupBy(SysUser::getRole_id)
                    .returnType(GroupConcatVo.class)
                    .list();

            System.out.println(resultMap);
            assertEquals(resultMap.size(), 2);
            if ("1".equals(resultMap.get(0).getGroupIds())) {
                assertEquals(resultMap.get(0).getGroupIds(), "1");
                assertTrue(resultMap.get(1).getGroupIds().equals("2,3") || resultMap.get(1).getGroupIds().equals("3,2"));
            } else {
                assertEquals(resultMap.get(1).getGroupIds(), "1");
                assertTrue(resultMap.get(0).getGroupIds().equals("2,3") || resultMap.get(0).getGroupIds().equals("3,2"));
            }

        }
    }
}
