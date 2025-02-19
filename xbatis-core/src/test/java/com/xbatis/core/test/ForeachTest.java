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

package com.xbatis.core.test;

import java.util.ArrayList;
import java.util.List;

public class ForeachTest {

    public static void main(String[] args) {
        List<String> lstStr = new ArrayList<>();
        // 生成一个存放String类型的列表，并存入三个字符串("test1","test2","test3")
        for (int i = 0; i < 1000; i++) {
            lstStr.add("test" + (i + 1));
        }

        long startTime = 0;

        startTime = System.currentTimeMillis();
        for (String s : lstStr) {
            String ss = s;
        }
        System.out.println("增强for 耗时：" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        int length = lstStr.size();
        for (int i = 0; i < length; i++) {
            String ss = lstStr.get(i);
        }
        System.out.println("普通for 耗时：" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();

        lstStr.forEach(item -> {
            String ss = item;
        });
        System.out.println("list forEach 耗时：" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();

        lstStr.stream().forEach(item -> {
            String ss = item;
        });
        System.out.println("list stream forEach 耗时：" + (System.currentTimeMillis() - startTime));
    }
}
