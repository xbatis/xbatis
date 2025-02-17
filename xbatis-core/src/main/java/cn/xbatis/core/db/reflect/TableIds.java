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

import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class TableIds {

    private static final Map<String, TableId> CACHE = new ConcurrentHashMap<>();

    private TableIds() {

    }

    public static TableId get(Class entity, DbType dbType) {
        return CACHE.computeIfAbsent(entity.getName() + dbType, key -> {

            TableFieldInfo tableFieldInfo = Tables.get(entity).getSingleIdFieldInfo(false);
            if (Objects.isNull(tableFieldInfo)) {
                return null;
            }
            return TableInfoUtil.getTableIdAnnotation(tableFieldInfo.getField(), dbType);
        });
    }

}
