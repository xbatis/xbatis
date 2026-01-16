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

package com.xbatis.core.test;

import db.sql.api.DbModel;
import db.sql.api.IDbType;
import db.sql.api.KeywordWrap;

import java.util.HashSet;
import java.util.Set;

public enum MyDbType implements IDbType {

    LIKE_MYSQL(Name.LIKE_MYSQL, new KeywordWrap("`", "`"), DbModel.MYSQL, ":mysql:"),

    LIKE_PGSQL(Name.LIKE_PGSQL, new KeywordWrap("\"", "\""), DbModel.PGSQL, ":postgresql:"),

    LIKE_ORACLE(Name.LIKE_ORACLE, new KeywordWrap("\"", "\"", true), DbModel.ORACLE, ":oracle:"),

    ;

    //数据库类型名字
    private final String name;
    //数据库关键字环绕
    private final KeywordWrap keywordWrap;
    //数据库关键字集合
    private final Set<String> keywords = new HashSet<>();
    //数据库模式，用于那些基于某些原数据库扩展的延伸数据库
    private final DbModel dbModel;
    //jdbc url 匹配串，必须全小写
    private final String[] jdbcUrlMatchers;

    MyDbType(String name, KeywordWrap keywordWrap, DbModel dbModel, String... jdbcUrlMatchers) {
        this.name = name;
        this.keywordWrap = keywordWrap;
        this.dbModel = dbModel;
        this.jdbcUrlMatchers = jdbcUrlMatchers;
    }

    @Override
    public KeywordWrap getKeywordWrap() {
        return keywordWrap;
    }

    @Override
    public Set<String> getKeywords() {
        return keywords;
    }

    /**
     * 官方提供的添加关键字的方法
     *
     * @param keywords
     * @return 添加是否成功
     */
    @SafeVarargs
    public final void addKeyword(String... keywords) {
        IDbType.super.addKeyword(keywords);
    }

    @Override
    public DbModel getDbModel() {
        return this.dbModel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getJdbcUrlMatchers() {
        return this.jdbcUrlMatchers;
    }

    public static final class Name {

        public static final String LIKE_MYSQL = "LIKE_MYSQL";

        public static final String LIKE_PGSQL = "LIKE_PGSQL";

        public static final String LIKE_ORACLE = "LIKE_ORACLE";

        private Name() {
        }
    }
}
