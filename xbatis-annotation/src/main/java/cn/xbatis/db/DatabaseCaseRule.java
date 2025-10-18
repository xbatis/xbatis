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

package cn.xbatis.db;

public enum DatabaseCaseRule {
    // 默认 - 不处理 未设置 双重含义
    DEFAULT,
    // 默认 - 不处理
    NONE,
    // 全大写
    UPPERCASE,
    // 全小写
    LOWERCASE,

    DatabaseNamingRule() {

    };

    public String convert(String name) {
        if (this == DEFAULT || this == NONE) {
            return name;
        } else if (this == UPPERCASE) {
            return name.toUpperCase();
        }
        return name.toLowerCase();
    }
}
