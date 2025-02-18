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

package cn.xbatis.core.db.reflect;


import cn.xbatis.core.exception.NotTableClassException;
import cn.xbatis.db.annotations.Table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * table 信息库
 */
public final class Tables {

    private static final Map<Class, TableInfo> CACHE = new ConcurrentHashMap<>();

    private Tables() {

    }


    /**
     * 获取表的信息
     *
     * @param entity
     * @return
     */

    public static TableInfo get(Class entity) {
        if (!entity.isAnnotationPresent(Table.class)) {
            throw new NotTableClassException(entity);
        }
        return CACHE.computeIfAbsent(entity, key -> new TableInfo(entity));
    }

    public static boolean contains(Class entity) {
        return CACHE.containsKey(entity);
    }
}
