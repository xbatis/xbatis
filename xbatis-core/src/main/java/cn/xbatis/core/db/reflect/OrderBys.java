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

import cn.xbatis.db.annotations.OrderByTarget;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderBys{

    private static final Map<Class, OrderByInfo> CACHE = new ConcurrentHashMap<>();

    private OrderBys() {

    }

    /**
     * 获取类的条件的信息
     *
     * @param clazz
     * @return
     */
    public static OrderByInfo get(Class clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz);
        }
        if (!clazz.isAnnotationPresent(OrderByTarget.class)) {
            throw new RuntimeException("class " + clazz.getName() + " is not annotated with @OrderByTarget");
        }
        return CACHE.computeIfAbsent(clazz, key -> new OrderByInfo(clazz));
    }
}
