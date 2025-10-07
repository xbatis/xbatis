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

import java.util.HashSet;
import java.util.Set;

public enum DbType {

    UNKNOWN(new KeywordWrap("", ""), new HashSet<>()),

    H2(new KeywordWrap("`", "`"), new HashSet<>()),

    MYSQL(new KeywordWrap("`", "`"), new HashSet<>()),

    MARIA_DB(new KeywordWrap("`", "`"), new HashSet<>()),

    SQL_SERVER(new KeywordWrap("[", "]"), new HashSet<>()),

    PGSQL(new KeywordWrap("\"", "\""), new HashSet<>()),

    ORACLE(new KeywordWrap("\"", "\"", true), new HashSet<>()),

    DM(new KeywordWrap("\"", "\"", true), new HashSet<>()),

    DB2(new KeywordWrap("\"", "\"", true), new HashSet<>()),

    KING_BASE(new KeywordWrap("\"", "\"", true), new HashSet<>()),

    CLICK_HOUSE(new KeywordWrap("\"", "\"", true), new HashSet<>()),

    SQLITE(new KeywordWrap("\"", "\""), new HashSet<>()),

    OPEN_GAUSS(new KeywordWrap("\"", "\""), new HashSet<>());

    private final KeywordWrap keywordWrap;
    private final Set<String> keywords;

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

    public KeywordWrap getKeywordWrap() {
        return keywordWrap;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public boolean addKeyword(String keyword) {
        return keywords.add(keyword.toUpperCase());
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
