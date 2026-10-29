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
package db.sql.api.tookit;


import db.sql.api.Getter;
import db.sql.api.GetterFun;
import db.sql.api.Setter;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class LambdaUtil {

    private static final ClassValue<LambdaFieldInfo<?>> LAMBDA_GETTER_FIELD_MAP = new ClassValue<LambdaFieldInfo<?>>() {
        @Override
        protected LambdaFieldInfo<?> computeValue(Class<?> type) {
            return new LambdaFieldInfo<>(null, null);
        }
    };

    //实体类映射
    private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>(64);

    private final static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

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

    public static <T, R> LambdaFieldInfo<T> getFieldInfo(GetterFun<T, R> getter) {
        LambdaFieldInfo lambdaFieldInfo = LAMBDA_GETTER_FIELD_MAP.get(getter.getClass());
        if (lambdaFieldInfo.isPresent()) {
            return lambdaFieldInfo;
        }
        SerializedLambda serializedLambda = getSerializedLambda(getter);
        Class type = getClass(serializedLambda, getter);
        String fieldName = getFieldName(serializedLambda);
        lambdaFieldInfo.set(type, fieldName);
        return lambdaFieldInfo;
    }

    private static String getFieldName(SerializedLambda serializedLambda) {
        // 兼容 Kotlin KProperty 的 Lambda 解析
        if (serializedLambda.getCapturedArgCount() == 1) {
            Object capturedArg = serializedLambda.getCapturedArg(0);
            if (capturedArg != null) {
                try {
                    Class type = capturedArg.getClass();
                    Method method = type.getMethod("getName");
                    method.setAccessible(Boolean.TRUE);
                    return (String) method.invoke(capturedArg);
                } catch (Exception ignored) {
                    // 回退到 Java getter 方法名解析
                }
            }
        }
        return PropertyNamer.methodToProperty(serializedLambda.getImplMethodName());
    }

    public static <T, R> SerializedLambda getSerializedLambda(GetterFun<T, R> getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to resolve lambda: " + getter, e);
        }
    }

    private static <T> Class<T> getClass(SerializedLambda lambda, GetterFun<?, ?> getterFun) {
        String classNamePath = getClassNamePath(lambda);
        return (Class<T>) CLASS_MAP.computeIfAbsent(classNamePath, key -> {
            String className = getClassName(key);
            try {
                return loadClass(className, getterFun);
            } catch (IllegalArgumentException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    return loadClass(className, getterFun);
                } catch (IllegalArgumentException ex) {
                    throw ex;
                }
            }
        });
    }


    private static Class<?> loadClass(String className, GetterFun<?, ?> getterFun) throws IllegalArgumentException {
        try {
            return Class.forName(className, false, getterFun.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            //忽略
        }
        try {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            //忽略
        }
        try {
            return Class.forName(className, false, LambdaUtil.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            //忽略
            throw new IllegalArgumentException("Unable to load lambda owner class: " + className, e);
        }
    }

    private static String getClassName(String classNamePath) {
        return classNamePath.replace("/", ".");
    }

    private static String getClassNamePath(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        int start = type.indexOf('L');
        int end = type.indexOf(';', start);
        if (start < 0 || end < 0 || end == start + 1) {
            throw new IllegalArgumentException("Invalid lambda method type: " + type);
        }
        return type.substring(start + 1, end);
    }

    public static <T, R> Getter<T> createGetterByField(Class<T> clazz, String field, Class<R> rType) {
        if (boolean.class.equals(rType)) {
            try {
                return createGetter(Getter.class, clazz, "is" + PropertyNamer.firstToUpperCase(field), rType);
            } catch (RuntimeException ignored) {

            }
        }
        return createGetter(Getter.class, clazz, "get" + PropertyNamer.firstToUpperCase(field), rType);
    }

    public static <T, R> Getter<T> createGetter(Class<T> clazz, String getterName, Class<R> rType) {
        return createGetter(Getter.class, clazz, getterName, rType);
    }

    public static <GETTER extends Function<T, R>, T, R> GETTER createGetter(Class<GETTER> getterClass, Class<T> clazz, String getterName, Class<R> rType) {
        try {
            CallSite callSite = LambdaMetafactory.altMetafactory(
                    LOOKUP,
                    "apply",
                    MethodType.methodType(getterClass),
                    MethodType.methodType(Object.class, Object.class),
                    LOOKUP.findVirtual(clazz, getterName, MethodType.methodType(rType)),
                    MethodType.methodType(rType, clazz),
                    LambdaMetafactory.FLAG_SERIALIZABLE  // 可选标志
            );

            MethodHandle factory = callSite.getTarget();
            return (GETTER) factory.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, V> Setter<T, V> createSetterByField(Class<T> clazz, String field, Class<V> vType) {
        return createSetter(clazz, "set" + PropertyNamer.firstToUpperCase(field), vType);
    }

    public static <T, V> Setter<T, V> createSetter(Class<T> clazz, String setterName, Class<V> vType) {
        return createSetter(Setter.class, clazz, setterName, vType);
    }

    public static <SETTER extends BiConsumer<T, V>, T, V> SETTER createSetter(Class<SETTER> setterClass, Class<T> clazz, String setterName, Class<V> vType) {
        try {
            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "accept",
                    MethodType.methodType(setterClass),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    LOOKUP.findVirtual(clazz, setterName, MethodType.methodType(void.class, vType)),
                    MethodType.methodType(void.class, clazz, vType)
            );

            MethodHandle factory = callSite.getTarget();
            return (SETTER) factory.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static class LambdaFieldInfo<T> {

        private volatile Class<T> type;
        private volatile String name;

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

        protected void set(Class<T> type, String name) {
            this.type = type;
            this.name = name;
        }

        protected boolean isPresent() {
            return this.type != null && this.name != null;
        }
    }

}
