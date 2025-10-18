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
import cn.xbatis.db.DatabaseCaseRule;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.DbType;
import db.sql.api.impl.SQLImplGlobalConfig;
import db.sql.api.impl.cmd.basic.Column;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.SQLPrinter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableAndColumnCaseTest extends BaseTest {

    private void setDatabaseCaseRule(DatabaseCaseRule databaseCaseRule) {
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
        setDatabaseCaseRule(DatabaseCaseRule.DEFAULT);
        assertEquals("testcase", new Table("testcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));
        assertEquals("testcase", new Column("testcase").getName(DbType.H2));

        assertEquals("tesTcase", new Table("tesTcase").getName(DbType.H2));
        assertEquals("tesTcase2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));
        assertEquals("tesTcase", new Column("tesTcase").getName(DbType.H2));


    }


    @Test
    public void lowerTest() {
        setDatabaseCaseRule(DatabaseCaseRule.LOWERCASE);
        assertEquals("testcase", new Table("testcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));
        assertEquals("testcase", new Column("testcase").getName(DbType.H2));

        assertEquals("testcase", new Table("tesTcase").getName(DbType.H2));
        assertEquals("testcase2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));
        assertEquals("testcase", new Column("tesTcase").getName(DbType.H2));
    }

    @Test
    public void upperTest() {
        setDatabaseCaseRule(DatabaseCaseRule.UPPERCASE);
        assertEquals("TESTCASE", new Table("testcase").getName(DbType.H2));
        assertEquals("TESTCASE2", new TableField(new Table("testcase"), "testcase2").getName(DbType.H2));
        assertEquals("TESTCASE", new Column("testcase").getName(DbType.H2));

        assertEquals("TESTCASE", new Table("tesTcase").getName(DbType.H2));
        assertEquals("TESTCASE2", new TableField(new Table("testcase"), "tesTcase2").getName(DbType.H2));
        assertEquals("TESTCASE", new Column("tesTcase").getName(DbType.H2));
    }


    @Test
    public void lowerTest2() {
        setDatabaseCaseRule(DatabaseCaseRule.LOWERCASE);
        String sql = SQLPrinter.sql(QueryChain.of(null)
                .select(SysUser::getId)
                .from(SysUser.class));

        assertEquals("SELECT t.id FROM t_sys_user t", sql.trim());
    }


    @AfterEach
    public void after1() {
        setDatabaseCaseRule(DatabaseCaseRule.DEFAULT);
    }

}
