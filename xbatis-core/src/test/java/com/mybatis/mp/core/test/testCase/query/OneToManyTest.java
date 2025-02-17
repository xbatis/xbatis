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

import cn.xbatis.core.db.reflect.ResultInfos;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysRoleMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.vo.OneToManyTypeHandlerVo;
import com.xbatis.core.test.vo.OneToManyVo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OneToManyTest extends BaseTest {


    @Test
    public void oneToManyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<OneToManyVo> list = QueryChain.of(sysRoleMapper)
                    .select(SysUser.class)
                    .select(SysUser::getUserName, c -> c.as(OneToManyVo::getAsName))
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .join(SysRole.class, SysUser.class, on -> on.eq(SysUser::getRole_id, SysRole::getId))
                    .orderBy(SysUser::getId)
                    .returnType(OneToManyVo.class)
                    .list();


            System.out.println(ResultInfos.get(OneToManyVo.class).getTablePrefixes());

            System.out.println(list);
            assertEquals(OneToManyVo.class, list.get(0).getClass(), "oneToManyTest");
            assertEquals(Integer.valueOf(1), list.size(), "oneToManyTest");
            assertEquals(Integer.valueOf(2), list.get(0).getSysUserList().size(), "oneToManyTest");
            assertEquals(SysUser.class, list.get(0).getSysUserList().get(0).getClass(), "oneToManyTest");

            assertEquals(list.get(0).getAsName(), "test1");
        }
    }


    @Test
    public void oneToManyTypeHandlerTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            List<OneToManyTypeHandlerVo> list = QueryChain.of(sysRoleMapper)
                    .select(SysUser.class)
                    .select(SysUser::getUserName, c -> c.as("kk"))
                    .select(SysUser::getUserName, c -> c.as("kk2"))
                    .select(SysRole.class)
                    .from(SysRole.class)
                    .join(SysRole.class, SysUser.class, on -> on.eq(SysUser::getRole_id, SysRole::getId))
                    .orderBy(SysUser::getId)
                    .returnType(OneToManyTypeHandlerVo.class)
                    .list();

            System.out.println(list);
            assertEquals("xxxx123", list.get(0).getName());
            assertEquals("xxxx123", list.get(0).getKkName());
            assertEquals("xxxx123", list.get(0).getSysUserList().get(0).getUserName());
            assertEquals("xxxx123", list.get(0).getSysUserList().get(0).getPwd());
            assertEquals("null-phone", list.get(0).getSysUserList().get(1).getPwd());
            assertEquals("xxxx123", list.get(0).getSysUserList().get(0).getKkName());
        }
    }
}
