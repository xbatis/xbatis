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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DbType 接口 ； 实现类 最好是一个是个 enum 类
 */
public interface IDbType {

    String getName();

    KeywordWrap getKeywordWrap();

    Set<String> getKeywords();

    default String wrap(String name) {
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

    DbModel getDbModel();

    default void addKeyword(String... keywords) {
        getKeywords().addAll(Arrays.stream(keywords).collect(Collectors.toList()));
    }

    String[] getJdbcUrlMatchers();
}
