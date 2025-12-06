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

package cn.xbatis.core.util;

import cn.xbatis.core.db.reflect.PutValueInfo;
import cn.xbatis.db.annotations.PutValue;
import db.sql.api.impl.tookit.Objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public final class PutValueUtil {
    private static final Object NULL_VALUE = new Object();

    public static Object getPutValue(Object[] values, PutValueInfo putValueInfo, Map<String, Object> sessionCache) {
        String cacheKey = putValueInfo.getAnnotation().method() + "." + Arrays.toString(values) + "." + putValueInfo.getAnnotation().factory().getName();
        Object v = sessionCache.computeIfAbsent(cacheKey, key -> {
            Object value = getPutValue(values, putValueInfo);
            return value == null ? NULL_VALUE : value;
        });
        return v == NULL_VALUE ? putValueInfo.getDefaultValue() : v;
    }

    private static Object getPutValue(Object[] values, PutValueInfo putValueInfo) {
        PutValue annotation = putValueInfo.getAnnotation();
        boolean allNull = Arrays.stream(values).noneMatch(java.util.Objects::nonNull);
        if (allNull) {
            if (annotation.required()) {
                throw new RuntimeException(" values are all null from table");
            }
            return null;
        }

        Class<?>[] paramTypes = Arrays.stream(values).map(v -> v == null ? Object.class : v.getClass()).toArray(Class[]::new);
        try {
            Method putValueMethod = annotation.factory().getMethod(annotation.method(), paramTypes);
            putValueMethod.setAccessible(true);
            Object value = putValueMethod.invoke(null, values);
            if (Objects.isNull(value)) {
                if (annotation.required()) {
                    throw new RuntimeException(" value is  null from " + annotation.factory() + " " + annotation.method());
                } else {
                    return putValueInfo.getDefaultValue();
                }
            }
            return TypeConvertUtil.convert(value, putValueInfo.getFieldInfo().getTypeClass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
