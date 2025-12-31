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

package db.sql.api.impl;

import cn.xbatis.db.DatabaseCaseRule;
import db.sql.api.DbType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SQLImplGlobalConfig {

    private static final Object NULL = new Object();
    private final static Map<DbType, Object> DATABASE_CASE_RULES = new ConcurrentHashMap<>();
    private static volatile Object DATABASE_CASE_RULE = NULL;

    /**
     * 数据库命名规则 默认 不处理
     *
     * @return 命名规则
     */
    public static DatabaseCaseRule getDatabaseCaseRule() {
        if (DATABASE_CASE_RULE == NULL) {
            DATABASE_CASE_RULE = DatabaseCaseRule.DEFAULT;
        }
        return (DatabaseCaseRule) DATABASE_CASE_RULE;
    }

    /**
     * 设置数据库命名规则 默认 不处理
     *
     * @return 是否成功
     */
    public static boolean setDatabaseCaseRule(DatabaseCaseRule databaseCaseRule) {
        if (DATABASE_CASE_RULE == NULL) {
            DATABASE_CASE_RULE = databaseCaseRule;
            return true;
        }

        return false;
    }


    /**
     * 数据库命名规则 默认 不处理
     *
     * @return 命名规则
     */
    public static DatabaseCaseRule getDatabaseCaseRule(DbType dbType) {
        Object value = DATABASE_CASE_RULES.computeIfAbsent(dbType, (i) -> NULL);
        if (value == NULL) {
            return null;
        }
        return (DatabaseCaseRule) value;
    }

    /**
     * 设置数据库命名规则 默认 不处理
     *
     */
    public static void setDatabaseCaseRule(DbType dbType, DatabaseCaseRule databaseCaseRule) {
        DATABASE_CASE_RULES.computeIfAbsent(dbType, i -> databaseCaseRule);
    }
}
