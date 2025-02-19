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
import com.xbatis.core.test.vo.SysUserVo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryAsTest extends BaseTest {

    @Test
    public void entityAsTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.as(SysUser::getId))
                    .select(SysUser::getUserName, c -> c.as(SysUser::getUserName))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2)
                    .get();
            System.out.println(sysUser);

            SysUser eqSysUser = new SysUser();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");

            assertEquals(eqSysUser, sysUser);
        }
    }


    @Test
    public void voAsTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUserVo sysUser = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.as(SysUserVo::getId))
                    .select(SysUser::getUserName, c -> c.as(SysUserVo::getUserName))
                    .select(SysUser::getUserName, c -> c.as(SysUserVo::getKkName))
                    .select(SysUser::getUserName, c -> c.as(SysUserVo::getKkName2))
                    .select(SysRole::getId, c -> c.as(SysRole::getId))
                    .select(SysRole::getName, c -> c.as(SysRole::getName))
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .eq(SysUser::getId, 2)
                    .returnType(SysUserVo.class)
                    .get();
            System.out.println(sysUser);

            SysUserVo eqSysUser = new SysUserVo();
            eqSysUser.setId(2);
            eqSysUser.setUserName("test1");
            eqSysUser.setKkName("test1");
            eqSysUser.setKkName2("test1");

            SysRole eqSysRole = new SysRole();
            eqSysRole.setId(1);
            eqSysRole.setName("测试");

            SysRole sysRole = sysUser.getRole();

            sysUser.setRole(null);

            assertEquals(eqSysUser, sysUser);
            assertEquals(eqSysRole, sysRole);
        }
    }

}
