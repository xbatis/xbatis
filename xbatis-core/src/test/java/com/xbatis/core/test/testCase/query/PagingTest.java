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

import cn.xbatis.core.mybatis.mapper.context.Pager;
import cn.xbatis.core.sql.executor.SubQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.util.WhereUtil;
import com.xbatis.core.test.DO.MultiPk;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.MultiPkMapper;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagingTest extends BaseTest {

    @Test
    public void pagingTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = sysUserMapper.paging(Pager.of(2), where -> {
                where.in(SysUser::getId, 1, 2, 3);
            });

            assertEquals(pager.getTotal(), 3);
            assertEquals(pager.getTotalPage(), 2);

            assertEquals(pager.getResults().size(), 2);
            assertEquals(pager.getResults().get(0).getId(), 1);
            assertEquals(pager.getResults().get(1).getId(), 2);
        }
    }

    @Test
    public void pagingTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Pager<SysUser> pager = sysUserMapper.paging(Pager.of(2), WhereUtil.create().in(SysUser::getId, 1, 2, 3));

            assertEquals(pager.getTotal(), 3);
            assertEquals(pager.getTotalPage(), 2);

            assertEquals(pager.getResults().size(), 2);
            assertEquals(pager.getResults().get(0).getId(), 1);
            assertEquals(pager.getResults().get(1).getId(), 2);
        }
    }

    @Test
    public void pagingTest3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper multiPkMapper = session.getMapper(MultiPkMapper.class);
            MultiPk multiPk = new MultiPk();
            multiPk.setId1(1);
            multiPk.setId2(2);
            multiPk.setName("xxx");
            multiPkMapper.save(multiPk);
            Pager<MultiPk> pager = multiPkMapper.paging(Pager.of(2), where -> {

            });
            System.out.println(pager.getResults());

            assertEquals(pager.getTotal(), 1);
            assertEquals(pager.getTotalPage(), 1);

            assertEquals(pager.getResults().size(), 1);
            assertEquals(pager.getResults().get(0).getId1(), 1);
            assertEquals(pager.getResults().get(0).getId2(), 2);
        }
    }


    @Test
    public void pagingSubQueryTest() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SubQuery subQuery = SubQuery.create();

            subQuery.select(SysUser.class)
                    .from(SysUser.class)
                    .in(SysUser::getId, 1, 2, 3, 4)
                    .orderByDesc(SysUser::getId)
                    .limit(10);


            Pager<SysUser> pager = QueryChain.of(sysUserMapper)
                    .optimizeOptions(optimizeOptions -> optimizeOptions.disableAll())
                    .selectAll()
                    .from(subQuery)
                    .orderByDesc(subQuery.$outerField(SysUser::getId))
                    .paging(Pager.of(2, 2));

            System.out.println(pager);

            assertEquals(pager.getTotal(), 3);
            assertEquals(pager.getTotalPage(), 2);

            assertEquals(pager.getResults().size(), 1);
            assertEquals(pager.getResults().get(0).getId(), 1);


            pager = QueryChain.of(sysUserMapper)
                    //.optimizeOptions(optimizeOptions -> optimizeOptions.disableAll())
                    .selectAll()
                    .from(subQuery)
                    .orderByDesc(subQuery.$outerField(SysUser::getId))
                    .paging(Pager.of(1, 2));

            System.out.println(pager);

            assertEquals(pager.getTotal(), 3);
            assertEquals(pager.getTotalPage(), 2);

            assertEquals(pager.getResults().size(), 2);
            assertEquals(pager.getResults().get(0).getId(), 3);
            assertEquals(pager.getResults().get(1).getId(), 2);
        }
    }
}
