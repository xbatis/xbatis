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

package cn.xbatis.core.sql.util;

import java.math.BigInteger;
import java.util.Objects;

public class IdValueConverter {

    public static Object convert(Object id, Class<?> targetType) {
        if (Objects.isNull(id)) {
            return null;
        }
        if (targetType.isAssignableFrom(id.getClass())) {
            return id;
        }
        if (targetType == String.class) {
            return id.toString();
        }
        if (id instanceof Number) {
            Number idN = (Number) id;
            if (targetType == Byte.class || targetType == byte.class) {
                return idN.byteValue();
            }
            if (targetType == Integer.class || targetType == int.class) {
                return idN.intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return idN.longValue();
            }
            if (targetType == BigInteger.class) {
                return new BigInteger(idN.toString());
            }
        }

        return id;
    }
}
