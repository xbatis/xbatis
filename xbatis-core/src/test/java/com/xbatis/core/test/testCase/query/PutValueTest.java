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
import com.xbatis.core.test.vo.PutValueEnum;
import com.xbatis.core.test.vo.PutValueVo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PutValueTest extends BaseTest {

    @Test
    public void putEnumValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<PutValueVo> list = QueryChain.of(sysUserMapper)
                    .orderBy(SysUser::getId)
                    .returnType(PutValueVo.class)
                    .list();
            list.stream().forEach(System.out::println);
            assertEquals(list.get(0).getEnumName(), PutValueEnum.ENUM1.getName());
            assertEquals(list.get(1).getEnumName(), PutValueEnum.ENUM2.getName());
            assertEquals(list.get(0).getEnumName2(), PutValueEnum.ENUM1.getName());
            assertEquals(list.get(1).getEnumName2(), PutValueEnum.ENUM2.getName());
        }
    }

    @Test
    public void putEnumDefaultValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            PutValueVo vo = QueryChain.of(sysUserMapper)
                    .eq(SysUser::getId, 3)
                    .returnType(PutValueVo.class)
                    .get();
            System.out.println(vo);
            assertNull(vo.getEnumName());
            assertEquals(vo.getDefaultEnumName(), "NULL");
            assertNull(vo.getEnumName2());
            assertEquals(vo.getDefaultEnumName2(), "NULL");
        }
    }

    @Test
    public void putValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<PutValueVo> list = QueryChain.of(sysUserMapper)
                    .orderBy(SysUser::getId)
                    .returnType(PutValueVo.class)
                    .list();
            list.stream().forEach(System.out::println);
            assertEquals(list.get(0).getEnumName(), PutValueEnum.ENUM1.getName());
            assertEquals(list.get(1).getEnumName(), PutValueEnum.ENUM2.getName());
            assertEquals(list.get(0).getEnumName2(), PutValueEnum.ENUM1.getName());
            assertEquals(list.get(1).getEnumName2(), PutValueEnum.ENUM2.getName());
        }
    }

    @Test
    public void putDefaultValueTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            PutValueVo vo = QueryChain.of(sysUserMapper)
                    .eq(SysUser::getId, 3)
                    .returnType(PutValueVo.class)
                    .get();
            System.out.println(vo);
            assertNull(vo.getEnumName());
            assertEquals(vo.getDefaultEnumName(), "NULL");
            assertNull(vo.getEnumName2());
            assertEquals(vo.getDefaultEnumName2(), "NULL");
        }
    }
}
