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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.mybatis.mapper.context.Pager;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountDistinctOptimizeTest extends BaseTest {


    @Test
    public void distinctPagingTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = QueryChain.of(sysUserMapper)

                    .selectDistinct()
                    .select(SysUser::getId)
                    .eq(SysUser::getId, 3)
                    .paging(Pager.of(1));

            assertEquals(pager.getResults().size(),1);
        }
    }

    @Test
    public void distinctCountTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count= QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getId)
                    .eq(SysUser::getId, 3)
                    .count();

            assertEquals(count,1);
        }
    }

    @Test
    public void distinctPagingTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = QueryChain.of(sysUserMapper)

                    .selectDistinct()
                    .select(SysUser::getId,SysUser::getUserName)
                    .eq(SysUser::getId, 3)
                    .paging(Pager.of(1));

            assertEquals(pager.getResults().size(),1);
        }
    }

    @Test
    public void distinctCountTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count= QueryChain.of(sysUserMapper)
                    .selectDistinct()
                    .select(SysUser::getId,SysUser::getUserName)
                    .eq(SysUser::getId, 3)
                    .count();

            assertEquals(count,1);
        }
    }
}
