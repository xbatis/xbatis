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

    H2(Name.H2, new KeywordWrap("`", "`")),

    MYSQL(Name.MYSQL, new KeywordWrap("`", "`")),

    MARIA_DB(Name.MARIA_DB, new KeywordWrap("`", "`")),

    SQL_SERVER(Name.SQL_SERVER, new KeywordWrap("[", "]")),

    PGSQL(Name.PGSQL, new KeywordWrap("\"", "\"")),

    ORACLE(Name.ORACLE, new KeywordWrap("\"", "\"", true)),

    DM(Name.DM, new KeywordWrap("\"", "\"", true)),

    DB2(Name.DB2, new KeywordWrap("\"", "\"", true)),

    KING_BASE(Name.KING_BASE, new KeywordWrap("\"", "\"", true)),

    CLICK_HOUSE(Name.CLICK_HOUSE, new KeywordWrap("\"", "\"", true)),

    SQLITE(Name.SQLITE, new KeywordWrap("\"", "\"")),

    GAUSS(Name.GAUSS, new KeywordWrap("\"", "\""));

    private final String name;

    private final KeywordWrap keywordWrap;

    private final Set<String> keywords;

    private final DbModel dbModel;

    DbType(String name, KeywordWrap keywordWrap) {
        this(name, keywordWrap, DbModel.DEFAULT, new HashSet<>());
    }

    DbType(String name, KeywordWrap keywordWrap, DbModel dbModel) {
        this(name, keywordWrap, dbModel, new HashSet<>());
    }

    DbType(String name, KeywordWrap keywordWrap, DbModel dbModel, Set<String> keywords) {
        this.name = name;
        this.keywordWrap = keywordWrap;
        this.keywords = keywords;
        this.dbModel = dbModel;
        DbTypes.register(this);
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

    /**
     * 官方提供的添加关键字的方法
     *
     * @param keywords
     * @return 添加是否成功
     */
    @SafeVarargs
    public final void addKeyword(String... keywords) {
        for (String keyword : keywords) {
            getKeywords().add(keyword.toUpperCase());
        }
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

        private Name() {
        }
    }
}