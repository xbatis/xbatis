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

package cn.xbatis.core.cache;

import cn.xbatis.core.db.reflect.FieldInfo;
import cn.xbatis.db.annotations.Fetch;

public interface FetchCache {

    /**
     * 空值结果
     */
    NULL NULL = new NULL();

    /**
     * 获取缓存值
     *
     * @param cacheName 缓存的名字
     * @param fetch     注解信息
     * @param fieldInfo 字段信息
     * @param cacheKey  缓存的key
     * @return 缓存中的结果
     */
    Object get(String cacheName, Fetch fetch, FieldInfo fieldInfo, String cacheKey);

    /**
     * 设置缓存
     *
     * @param cacheName 缓存的名字
     * @param fetch     注解信息
     * @param fieldInfo 字段信息
     * @param cacheKey  缓存的key
     * @param result    查询结果
     */
    void set(String cacheName, Fetch fetch, FieldInfo fieldInfo, String cacheKey, Object result);
}
