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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.util.TypeConvertUtil;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class VersionUtil {

    public static final <T> T getInitValue(Class<T> type) {
        if (Objects.isNull(type)) {
            return null;
        }
        if (type == Integer.class) {
            return cast(1);
        } else if (type == Long.class) {
            return cast(1L);
        } else if (type == BigInteger.class) {
            return cast(BigInteger.ONE);
        } else if (type == String.class) {
            return cast("1");
        } else if (Date.class.isAssignableFrom(type)) {
            return cast(new Date(System.currentTimeMillis()));
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            return cast(LocalDateTime.now());
        } else {
            throw new RuntimeException("version field not support define with " + type);
        }
    }

    private static <T> T cast(Object version) {
        if (Objects.isNull(version)) {
            return null;
        }
        return (T) version;
    }

    public static final <T> T plus(T version) {
        if (Objects.isNull(version)) {
            return null;
        }
        if (version instanceof Integer) {
            return cast((Integer) version + 1);
        } else if (version instanceof String) {
            return cast("" + (Long.valueOf(version.toString()) + 1));
        } else if (version instanceof Long) {
            return cast(Long.valueOf(version.toString()) + 1);
        } else if (version instanceof Date) {
            return cast(new Date(System.currentTimeMillis()));
        } else if (version instanceof LocalDateTime) {
            return cast(LocalDateTime.now());
        }
        return cast(TypeConvertUtil.convert(new BigInteger(version.toString()).add(BigInteger.ONE), version.getClass()));
    }

    public static void main(String[] args) {
        BigInteger i = new BigInteger("1");
        for (int j = 0; j < 10; j++) {
            System.out.println(i = plus(i));
        }
    }
}
