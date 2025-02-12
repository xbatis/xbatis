/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.util;

import cn.mybatis.mp.core.mybatis.typeHandler.EnumSupport;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 默认值转换
 */
public final class TypeConvertUtil {

    private TypeConvertUtil() {
    }

    /**
     * 默认值转换
     *
     * @param value
     * @param targetType
     * @param <T>
     * @return
     */
    public static <T> T convert(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (value.getClass() == targetType) {
            return (T) value;
        }
        if (value instanceof String && value.equals("")) {
            return null;
        }

        if (targetType.isEnum()) {
            if (EnumSupport.class.isAssignableFrom(targetType)) {
                for (EnumSupport e : (EnumSupport[]) targetType.getEnumConstants()) {
                    if (e.getCode().toString().equals(value.toString())) {
                        return (T) e;
                    }
                }
            } else {
                for (Enum<?> e : (Enum<?>[]) targetType.getEnumConstants()) {
                    if (e.name().equals(value.toString())) {
                        return (T) e;
                    }
                }
            }
            throw new RuntimeException("Can't find default value:" + value + " from enum: " + targetType);
        }

        Object newValue;
        if (targetType == String.class) {
            newValue = value.toString();
        } else if (targetType == Boolean.class) {
            String v = value.toString().trim();
            if (v.equals("1")) {
                return (T) Boolean.TRUE;
            } else if (v.equals("0")) {
                return (T) Boolean.FALSE;
            } else if (v.equalsIgnoreCase("true")) {
                return (T) Boolean.TRUE;
            } else if (v.equalsIgnoreCase("false")) {
                return (T) Boolean.FALSE;
            }
            throw new RuntimeException("value : " + value + " can't convert to a boolean");
        } else if (targetType == Byte.class) {
            newValue = Byte.valueOf(value.toString());
        } else if (targetType == Integer.class) {
            newValue = Integer.valueOf(value.toString());
        } else if (targetType == Long.class) {
            newValue = Long.valueOf(value.toString());
        } else if (targetType == BigDecimal.class) {
            newValue = new BigDecimal(value.toString());
        } else if (targetType == BigInteger.class) {
            newValue = new BigInteger(value.toString());
        } else if (targetType == Character.class) {
            newValue = value.toString().charAt(0);
        } else {
            throw new RuntimeException("Inconsistent types value : " + value + " can't convert to a " + targetType);
        }

        return (T) newValue;
    }
}
