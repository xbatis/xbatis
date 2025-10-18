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

import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableAndColumnKeywordTest extends BaseTest {

    private Set oldKeywordsSet;

    @BeforeEach
    public void before() {
        addKeyWords();
    }

    private void addKeyWords(String... keywords) {
        try {
            Field field = DbType.class.getDeclaredField("keywords");
            field.setAccessible(true);
            oldKeywordsSet = (Set) field.get(DbType.H2);
            if (keywords.length > 0) {
                DbType.H2.addKeyword(keywords);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeKeyWords() {
        try {
            Field field = DbType.class.getDeclaredField("keywords");
            field.setAccessible(true);
            field.set(DbType.H2, oldKeywordsSet);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void defaultTest() {
        assertEquals("1test", new Table("1test").getName(DbType.H2));
        assertEquals("1test2", new TableField(new Table("test"), "1test2").getName(DbType.H2));
        assertEquals("1tesT", new Table("1tesT").getName(DbType.H2));
        assertEquals("1tesT2", new TableField(new Table("test"), "1tesT2").getName(DbType.H2));
    }

    @Test
    public void keywordTest() {
        addKeyWords("test", "test2");
        assertEquals("`test`", new Table("test").getName(DbType.H2));
        assertEquals("`tesT`", new Table("tesT").getName(DbType.H2));
        assertEquals("`test2`", new TableField(new Table("test"), "test2").getName(DbType.H2));
        assertEquals("`tesT2`", new TableField(new Table("test"), "tesT2").getName(DbType.H2));
    }

    @Test
    public void keywordTest2() {
        addKeyWords("TEST", "TEST2");
        assertEquals("`test`", new Table("test").getName(DbType.H2));
        assertEquals("`tesT`", new Table("tesT").getName(DbType.H2));
        assertEquals("`test2`", new TableField(new Table("test"), "test2").getName(DbType.H2));
        assertEquals("`tesT2`", new TableField(new Table("test"), "tesT2").getName(DbType.H2));
    }


    @AfterEach
    public void after1() {
        resumeKeyWords();
    }
}
