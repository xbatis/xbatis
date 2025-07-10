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
import com.xbatis.core.test.REQ.KeywordLikeREQ;
import com.xbatis.core.test.REQ.KeywordLikeREQ2;
import com.xbatis.core.test.REQ.QueryREQ;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ConditionObjectTest extends BaseTest {

    @Test
    public void eq() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setId(1);
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(1, id);
        }
    }

    @Test
    public void eq2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = new SysUser();
            sysUser.setId(1);
            Integer id = QueryChain.of(sysUserMapper)
                    .where(sysUser)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(1, id);
        }
    }

    @Test
    public void gtAndLt() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setGtId(1);
            queryReq.setLtId(2);
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(null, id);
        }
    }

    @Test
    public void gtAndLte() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setGtId(1);
            queryReq.setLteId(2);
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(2, id);
        }
    }


    @Test
    public void like() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setUserName("ad");
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(1, id);
        }
    }

    @Test
    public void like2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = new SysUser();
            sysUser.setUserName("ad");
            Integer id = QueryChain.of(sysUserMapper)
                    .where(sysUser)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(1, id);
        }
    }

    @Test
    public void between() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            QueryREQ queryReq = new QueryREQ();
            queryReq.setBtIds(new Integer[]{2, 2});
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();

            assertEquals(2, id);
        }
    }


    @Test
    public void gtOrLte() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setId(1);
            queryReq.setId1(2);
            queryReq.setId2(3);
            Integer count = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .count();
            assertEquals(2, count);
        }
    }


    @Test
    public void betweenDays() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setRangeTimes(new LocalDate[]{LocalDate.now(), LocalDate.now()});
            Integer id = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .get();
            assertEquals(null, id);
        }
    }

    @Test
    public void keyword() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            KeywordLikeREQ queryReq = new KeywordLikeREQ();
            queryReq.setKeyword("test");
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .orderBy(SysUser::getId)
                    .list();

            assertEquals(2, list.get(0).getId());
            assertEquals(3, list.get(1).getId());
        }
    }

    @Test
    public void keyword2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            KeywordLikeREQ queryReq = new KeywordLikeREQ();
            queryReq.setKeyword("test");
            queryReq.setId(3);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .orderBy(SysUser::getId)
                    .list();

            assertEquals(3, list.get(0).getId());
        }
    }

    @Test
    public void keyword3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            KeywordLikeREQ2 queryReq = new KeywordLikeREQ2();
            queryReq.setKeyword("test");
            queryReq.setId(3);
            List<SysUser> list = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .orderBy(SysUser::getId)
                    .list();

            assertEquals(2, list.get(0).getId());
            assertEquals(3, list.get(1).getId());
        }
    }

    @Test
    public void gtOrLte3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            QueryREQ queryReq = new QueryREQ();
            queryReq.setId(1);
            queryReq.setId1(2);
            queryReq.setId2(3);
            queryReq.setKeyword("test");
            Integer count = QueryChain.of(sysUserMapper)
                    .where(queryReq)
                    .select(SysUser::getId)
                    .returnType(Integer.class)
                    .count();
            assertEquals(2, count);
        }
    }
}
