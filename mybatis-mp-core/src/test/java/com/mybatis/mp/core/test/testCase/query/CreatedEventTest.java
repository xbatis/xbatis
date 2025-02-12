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

package com.mybatis.mp.core.test.testCase.query;

import cn.mybatis.mp.core.sql.executor.chain.QueryChain;
import com.mybatis.mp.core.test.DO.SysRole;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.mapper.SysUserMapper;
import com.mybatis.mp.core.test.testCase.BaseTest;
import com.mybatis.mp.core.test.vo.CreatedEventTestVo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreatedEventTest extends BaseTest {


    @Test
    public void createdEventTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<CreatedEventTestVo> list = QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .returnType(CreatedEventTestVo.class)
                    .list();

            list.forEach(item -> {
                assertEquals(item.getSourcePut(), "CreatedEventTestVo");
                assertEquals(item.getCreatedEventNestedTestVo().getSourcePut(), "CreatedEventNestedTestVo");
                assertEquals(item.getCreatedEventFetchTestVo().getSourcePut(), "CreatedEventFetchTestVo");
            });

        }
    }
}
