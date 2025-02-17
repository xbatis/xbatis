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

package cn.xbatis.core.mybatis;

import java.lang.reflect.Method;

public class MethodUtil {

    public static Method getMethod(Class<?> clazz, String methodName) {
        Method method = getMethod(clazz.getMethods(), methodName);
        if (method == null) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                method = getMethod(superClass, methodName);
                if (method != null) {
                    return method;
                }
            }
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                for (Class c : clazz.getInterfaces()) {
                    method = getMethod(c.getMethods(), methodName);
                    if (method != null) {
                        break;
                    }
                }
            }
        }
        return method;
    }

    private static Method getMethod(Method[] methods, String methodName) {
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }
}
