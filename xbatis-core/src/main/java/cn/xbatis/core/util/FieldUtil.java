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

import cn.xbatis.db.annotations.Ignore;
import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FieldUtil {

    public static boolean isResultMappingField(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }

        if (Modifier.isFinal(field.getModifiers())) {
            return false;
        }
        return !field.isAnnotationPresent(Ignore.class);
    }

    /**
     * 是否为ignore字段；只针对非静态 非final字段
     *
     * @param field
     * @return
     */
    private static boolean isIgnoreField(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }

        if (Modifier.isFinal(field.getModifiers())) {
            return false;
        }
        return field.isAnnotationPresent(Ignore.class);
    }

    public static List<Field> getResultMappingFields(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        Set<String> ignoreFieldNameSet = new HashSet<>();
        Class parseClass = clazz;
        while (parseClass != null) {
            Field[] fields = parseClass.getDeclaredFields();
            for (Field field : fields) {
                if (isResultMappingField(field)) {
                    if (fieldNameSet.contains(field.getName()) || ignoreFieldNameSet.contains(field.getName())) {
                        continue;
                    }
                    fieldNameSet.add(field.getName());
                    fieldList.add(field);
                } else if (isIgnoreField(field)) {
                    ignoreFieldNameSet.add(field.getName());
                }
            }
            parseClass = parseClass.getSuperclass();
        }
        return fieldList;
    }

    public static Class<?> getFieldType(Class clazz, Field field) {
        Type type = TypeParameterResolver.resolveFieldType(field, clazz);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }
        return (Class<?>) type;
    }


    /**
     * 获取字段的真正Type,假如是List<T> id，会返回 里面的 T
     *
     * @param clazz
     * @param field
     * @return
     */
    public static Class<?> getFieldFinalType(Class clazz, Field field) {
        Type type = TypeParameterResolver.resolveFieldType(field, clazz);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return (Class<?>) type;
    }

}
