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

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysRoleMapper;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.vo.SysRoleVo3;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MapGroupWithKeyTest extends BaseTest {

    @Test
    public void mapWithKeyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            //最强mapWithKey 完全自己构建
            Map<Integer, List<SysUser>> map = QueryChain.of(sysUserMapper).mapGroupWithKey(SysUser::getRole_id);
            assertEquals(2, map.size());
            System.out.println(map);
            assertEquals( 1,map.get(0).size());
            assertEquals(  2,map.get(1).size());
            assertInstanceOf(SysUser.class, map.get(1).get(0));
        }
    }

    @Test
    public void mapWithKey2Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            //最强mapWithKey 完全自己构建
            Map<String, List<SysUser>> map = QueryChain.of(sysUserMapper).mapGroupWithKey(i->i.getRole_id()+"");
            assertEquals(2, map.size());
            System.out.println(map);
            assertEquals( 1,map.get("0").size());
            assertEquals(  2,map.get("1").size());
            assertInstanceOf(SysUser.class, map.get("1").get(0));
        }
    }

    @Test
    public void mapWithKey3Test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            //最强mapWithKey 完全自己构建
            Map<String, List<SysUser>> map = QueryChain.of(sysUserMapper).mapGroupWithKey(i->i.getRole_id()!=null && i.getRole_id()>0 ,i->i.getRole_id()+"");
            assertEquals(1, map.size());
            System.out.println(map);
            assertEquals( 2,map.get("1").size());
            assertInstanceOf(SysUser.class, map.get("1").get(0));
        }
    }
}
