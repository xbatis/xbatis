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

package cn.xbatis.core.db.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtil {

    public static Map<Class<? extends Annotation>, Annotation> getAnnotations(Class clazz, Field field, Class<?> fieldType, Class<? extends Annotation>... annotationTypes) {
        String setterName = "set" + Character.toUpperCase(field.getName().charAt(0)) + (field.getName().length() > 1 ? field.getName().substring(1) : "");
        Method fieldSetter = null;
        try {
            fieldSetter = clazz.getDeclaredMethod(setterName, fieldType);
        } catch (NoSuchMethodException ignored) {
        }

        return getAnnotations(field, fieldSetter, annotationTypes);
    }

    private static Map<Class<? extends Annotation>, Annotation> getAnnotations(Field field, Method fieldSetter, Class<? extends Annotation>... annotationTypes) {
        Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            Target target = annotationType.getAnnotation(Target.class);
            if (target == null) {
                throw new RuntimeException("the " + annotationType + " has no annotation @Target config");
            }

            Annotation annotation;
            if (!Arrays.stream(target.value()).anyMatch(i -> i == ElementType.METHOD)) {
                annotation = field.getAnnotation(annotationType);
                if (annotation != null) {
                    annotationMap.put(annotationType, field.getAnnotation(annotationType));
                }
                continue;
            }

            if (fieldSetter != null) {
                if (fieldSetter.getDeclaringClass() != field.getDeclaringClass() && fieldSetter.getDeclaringClass().isAssignableFrom(field.getDeclaringClass())) {
                    annotation = field.getAnnotation(annotationType);
                    if (annotation != null) {
                        annotationMap.put(annotationType, annotation);
                        continue;
                    }
                }
                annotation = fieldSetter.getAnnotation(annotationType);
                if (annotation != null) {
                    annotationMap.put(annotationType, annotation);
                    continue;
                }
            }

            annotation = field.getAnnotation(annotationType);
            if (annotation != null) {
                annotationMap.put(annotationType, field.getAnnotation(annotationType));
            }
            continue;
        }
        return annotationMap;
    }
}
