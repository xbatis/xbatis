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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DbTypes {

    private static final Map<String, IDbType> DB_TYPE_MAP = new ConcurrentHashMap<>();

    private static final Map<String, IDbType> JDBC_URL_MATCHER_MAP = new ConcurrentHashMap<>();

    static {
        DbTypes.register(DbType.class);
    }

    /**
     * 注册 IDbType
     * 所有 IDbType 的实现类 都必须调用此方法 才能注入
     * @param dbTypeClass
     */
    public static <T extends Enum<T> & IDbType> void register(Class<T> dbTypeClass) {
        if (!(Enum.class.isAssignableFrom(dbTypeClass))) {
            throw new RuntimeException(dbTypeClass + " must be an enum");
        }

        if (!(IDbType.class.isAssignableFrom(dbTypeClass))) {
            throw new RuntimeException(dbTypeClass + " must implement IDbType");
        }

        T[] values = dbTypeClass.getEnumConstants();
        for (T dbType : values) {
            if (DB_TYPE_MAP.containsKey(dbType.getName()) && DB_TYPE_MAP.get(dbType.getName()) != dbType) {
                throw new RuntimeException(dbType.getName() + " is registered");
            }
            DB_TYPE_MAP.put(dbType.getName(), dbType);
            for (String key : dbType.getJdbcUrlMatchers()) {
                IDbType existsDbType = JDBC_URL_MATCHER_MAP.get(key);
                if (existsDbType != null && existsDbType != dbType) {
                    //throw new RuntimeException(dbType.getName() + "'s " + key + " jdbcUrlMatcher is exists");
                }
                JDBC_URL_MATCHER_MAP.put(key, dbType);
            }
        }
    }

    /**
     * 根据 dbType name 获取 IDbType
     *
     * @param name dbType name
     * @return IDbType
     */
    public static IDbType getByName(String name) {
        return DB_TYPE_MAP.get(name);
    }

    /**
     * 官方提供的添加关键字的方法
     * 给所有数据库都加上数据库关键词
     *
     * @param keywords
     */
    @SafeVarargs
    public static void addKeyword(String... keywords) {
        if (DB_TYPE_MAP.isEmpty()) {
            throw new RuntimeException("DbType instance is not registered to DbTypes yet");
        }
        DB_TYPE_MAP.entrySet().forEach(e -> {
            e.getValue().addKeyword(keywords);
        });
    }

    /**
     * 根据jdbc url key
     *
     * @param key 例如 :mysql:
     * @return IDbType
     */
    public static IDbType getDbTypeByUrlKey(String key) {
        return JDBC_URL_MATCHER_MAP.get(key);
    }
}
