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
package db.sql.api.tookit;


import db.sql.api.Getter;
import db.sql.api.GetterFun;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LambdaUtil {

    private static final Map<GetterFun, LambdaFieldInfo> LAMBDA_GETTER_FIELD_MAP = new ConcurrentHashMap<>(65535);
    private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap();

    private LambdaUtil() {

    }

    public static <T> Set<String> getFieldNames(Getter<T>[] fieldGetters) {
        Set<String> fieldsSet = null;
        if (Objects.nonNull(fieldGetters) && fieldGetters.length > 0) {
            fieldsSet = new HashSet<>(fieldGetters.length);
            for (Getter<?> column : fieldGetters) {
                fieldsSet.add(LambdaUtil.getName(column));
            }
        }
        return fieldsSet;
    }

    public static <T, R> String getName(GetterFun<T, R> getter) {
        return getFieldInfo(getter).getName();
    }

    private static <T> LambdaFieldInfo<T> getLambdaFieldInfo(SerializedLambda serializedLambda, ClassLoader classLoader) {
        Class type = getClass(serializedLambda, classLoader);

        String fieldName = null;
        // 兼容 Kotlin KProperty 的 Lambda 解析
        if (serializedLambda.getCapturedArgCount() == 1) {
            Object capturedArg = serializedLambda.getCapturedArg(0);
            try {
                fieldName = (String) capturedArg.getClass()
                        .getMethod("getName")
                        .invoke(capturedArg);
            } catch (Exception e) {
                // 忽略这个异常，使用java方式获取方法名
            }
        }

        if (fieldName == null) {
            String methodName = serializedLambda.getImplMethodName();
            fieldName = PropertyNamer.methodToProperty(methodName);
        }
        return new LambdaFieldInfo(type, fieldName);
    }

    public static <T, R> LambdaFieldInfo<T> getFieldInfo(GetterFun<T, R> getter) {
        return LAMBDA_GETTER_FIELD_MAP.computeIfAbsent(getter, (key) -> getLambdaFieldInfo(getSerializedLambda(getter), Thread.currentThread().getContextClassLoader()));
    }

    public static <T, R> SerializedLambda getSerializedLambda(GetterFun<T, R> getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Class<T> getClass(SerializedLambda lambda, ClassLoader classLoader) {
        String classNamePath = getClassNamePath(lambda);
        return (Class<T>) CLASS_MAP.computeIfAbsent(classNamePath, key -> {
            String className = getClassName(key);
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                try {
                    return Class.forName(className, false, LambdaUtil.class.getClassLoader());
                } catch (ClassNotFoundException ex) {
                    try {
                        return Class.forName(className, false, classLoader);
                    } catch (ClassNotFoundException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }
        });
    }

    private static String getClassName(String classNamePath) {
        return classNamePath.replace("/", ".");
    }

    private static String getClassNamePath(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        return type.substring(2, type.indexOf(";"));
    }

    public static class LambdaFieldInfo<T> {

        private final Class<T> type;
        private final String name;

        public LambdaFieldInfo(Class<T> type, String name) {
            this.type = type;
            this.name = name;
        }

        public Class<T> getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
