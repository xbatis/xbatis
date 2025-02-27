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

import cn.xbatis.core.sql.executor.SubQuery;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

public class SubQueryTest extends BaseTest {

    @Test
    public void fromSubQueryTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SubQuery subQuery = SubQuery.create("xx")
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .in(false, SysUser::getId, 23, 234)
                    .eq(SysUser::getId, 2);

            QueryChain.of(sysUserMapper)
                    .select("*")
                    .from(subQuery)
                    .returnMap()
                    .list()
            ;
        }
    }

    @Test
    public void joinSubQueryTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            SubQuery subQuery = SubQuery.create("xx")
                    .select(SysUser::getId)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2);

            QueryChain.of(sysUserMapper)
                    .select("*")
                    .from(SysUser.class)
                    .join(SysUser.class, subQuery, on -> {
                        on.eq(SysUser::getId, subQuery.$outerField(SysUser::getId));
                    })
                    .returnMap()
                    .list()
            ;
        }
    }

}
