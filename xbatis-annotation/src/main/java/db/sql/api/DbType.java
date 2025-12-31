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

package db.sql.api;

import java.util.*;

public enum DbType {

    UNKNOWN(new KeywordWrap("", ""), Collections.emptySet()),

    H2(new KeywordWrap("`", "`"), Collections.emptySet()),

    MYSQL(new KeywordWrap("`", "`"), Collections.emptySet()),

    MARIA_DB(new KeywordWrap("`", "`"), Collections.emptySet()),

    SQL_SERVER(new KeywordWrap("[", "]"), Collections.emptySet()),

    PGSQL(new KeywordWrap("\"", "\""), Collections.emptySet()),

    ORACLE(new KeywordWrap("\"", "\"", true), Collections.emptySet()),

    DM(new KeywordWrap("\"", "\"", true), Collections.emptySet()),

    DB2(new KeywordWrap("\"", "\"", true), Collections.emptySet()),

    KING_BASE(new KeywordWrap("\"", "\"", true), Collections.emptySet()),

    CLICK_HOUSE(new KeywordWrap("\"", "\"", true), Collections.emptySet()),

    SQLITE(new KeywordWrap("\"", "\""), Collections.emptySet()),

    GAUSS(new KeywordWrap("\"", "\""), Collections.emptySet());

    private final KeywordWrap keywordWrap;
    private Set<String> keywords;

    DbType(KeywordWrap keywordWrap, Set<String> keywords) {
        this.keywordWrap = keywordWrap;
        this.keywords = keywords;
    }

    public static DbType getByName(String name) {
        DbType[] dbTypes = values();
        for (DbType dbType : dbTypes) {
            if (dbType.name().equals(name)) {
                return dbType;
            }
        }
        return MYSQL;
    }

    /**
     * 官方提供的添加关键字的方法
     * 给所有数据库都加上数据库关键词
     *
     * @param keywords
     */
    @SafeVarargs
    public static final void addKeywords(String... keywords) {
        for (DbType dbType : DbType.values()) {
            dbType.addKeyword(keywords);
        }
    }

    public KeywordWrap getKeywordWrap() {
        return keywordWrap;
    }

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
    public final boolean addKeyword(String... keywords) {
        Set<String> keywordsSet = new HashSet<>(this.keywords);
        List<String> newKeywords = new ArrayList<>(keywords.length);
        for (String keyword : keywords) {
            newKeywords.add(keyword.toUpperCase());
        }
        boolean bool = keywordsSet.addAll(newKeywords);
        this.keywords = Collections.unmodifiableSet(keywordsSet);
        return bool;
    }

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
}
