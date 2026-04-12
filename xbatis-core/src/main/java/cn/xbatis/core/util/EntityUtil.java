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

package cn.xbatis.core.util;

import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import db.sql.api.Getter;
import db.sql.api.tookit.LambdaUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {

    private static final Map<String, Getter> CACHE = new ConcurrentHashMap<>();

    public <T> Getter<T> createGetter(Class<T> clazz, String fieldName) {
        String cacheKey = clazz.getName() + "." + fieldName;
        Getter getter = CACHE.get(cacheKey);
        if (getter != null) {
            return getter;
        }
        TableInfo tableInfo = Tables.get(clazz);
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(fieldName);
        if (tableFieldInfo == null) {
            return null;
        }
        return CACHE.computeIfAbsent(cacheKey, key -> LambdaUtil.createGetterByField(clazz, fieldName, tableFieldInfo.getField().getType()));
    }
}
