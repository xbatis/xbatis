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

public class KeywordWrap {

    private final String prefix;
    private final String suffix;

    private final boolean toUpperCase;

    public KeywordWrap(String prefix, String suffix) {
        this(prefix, suffix, false);
    }

    public KeywordWrap(String prefix, String suffix, boolean toUpperCase) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.toUpperCase = toUpperCase;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }
}
