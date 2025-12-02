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

package com.xbatis.core.test.fetch;

import cn.xbatis.core.cache.FetchCache;
import cn.xbatis.core.db.reflect.FieldInfo;
import cn.xbatis.db.annotations.Fetch;
import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FetchCacheTestImpl implements FetchCache {

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    @Override
    public Object get(String cacheName, Fetch fetch, FieldInfo fieldInfo, String cacheKey) {

        if ("FetchSysRoleCacheVo.sysRoleNames".equals(cacheName) && (cacheKey.equals("2"))) {
            return Arrays.asList("写死的缓存ID2");
        }

        String value = CACHE.get(cacheName + cacheKey);
        System.out.println("获取fetch cache value: " + cacheName + ":" + cacheKey + " <<<<<<=== " + value);
        if (value == null) {
            return null;
        }
        if ("NULL".equals(value)) {
            return NULL;
        }

        Object value2 = fieldInfo.isCollection() ? JSON.parseArray(value, fieldInfo.getFinalClass()) : JSON.parseObject(value, fieldInfo.getFinalClass());
        if ("FetchSysRoleCacheVo.sysRoleNames".equals(cacheName) && value2 instanceof List) {
            value2 = ((List) value2).stream().map(i -> "123444").collect(Collectors.toList());
        }
        return value2;
    }

    @Override
    public void set(String cacheName, Fetch fetch, FieldInfo fieldInfo, String cacheKey, Object result) {
        System.out.println("设置fetch cache value: " + cacheName + ":" + cacheKey + "===>" + result);
        if (Objects.isNull(result)) {
            result = "NULL";
        }
        CACHE.put(cacheName + cacheKey, JSON.toJSONString(result));
    }
}
