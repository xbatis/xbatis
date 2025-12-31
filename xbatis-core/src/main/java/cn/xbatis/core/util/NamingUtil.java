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

import java.util.List;
import java.util.Objects;

/**
 * 驼峰 下划线 工具类
 */
public final class NamingUtil {

    private static boolean isBlank(String str) {
        if (Objects.isNull(str)) {
            return true;
        }
        return StringPool.EMPTY.equals(str);
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param name
     * @return 下划线格式的名称
     */
    public static String camelToUnderline(String name) {
        if (isBlank(name)) {
            return name;
        }
        int length = name.length();
        StringBuilder sb = new StringBuilder(length + length / 4);
        for (char c : name.toCharArray()) {
            char lower = Character.toLowerCase(c);
            if (lower != c && sb.length() > 0) {
                sb.append(StringPool.UNDERLINE);
            }
            sb.append(lower);
        }
        return sb.toString();
    }

    /**
     * 字符串下划线转驼峰格式
     *
     * @param name
     * @return 驼峰格式的名称
     */
    public static String underlineToCamel(String name) {
        if (isBlank(name)) {
            return name;
        }
        int length = name.length();
        StringBuilder sb = new StringBuilder(length);
        boolean matchedUnderline = false;
        for (char c : name.toCharArray()) {
            if (matchedUnderline) {
                if (c == StringPool.UNDERLINE) {
                    continue;
                }
                sb.append(Character.toUpperCase(c));
                matchedUnderline = false;
            } else {
                matchedUnderline = c == StringPool.UNDERLINE;
                if (!matchedUnderline) {
                    sb.append(c);
                }
            }

        }
        return sb.toString();
    }


    /**
     * 首字母小写
     *
     * @return 首字母小写后的名称
     */
    public static String firstToLower(String name) {
        if (isBlank(name)) {
            return name;
        }

        char first = name.charAt(0);
        if (Character.isLowerCase(first)) {
            return name;
        }

        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(first);
        return new String(chars);
    }

    /**
     * 首字母大写
     *
     * @return 首字母大写后的名称
     */
    public static String firstToUpperCase(String name) {
        if (isBlank(name)) {
            return name;
        }

        char first = name.charAt(0);
        if (Character.isUpperCase(first)) {
            return name;
        }

        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(first);
        return new String(chars);
    }

    /**
     * 移除前缀
     *
     * @param original 源字符串
     * @param prefixes 前缀列表
     * @return 移除前缀后的结果
     */
    public static String removePrefix(String original, List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return original;
        }
        for (String prefix : prefixes) {
            String target = removePrefix(original, prefix);
            if (!Objects.equals(target, original)) {
                return target;
            }
        }
        return original;
    }

    /**
     * 移除前缀
     *
     * @param original 源字符串
     * @param prefix   前缀列表
     * @return 移除前缀后的结果
     */
    public static String removePrefix(String original, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return original;
        }
        if (original.startsWith(prefix)) {
            return original.substring(prefix.length());
        }
        return original;
    }

    public static void main(String[] args) {
        System.out.println(camelToUnderline("UserTable"));
        System.out.println(camelToUnderline("user_table"));
        System.out.println(camelToUnderline("User_table"));

        System.out.println(underlineToCamel("UserTable"));
        System.out.println(underlineToCamel("user_table"));
        System.out.println(underlineToCamel("User_table"));
    }
}
