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
import com.mybatis.mp.core.test.mapper.SysRoleMapper;
import com.mybatis.mp.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class MapWithKeyTest extends BaseTest {

    @Test
    public void mapWithKeyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            //最强mapWithKey 完全自己构建
            Map<Integer, SysRole> map = QueryChain.of(sysRoleMapper).mapWithKey(SysRole::getId);
            //根据where条件
            Map<String, SysRole> map1 = sysRoleMapper.mapWithKey(SysRole::getName, where -> {
                where.gt(SysRole::getCreateTime, LocalDate.parse("2023-01-01").atStartOfDay());
            });
            //根据多个id
            Map<Integer, SysRole> map2 = sysRoleMapper.mapWithKey(SysRole::getId, 1, 2, 3);
            //根据List<id>
            Map<Integer, SysRole> map3 = sysRoleMapper.mapWithKey(SysRole::getId, Arrays.asList(1, 2, 3));


            assertEquals(2, map.size());
            System.out.println(map);
            assertInstanceOf(SysRole.class, map.get(1));
            assertEquals(map.get(1).getName(), "测试");
            assertEquals(map.get(2).getName(), "运维");
        }
    }

    @Test
    public void mapWithKeyTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            this.configuration.setMapUnderscoreToCamelCase(true);
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            //最强mapWithKey 完全自己构建
            Map<Integer, SysRole> map = QueryChain.of(sysRoleMapper).mapWithKey(SysRole::getId);
            //根据where条件
            Map<String, SysRole> map1 = sysRoleMapper.mapWithKey(SysRole::getName, where -> {
                where.gt(SysRole::getCreateTime, LocalDate.parse("2023-01-01").atStartOfDay());
            });
            //根据多个id
            Map<Integer, SysRole> map2 = sysRoleMapper.mapWithKey(SysRole::getId, 1, 2, 3);
            //根据List<id>
            Map<Integer, SysRole> map3 = sysRoleMapper.mapWithKey(SysRole::getId, Arrays.asList(1, 2, 3));


            assertEquals(2, map.size());
            System.out.println(map);
            assertInstanceOf(SysRole.class, map.get(1));
            assertEquals(map.get(1).getName(), "测试");
            assertEquals(map.get(2).getName(), "运维");
        }
    }
}
