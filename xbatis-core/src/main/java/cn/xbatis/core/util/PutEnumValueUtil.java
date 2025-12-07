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

import cn.xbatis.core.db.reflect.PutEnumValueInfo;
import cn.xbatis.db.annotations.PutEnumValue;
import db.sql.api.impl.tookit.Objects;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PutEnumValueUtil {

    private static final Map<String, Object> ENUM_VALUE_CACHE = new ConcurrentHashMap<>();

    private static final Object NULL_VALUE = new Object();

    public static Object getEnumValue(Object code, PutEnumValueInfo putEnumValueInfo) {

        Object value = ENUM_VALUE_CACHE.computeIfAbsent(putEnumValueInfo.getAnnotation().target().getName() + "." + code, key -> {
            PutEnumValue annotation = putEnumValueInfo.getAnnotation();
            if (!putEnumValueInfo.getAnnotation().target().isEnum()) {
                throw new RuntimeException(annotation.target().getName() + " is not an enum");
            }

            if (Objects.isNull(code)) {
                if (annotation.required()) {
                    throw new RuntimeException("code value is not null from table");
                }
                return NULL_VALUE;
            }

            Object[] enums = annotation.target().getEnumConstants();
            Field codeField;
            Field valueField;
            try {
                codeField = annotation.target().getDeclaredField(annotation.code());
                valueField = annotation.target().getDeclaredField(annotation.value());

                codeField.setAccessible(true);
                valueField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(annotation.target().getName() + " has no " + e.getMessage() + " field");
            }
            String codeString = code.toString();
            try {
                for (Object e : enums) {
                    if (codeString.equals(codeField.get(e).toString())) {
                        return TypeConvertUtil.convert(valueField.get(e), putEnumValueInfo.getFieldInfo().getTypeClass());
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

            return NULL_VALUE;
        });

        if (value == NULL_VALUE) {
            if (putEnumValueInfo.getAnnotation().required()) {
                throw new RuntimeException("code value is not match with " + putEnumValueInfo.getAnnotation().target().getName());
            }
            value = putEnumValueInfo.getDefaultValue();
        }

        return value;
    }
}
