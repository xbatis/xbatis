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

package db.sql.api.impl.tookit;

import db.sql.api.Getter;
import db.sql.api.tookit.LambdaUtil;

import java.lang.reflect.Field;

public final class SqlUtil {

    public static final String AS_SPLIT = "$$";

    public static <T> String getAsName(Getter<T> getter) {
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(getter);
        return lambdaFieldInfo.getType().getSimpleName() + AS_SPLIT + lambdaFieldInfo.getName();
    }

    public static String getAsName(Class clazz, Field field) {
        return getAsName(clazz, field.getName());
    }

    public static String getAsName(Class clazz, String fieldName) {
        return clazz.getSimpleName() + AS_SPLIT + fieldName;
    }

    public static boolean isAsName(Class clazz, Field field, String name) {
        if (!name.contains(AS_SPLIT)) {
            return false;
        }
        return getAsName(clazz, field).equals(name);
    }
}
