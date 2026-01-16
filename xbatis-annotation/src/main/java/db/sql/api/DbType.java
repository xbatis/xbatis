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

package db.sql.api;

import java.util.HashSet;
import java.util.Set;

public enum DbType implements IDbType {

    UNKNOWN(Name.UNKNOWN, new KeywordWrap("", "")),

    PGSQL(Name.PGSQL, new KeywordWrap("\"", "\""), ":postgresql:"),

    MYSQL(Name.MYSQL, new KeywordWrap("`", "`"), ":mysql:"),

    MARIA_DB(Name.MARIA_DB, new KeywordWrap("`", "`"), ":mariadb:"),

    SQL_SERVER(Name.SQL_SERVER, new KeywordWrap("[", "]"), ":sqlserver:"),

    ORACLE(Name.ORACLE, new KeywordWrap("\"", "\"", true), ":oracle:"),

    DM(Name.DM, new KeywordWrap("\"", "\"", true), ":dm:"),

    GAUSS(Name.GAUSS, new KeywordWrap("\"", "\""), ":opengauss:", ":gaussdb:"),

    H2(Name.H2, new KeywordWrap("`", "`"), ":h2:"),

    SQLITE(Name.SQLITE, new KeywordWrap("\"", "\""), ":sqlite:"),

    KING_BASE(Name.KING_BASE, new KeywordWrap("\"", "\"", true), ":kingbase8:"),

    CLICK_HOUSE(Name.CLICK_HOUSE, new KeywordWrap("\"", "\"", true), ":clickhouse:"),

    DB2(Name.DB2, new KeywordWrap("\"", "\"", true), ":db2:"),

    COBAR(Name.COBAR, new KeywordWrap("`", "`"), DbModel.MYSQL, ":cobar:"),

    ;

    static {
        DbTypes.register(DbType.class);
    }

    //数据库类型名字
    private final String name;
    //数据库关键字环绕
    private final KeywordWrap keywordWrap;
    //数据库关键字集合
    private final Set<String> keywords;
    //数据库模式，用于那些基于某些原数据库扩展的延伸数据库
    private final DbModel dbModel;
    //jdbc url 匹配串，必须全小写
    private final String[] jdbcUrlMatchers;


    DbType(String name, KeywordWrap keywordWrap, String... jdbcUrlMatchers) {
        this(name, keywordWrap, DbModel.DEFAULT, new HashSet<>(), jdbcUrlMatchers);
    }

    DbType(String name, KeywordWrap keywordWrap, DbModel dbModel, String... jdbcUrlMatchers) {
        this(name, keywordWrap, dbModel, new HashSet<>(), jdbcUrlMatchers);
    }

    DbType(String name, KeywordWrap keywordWrap, DbModel dbModel, Set<String> keywords, String... jdbcUrlMatchers) {
        this.name = name;
        this.keywordWrap = keywordWrap;
        this.keywords = keywords;
        this.dbModel = dbModel;
        this.jdbcUrlMatchers = jdbcUrlMatchers;
    }

    /**
     * 官方提供的添加关键字的方法
     * 给所有数据库都加上数据库关键词
     * 此方法已过期，后续使用 DbTypes.addKeyword 替代
     * @param keywords
     */
    @Deprecated
    @SafeVarargs
    public static final void addKeywords(String... keywords) {
        DbTypes.addKeyword(keywords);
    }

    @Override
    public KeywordWrap getKeywordWrap() {
        return keywordWrap;
    }

    @Override
    public Set<String> getKeywords() {
        return keywords;
    }

    public static void main(String[] args) {
        System.out.println(DbType.UNKNOWN.getName());
        DbTypes.addKeyword("!");
    }

    @Override
    public String wrap(String name) {
        if (getKeywords().isEmpty()) {
            return name;
        }
        if (getKeywords().contains(name.toUpperCase())) {
            if (getKeywordWrap().isToUpperCase()) {
                name = name.toUpperCase();
            }
            return getKeywordWrap().getPrefix() + name + getKeywordWrap().getSuffix();
        }
        return name;
    }

    @Override
    public DbModel getDbModel() {
        return this.dbModel;
    }

    public String getName() {
        return name;
    }

    /**
     * 官方提供的添加关键字的方法
     *
     * @param keywords
     * @return 添加是否成功
     */
    @Override
    @SafeVarargs
    public final void addKeyword(String... keywords) {
        IDbType.super.addKeyword(keywords);
    }

    @Override
    public String[] getJdbcUrlMatchers() {
        return jdbcUrlMatchers;
    }

    public static final class Name {

        public static final String UNKNOWN = "UNKNOWN";
        public static final String H2 = "H2";
        public static final String MYSQL = "MYSQL";
        public static final String MARIA_DB = "MARIA_DB";
        public static final String SQL_SERVER = "SQL_SERVER";
        public static final String PGSQL = "PGSQL";
        public static final String ORACLE = "ORACLE";
        public static final String DM = "DM";
        public static final String DB2 = "DB2";
        public static final String KING_BASE = "KING_BASE";
        public static final String CLICK_HOUSE = "CLICK_HOUSE";
        public static final String SQLITE = "SQLITE";
        public static final String GAUSS = "GAUSS";
        public static final String COBAR = "COBAR";

        private Name() {

        }
    }
}