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

import cn.xbatis.core.db.reflect.FetchInfo;
import cn.xbatis.core.db.reflect.ResultInfo;
import cn.xbatis.core.db.reflect.ResultInfos;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbatis.core.test.DO.AddrArchive;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.AddrArchiveMapper;
import com.xbatis.core.test.mapper.SysRoleMapper;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import com.xbatis.core.test.vo.*;
import db.sql.api.DbType;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FetchTest extends BaseTest {

    @Test
    public void fetchCnt() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            LongAdder longAdder = new LongAdder();
            Map<Integer, FetchSysRoleVo2> map = QueryChain.of(sysRoleMapper)
                    //.log(false)
                    .log(getClass(), "fetchCnt")
                    .fetchFilter(FetchSysRoleVo2::getSysRoleNames, where -> {
                        longAdder.increment();
                    })
                    .returnType(FetchSysRoleVo2.class)
                    .mapWithKey(FetchSysRoleVo2::getId);
            System.out.println(map);

            System.out.println(longAdder.sum());
            assertEquals(1, longAdder.sum());
            assertEquals(map.get(1).getCnts(), 2);
            assertEquals(map.get(1).getCnts2(), 2);
            assertEquals(map.get(1).getCnts3(), 2);
            assertEquals(map.get(1).getSysRoleNames().get(0), "test1");
            assertEquals(map.get(1).getSysRoleNames().get(1), "test2");
            assertEquals(map.get(1).getRoleName(), "测试");
            assertEquals(map.get(2).getRoleName(), "运维");
        }
    }

    @Test
    public void onRowEvent() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LongAdder longAdder = new LongAdder();
            QueryChain.of(sysUserMapper)
                    .returnType(SysUser.class, sysUser -> {
                        System.out.println(sysUser);
                        if (sysUser instanceof SysUser) {
                            longAdder.increment();
                        } else {
                            throw new RuntimeException("returnTypeEach item not instanceof SysUser");
                        }
                    })
                    .mapWithKey(SysUser::getRole_id);
            System.out.println(longAdder.sum());
            assertTrue(longAdder.sum() > 0);
        }
    }

    @Test
    public void onRowEventVO() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LongAdder longAdder = new LongAdder();
            QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .join(SysUser.class, SysRole.class)
                    .returnType(SysUserVo.class, sysUserVo -> {
                        System.out.println(sysUserVo);
                        if (sysUserVo instanceof SysUserVo) {
                            longAdder.increment();
                        } else {
                            throw new RuntimeException("returnTypeEach item not instanceof SysUser");
                        }
                    })
                    .mapWithKey(SysUserVo::getId);
            System.out.println(longAdder.sum());
            assertTrue(longAdder.sum() > 0);
        }
    }

    @Test
    public void fetchWithNoRootField() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserVo> list = QueryChain.of(sysUserMapper)
                    .select(SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2)
                    .returnType(FetchSysUserVo.class)
                    .list();
            System.out.println(list);
            assertEquals("测试", list.get(0).getSysRole().getName());
        }
    }


    @Test
    public void fetchByAllNested() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            List<FetchSysRoleVoWhenHasNested> list = QueryChain.of(sysUserMapper)
                    .select(FetchSysRoleVoWhenHasNested.class)
                    .from(SysUser.class)
                    .returnType(FetchSysRoleVoWhenHasNested.class)
                    .list();
            System.out.println(list);
            assertEquals("测试", list.get(1).getSysRole().getName());
            assertEquals("测试", list.get(2).getSysRole().getName());

            assertEquals("admin", list.get(0).getSysUser().getUserName());
            assertEquals("test1", list.get(1).getSysUser().getUserName());
            assertEquals("test2", list.get(2).getSysUser().getUserName());
        }
    }

    @Test
    public void fetchFilterByAllNested() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            List<FetchSysRoleVoWhenHasNested> list = QueryChain.of(sysUserMapper)
                    .select(FetchSysRoleVoWhenHasNested.class)
                    .from(SysUser.class)
                    .fetchFilter(FetchSysRoleVoWhenHasNested::getSysRole, where -> {
                        where.eq(SysRole::getId, 0);
                    })
                    .returnType(FetchSysRoleVoWhenHasNested.class)
                    .list();
            System.out.println(list);
            list.forEach(item -> assertEquals(null, item.getSysRole()));
        }
    }

    @Test
    public void fetchByPropertyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserVo> list = QueryChain.of(sysUserMapper)
                    .select(FetchSysUserVo.class)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2)
                    .fetchFilter(FetchSysUserVo::getSysRole, where -> where.eq(SysRole::getId, 1))
                    .fetchFilter(SysRoleVo::getSysRole, where -> where.eq(SysRole::getId, 1))
                    .returnType(FetchSysUserVo.class)
                    .list();
            System.out.println(list);
            assertEquals("测试", list.get(0).getSysRole().getName());
            assertEquals(list.get(0).getSysRole().getId(), list.get(0).getSysRole().getSysRole().getId());
        }
    }

    @Test
    public void fetchByPropertyTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserVo> list = QueryChain.of(sysUserMapper)
                    .select(FetchSysUserVo.class)
                    .select(SysUser::getRole_id)
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2)
                    .returnType(FetchSysUserVo.class)
                    .list();
            System.out.println(list);
            assertEquals("测试", list.get(0).getSysRole().getName());
        }
    }


    @Test
    public void fetchMutiByPropertyTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<MutiFetchSysUserVo> list = QueryChain.of(sysUserMapper)
                    .select(MutiFetchSysUserVo.class)
                    .from(SysUser.class)
                    .returnType(MutiFetchSysUserVo.class)
                    .list();
            System.out.println(list);
            assertEquals(Integer.valueOf(1), list.get(1).getSysRole().get(0).getId());
            assertEquals("测试", list.get(1).getSysRole().get(0).getName());
            assertEquals(Integer.valueOf(1), list.get(1).getSysRole().size());
        }
    }


    @Test
    public void fetchMutiByPropertyTest2() {
        if (TestDataSource.DB_TYPE == DbType.SQL_SERVER || TestDataSource.DB_TYPE == DbType.DB2) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysRole sysRole = new SysRole();
            sysRole.setId(4);
            sysRole.setName("cesh2");
            sysRole.setCreateTime(LocalDateTime.now());
            sysRoleMapper.save(sysRole);

            SysUser sysUser = new SysUser();
            sysUser.setId(4);
            sysUser.setRole_id(4);
            sysUser.setUserName("xx");
            sysUser.setCreate_time(LocalDateTime.now());
            sysUserMapper.save(sysUser);

            QueryChain<FetchSysRoleVo> queryChain = QueryChain.of(sysRoleMapper)
                    .select(FetchSysRoleVo.class)
                    .from(SysRole.class)
                    .returnType(FetchSysRoleVo.class);


            FetchSysRoleVo roleVo = new FetchSysRoleVo();
            roleVo.setId(1);
            roleVo.setName("测试");

            FetchSysUserForRoleVo userVo = new FetchSysUserForRoleVo();
            userVo.setId(2);
            userVo.setRole_id(1);
            userVo.setUserName("test1");
            userVo.setPassword("123456");
            userVo.setCreate_time(LocalDateTime.parse("2023-10-11T15:16:17"));

            FetchSysUserForRoleVo userVo2 = new FetchSysUserForRoleVo();
            userVo2.setId(3);
            userVo2.setRole_id(1);
            userVo2.setUserName("test2");
            userVo2.setCreate_time(LocalDateTime.parse("2023-10-12T15:16:17"));

            roleVo.setSysRole(Arrays.asList(userVo, userVo2));
            roleVo.setSysRoleNames(Arrays.asList("test1", "test2"));

            Map<Integer, FetchSysRoleVo> voMap = queryChain.mapWithKey(FetchSysRoleVo::getId);

            System.out.println(voMap);
            assertEquals(roleVo, voMap.get(1));
            assertEquals("xx", voMap.get(4).getSysRoleNames().get(0));
            assertEquals("xx", voMap.get(4).getSysRole().get(0).getUserName());

            List<FetchSysRoleVo> list = queryChain
                    .list();
            System.out.println(list);
            assertEquals(roleVo, list.get(0));

            Cursor<FetchSysRoleVo> cursor = queryChain
                    .cursor();
            list.clear();
            cursor.forEach(item -> list.add(item));

            System.out.println(list);
            assertEquals(roleVo, list.get(0));

            final List<FetchSysRoleVo3> list3 = queryChain
                    .returnType(FetchSysRoleVo3.class)
                    .list();

            System.out.println(list3);
            assertEquals(Arrays.asList("test1", "test2"), list3.get(0).getSysRoleNames());

            Cursor<FetchSysRoleVo3> cursor3 = queryChain
                    .returnType(FetchSysRoleVo3.class)
                    .cursor();
            list3.clear();
            cursor3.forEach(item -> {
                System.out.println(">>>xxxxxxxxxxxxx>>" + item);
                list3.add(item);
            });

            System.out.println(list3);
            assertEquals(Arrays.asList("test1", "test2"), list3.get(0).getSysRoleNames());


            List<FetchSysRoleVo3> list4 = queryChain
                    .eq(SysRole::getId, -1)
                    .returnType(FetchSysRoleVo3.class)
                    .list();

            assertEquals(list4.size(), 0);
//            assertEquals(Integer.valueOf(1), list.get(1).getSysRole().get(0).getId());
//            assertEquals("测试", list.get(1).getSysRole().get(0).getName());
//            assertEquals(Integer.valueOf(1), list.get(1).getSysRole().size());
        }
    }

    @Test
    public void fetchResultEntityVoidTest2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<FetchSysRoleVo3> list3 = new ArrayList<>();
            Cursor<FetchSysRoleVo3> cursor3 = QueryChain.of(sysRoleMapper)
                    .select(FetchSysRoleVo3.class)
                    .from(SysRole.class)
                    .returnType(FetchSysRoleVo3.class)
                    .cursor();

            cursor3.forEach(item -> {
                System.out.println(">>>xxxxxxxxxxxxx>>" + item);
                list3.add(item);
            });

            assertEquals(Arrays.asList("test1", "test2"), list3.get(0).getSysRoleNames());
        }
    }

    @Test
    public void fetchNullValue() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserNullVo> list = QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .returnType(FetchSysUserNullVo.class)
                    .list();
            System.out.println(list);
            assertEquals(3, list.size());
            assertEquals("NULL", list.get(0).getRoleName());
            assertEquals("测试", list.get(1).getRoleName());
            assertEquals("测试", list.get(2).getRoleName());
        }
    }

    @Test
    public void fetchNullValue2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserNullVo> list = QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .returnType(FetchSysUserNullVo.class)
                    .eq(SysUser::getId, 1)
                    .list();
            System.out.println(list);
            assertEquals(1, list.size());
            assertEquals("NULL", list.get(0).getRoleName());
        }
    }

    @Test
    public void fetchNested() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            List<FetchSysUserVo2> list = QueryChain.of(sysUserMapper)
                    .from(SysUser.class)
                    .innerJoin(SysUser::getRole_id, SysRole::getId)
                    .returnType(FetchSysUserVo2.class)
                    .eq(SysUser::getId, 2)
                    .list();
            System.out.println(list);
            assertEquals(1, list.size());

            assertEquals("测试ENUM2", list.get(0).getSysRole().getIdName());
            assertEquals("测试ENUM2", list.get(0).getSysRole().getIdName2());

            assertEquals("测试ENUM1", list.get(0).getSysRole().getIdName3());
            assertEquals("测试ENUM1", list.get(0).getSysRole().getIdName4());
            assertEquals("测试", list.get(0).getSysRole().getSysRole().getName());
        }
    }

    @Test
    public void fetchLimit() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<FetchSysRoleVo4> listResult = QueryChain.of(sysRoleMapper)
                    .select(FetchSysRoleVo4.class)
                    .from(SysRole.class)
                    .returnType(FetchSysRoleVo4.class)
                    .list();

            System.out.println(listResult);

            assertEquals(listResult.size(), 2);
            assertEquals(listResult.get(0).getSysRole().size(), 1);
            assertEquals(listResult.get(1).getSysRole().size(), 0);

        }
    }

    @Test
    public void fetchLimit2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<FetchSysRoleVo5> listResult = QueryChain.of(sysRoleMapper)
                    .select(FetchSysRoleVo5.class)
                    .from(SysRole.class)
                    .returnType(FetchSysRoleVo5.class)
                    .list();

            System.out.println(listResult);

            assertEquals(listResult.size(), 2);
            assertEquals(listResult.get(0).getSysRole().size(), 1);
            assertEquals(listResult.get(1).getSysRole().size(), 0);

        }
    }

    @Test
    public void fetchLimit3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<FetchSysRoleVo5> listResult = QueryChain.of(sysRoleMapper)
                    .select(FetchSysRoleVo5.class)
                    .from(SysRole.class)
                    .returnType(FetchSysRoleVo5.class)
                    .list();

            System.out.println(listResult);

            assertEquals(listResult.size(), 2);
            assertEquals(listResult.get(0).getSysRole2().get(0).getId(), 3);

        }
    }

    @Test
    public void fetchSameTargetTest() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            AddrArchiveMapper addrArchiveMapper = session.getMapper(AddrArchiveMapper.class);

            List<AddrArchiveFetchTestVO> listResult = QueryChain.of(addrArchiveMapper)
                    .from(AddrArchive.class)
                    .leftJoin(AddrArchive::getId, 1, AddrArchive::getId, 2)
                    .eq(AddrArchive::getCid, 3)
                    .returnType(AddrArchiveFetchTestVO.class)
                    .list();

            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listResult));

            assertEquals(listResult.size(), 3);
            assertEquals(listResult.get(0).getAddrArchiveNested().getDname(), "章贡区");
            assertEquals(listResult.get(1).getAddrArchiveNested().getDname(), "瑞金市");
            assertEquals(listResult.get(2).getAddrArchiveNested().getDname(), "兴国县");

            assertEquals(listResult.get(0).getAddrArchiveNested2().getDname(), "章贡区");
            assertEquals(listResult.get(1).getAddrArchiveNested2().getDname(), "瑞金市");
            assertEquals(listResult.get(2).getAddrArchiveNested2().getDname(), "兴国县");

            ResultInfo resultInfo = ResultInfos.get(AddrArchiveFetchTestVO.class);

            List<FetchInfo> fetchList = resultInfo.getFetchInfoMap().get(AddrArchiveNested.class);

            assertEquals("x2$pid", fetchList.get(3).getValueColumn());
            assertEquals("x2$cid", fetchList.get(4).getValueColumn());
            assertEquals("x2$did", fetchList.get(5).getValueColumn());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void fetchCacheTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            QueryChain<FetchSysRoleCacheVo> queryChain = QueryChain.of(sysRoleMapper)
                    .fetchFilter(FetchSysRoleCacheVo::getSysRoleNames, where -> {

                    })
                    .returnType(FetchSysRoleCacheVo.class);

            List<FetchSysRoleCacheVo> list = queryChain.list();
            System.out.println(list);

            assertEquals(list.get(0).getSysRoleNames().get(0), "test1");
            assertEquals(list.get(0).getSysRoleNames().get(1), "test2");
            assertEquals(list.get(1).getSysRoleNames().get(0), "写死的缓存ID2");
            assertEquals(list.get(0).getName(), "测试");
            assertEquals(list.get(1).getName(), "运维");
            assertEquals(list.get(0).getSysUsers().get(0).getId(), 2);
            assertEquals(list.get(0).getSysUsers().get(1).getId(), 3);
        }


        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            QueryChain<FetchSysRoleCacheVo> queryChain = QueryChain.of(sysRoleMapper)
                    .fetchFilter(FetchSysRoleCacheVo::getSysRoleNames, where -> {

                    })
                    .returnType(FetchSysRoleCacheVo.class);

            List<FetchSysRoleCacheVo> list = queryChain.list();
            System.out.println(list);

            assertEquals(list.get(0).getSysRoleNames().get(0), "123444");
            assertEquals(list.get(0).getSysRoleNames().get(1), "123444");
            assertEquals(list.get(1).getSysRoleNames().get(0), "写死的缓存ID2");
            assertEquals(list.get(0).getName(), "测试");
            assertEquals(list.get(1).getName(), "运维");
            assertEquals(list.get(0).getSysUsers().get(0).getId(), 2);
            assertEquals(list.get(0).getSysUsers().get(1).getId(), 3);
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);
            QueryChain<FetchSysRoleCacheVo> queryChain = QueryChain.of(sysRoleMapper)
                    .fetchFilter(FetchSysRoleCacheVo::getSysRoleNames, where -> {

                    })
                    .fetchEnable(FetchSysRoleCacheVo::getSysRoleNames, false)
                    .returnType(FetchSysRoleCacheVo.class);

            List<FetchSysRoleCacheVo> list = queryChain.list();
            System.out.println(list);


            assertEquals(list.get(0).getSysRoleNames().isEmpty(), true);
            assertEquals(list.get(1).getSysRoleNames().isEmpty(), true);
            assertEquals(list.get(0).getName(), "测试");
            assertEquals(list.get(1).getName(), "运维");
            assertEquals(list.get(0).getSysUsers().get(0).getId(), 2);
            assertEquals(list.get(0).getSysUsers().get(1).getId(), 3);
        }
    }
}
