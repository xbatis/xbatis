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

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.db.DatabaseCaseRule;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.DbType;
import db.sql.api.impl.SQLImplGlobalConfig;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableAndColumnQueryChainCaseTest extends BaseTest {

    @BeforeAll
    public static void beforeAll() {
        setDatabaseCaseRule(DatabaseCaseRule.DEFAULT);
        clearTableInfo(SysUser.class);
    }

    private static void clearTableInfo(Class entity) {
        try {
            Field field = Tables.class.getDeclaredField("CACHE");
            field.setAccessible(true);
            Map<Class, TableInfo> CACHE = (Map<Class, TableInfo>) field.get(null);
            CACHE.remove(entity);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setDatabaseCaseRule(DatabaseCaseRule databaseCaseRule) {
        try {
            Field field = SQLImplGlobalConfig.class.getDeclaredField("DATABASE_CASE_RULE");
            field.setAccessible(true);
            field.set(null, databaseCaseRule);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void defaultTest() {

        assertEquals("testcase", new Table("testcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));

        assertEquals("tesTcase", new Table("tesTcase").getName(DbType.H2));
        assertEquals("tesTcase2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));


    }


    @Test
    public void lowerTest() {
        setDatabaseCaseRule(DatabaseCaseRule.LOWERCASE);
        assertEquals("testcase", new Table("testcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));

        assertEquals("testcase", new Table("tesTcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));
    }

    @Test
    public void upperTest() {
        setDatabaseCaseRule(DatabaseCaseRule.UPPERCASE);
        assertEquals("TESTCASE", new Table("testcase").getName(DbType.H2));
        assertEquals("TESTCASE2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));

        assertEquals("TESTCASE", new Table("tesTcase").getName(DbType.H2));
        assertEquals("TESTCASE2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));
    }


    @AfterEach
    public void after1() {
        setDatabaseCaseRule(DatabaseCaseRule.DEFAULT);
        clearTableInfo(SysUser.class);
    }

}
