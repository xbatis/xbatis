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

package cn.xbatis.core;

public final class XbatisVersion {

    public static String VERSION = "1.10.12";

    public static int compare(String version) {
        String[] parts1 = VERSION.split("\\.");
        String[] parts2 = version.split("\\.");

        int len = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < len; i++) {
            // 缺失的段按 0 处理，比如 "1.0" 和 "1.0.0" 视为相等
            int num1 = i < parts1.length ? parseIntSafe(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseIntSafe(parts2[i]) : 0;

            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0; // 非法段当作 0，避免崩溃
        }
    }

    public static void main(String[] args) {
        System.out.println(compare("1.10.13"));
    }
}
