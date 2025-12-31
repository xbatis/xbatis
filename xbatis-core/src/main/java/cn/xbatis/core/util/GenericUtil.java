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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericUtil {

    public static List<Class<?>> get(Class clazz) {
        List<Class<?>> list = getGenericInterfaceClass(clazz);
        list.addAll(getGenericSuperClass(clazz));
        return list;
    }

    /**
     * 获取通过继承的泛型类
     *
     * @param clazz
     * @return 获取继承的所有泛型
     */
    public static List<Class<?>> getGenericSuperClass(Class clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            List<Class<?>> classList = new ArrayList<>(actualTypeArguments.length);
            for (Type actualTypeArgument : actualTypeArguments) {
                if (!(actualTypeArgument instanceof Class)) {
                    continue;
                }
                classList.add((Class) actualTypeArgument);
            }
            return classList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取通过接口实现的泛型
     *
     * @param clazz 类
     * @return 获取通过接口实现的泛型
     */
    public static List<Class<?>> getGenericInterfaceClass(Class clazz) {
        Type[] types = clazz.getGenericInterfaces();
        return getGenericTypes(types);
    }

    public static List<Class<?>> getGenericTypes(Type[] types) {
        List<Class<?>> classList = new ArrayList<>(types.length * 2);
        for (Type type : types) {
            classList = getGeneric(type, classList);
        }
        return classList;
    }

    public static List<Class<?>> getGeneric(Type type) {
        return getGeneric(type, new ArrayList<>(1));
    }

    public static List<Class<?>> getGeneric(Type type, List<Class<?>> resultList) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                if (!(actualTypeArgument instanceof Class)) {
                    continue;
                }
                resultList.add((Class) actualTypeArgument);
            }
        }
        return resultList;
    }

    public static List<Class<?>> getGenericParameterTypes(Method method) {
        return getGenericTypes(method.getGenericParameterTypes());
    }
}
