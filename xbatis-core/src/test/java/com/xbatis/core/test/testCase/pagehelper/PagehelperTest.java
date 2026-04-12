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

package com.xbatis.core.test.testCase.pagehelper;


import cn.xbatis.core.sql.util.WhereUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagehelperTest extends BaseTest {
    @BeforeEach
    public void init() {
        super.init();
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("helperDialect", "h2");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params", "count=countSql");
        pageInterceptor.setProperties(properties);

        this.configuration.addInterceptor(pageInterceptor);
    }

    @Test
    public void test() {
        if (TestDataSource.DB_TYPE != DbType.H2) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            List<SysUser> list;

            PageHelper.startPage(1, 10);
            SysUserMapper mapper = session.getMapper(SysUserMapper.class);
            list = mapper.listPagehelper();
            assertTrue(list instanceof Page);
            PageInfo pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 3);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }

            PageHelper.startPage(1, 10);
            mapper = session.getMapper(SysUserMapper.class);
            list = mapper.selectList(SysUser.class, "select * from t_sys_user t where ?", WhereUtil.create(where -> {
                where.gte(SysUser::getId, 1);
            }));
            assertTrue(list instanceof Page);
            pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 3);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }

            PageHelper.startPage(1, 10);
            list = mapper.selectList(SysUser.class, "select * from t_sys_user t where ? and ?", WhereUtil.create(where -> {
                where.eq(SysUser::getId, 1);
            }), WhereUtil.create(where -> {
                where.in(SysUser::getId, 1, 2, 3, 4, 5, 6);
            }));

            assertTrue(list instanceof Page);
            pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 1);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }

            PageHelper.startPage(1, 10);
            list = mapper.listPagehelper2(0);

            assertTrue(list instanceof Page);
            pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 3);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }

            PageHelper.startPage(1, 10);
            list = mapper.listPagehelper3(0, 0);

            assertTrue(list instanceof Page);
            pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 3);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }

            PageHelper.startPage(1, 10);
            list = mapper.listPagehelper4(WhereUtil.create(where -> {
                where.gte(SysUser::getId, 1);
            }), WhereUtil.create(where -> {
                where.in(SysUser::getId, 1, 2, 3, 4, 5, 6, 7);
            }));

            assertTrue(list instanceof Page);
            pageInfo = new PageInfo(list);
            assertEquals(pageInfo.getTotal(), 3);

            for (SysUser sysUser : list) {
                System.out.println(sysUser.getRole_id().intValue());
            }
        }

    }

}
