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

import cn.xbatis.core.mybatis.typeHandler.EnumSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumUtil {

    private static final Map<Class, Map<Object, Enum>> ENUM_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据code 获取枚举
     *
     * @param enumClass 枚举类
     * @param code      枚举类的code
     * @param <T>       枚举类型
     * @param <E>       枚举类的code的类型
     * @return
     */
    public static <T extends Enum<T> & EnumSupport<E>, E> T get(Class<T> enumClass, E code) {
        if (code == null) {
            return null;
        }
        return (T) ENUM_CACHE.computeIfAbsent(enumClass, k -> {
            Object[] values = k.getEnumConstants();
            Map<Object, Enum> valueMap = new HashMap<>();
            for (Object value : values) {
                T valueEnum = (T) value;
                valueMap.put(valueEnum.getCode(), valueEnum);
            }
            return valueMap;
        }).get(code);
    }
}
