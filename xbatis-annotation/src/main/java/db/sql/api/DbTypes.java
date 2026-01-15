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

import java.util.HashMap;
import java.util.Map;

public final class DbTypes {

    private static final Map<String, IDbType> DB_TYPE_MAP = new HashMap<>();

    static {
        //静态加载 防止 DbType 未初始化
        DbType.values();
    }

    /**
     * 注册 IDbType
     *
     * @param dbType
     */
    public static void register(IDbType dbType) {
        if (DB_TYPE_MAP.containsKey(dbType.getName())) {
            throw new RuntimeException(dbType.getName() + " is registered");
        }
        if (!(dbType instanceof Enum)) {
            throw new RuntimeException(dbType.getClass() + " must be an enum");
        }
        DB_TYPE_MAP.put(dbType.getName(), dbType);
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
        for (DbType dbType : DbType.values()) {
            dbType.addKeyword(keywords);
        }
    }
}
