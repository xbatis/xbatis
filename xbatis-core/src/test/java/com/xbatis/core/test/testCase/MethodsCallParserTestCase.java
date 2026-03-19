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

package com.xbatis.core.test.testCase;

import db.sql.api.tookit.MethodsCallParser;
import org.junit.jupiter.api.Test;

public class MethodsCallParserTestCase extends BaseTest {

    @Test
    public void testMethodsCallParser() {
        MethodsCallParser parser = new MethodsCallParser();

        // 测试用例
        String[] testCases = {
                "sum(ifNull(id,NULL()))",
                "if(eq(id,NULL()),1,2)"
        };

        System.out.println("========== 基本测试 ==========");
        for (String test : testCases) {
            try {
                Object result = parser.parse(test);
                System.out.printf("%-40s -> %s%n", test, result);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("%-40s -> 错误: %s%n", test, e.getMessage());
            }
        }
    }
}
