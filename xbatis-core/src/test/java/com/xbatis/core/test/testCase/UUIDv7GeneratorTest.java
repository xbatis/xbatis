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

import cn.xbatis.core.incrementer.UUIDv7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 针对之前提供的UUIDv7Generator的测试
 */
@DisplayName("UUIDv7Generator 测试")
class UUIDv7GeneratorTest extends UUIDv7BaseTest {

    // 实现抽象方法
    @Override
    protected String generateUUIDv7() {
        return generateUUIDv7Object().toString();
    }

    @Override
    protected UUID generateUUIDv7Object() {
        return UUIDv7.next();
    }

    // ============== 额外特性测试 ==============


    // ============== 并发测试 ==============

    @Test
    @DisplayName("多线程并发测试")
    void testConcurrentGeneration() throws InterruptedException {
        final int threadCount = 10;
        final int perThread = 1000;
        final Set<String> allUuids = java.util.Collections.synchronizedSet(
                new HashSet<>(threadCount * perThread)
        );

        Thread[] threads = new Thread[threadCount];

        // 创建线程
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    String uuid = generateUUIDv7();
                    allUuids.add(uuid);
                }
            });
        }

        // 启动所有线程
        for (Thread t : threads) t.start();

        // 等待所有线程完成
        for (Thread t : threads) t.join();

        // 验证唯一性
        assertEquals(threadCount * perThread, allUuids.size(),
                "并发环境下应保证唯一性");

        // 验证格式
        for (String uuid : allUuids) {
            assertEquals('7', uuid.charAt(14), "并发生成的UUID版本位正确");
        }
    }

    // ============== 嵌套测试类 ==============

    @Nested
    @DisplayName("边缘情况测试")
    class EdgeCasesTest {

        @Test
        @DisplayName("连续高速生成测试")
        void testHighFrequencyGeneration() {
            final int count = 1_000_000;
            Set<String> set = new HashSet<>(count);

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                String uuid = generateUUIDv7();
                if (!set.add(uuid)) {
                    fail("发现重复UUID: " + uuid + " 在第" + i + "次生成");
                }
            }
            long duration = System.currentTimeMillis() - startTime;

            System.out.printf("生成 %,d 个UUID用时: %,d ms%n", count, duration);
            assertTrue(duration < 10_000, "生成速度应合理");
        }

        @Test
        @DisplayName("跨毫秒边界测试")
        void testCrossMillisecondBoundary() throws InterruptedException {
            List<Long> timestamps = new ArrayList<>();

            // 捕捉跨毫秒时刻
            long start = System.currentTimeMillis();
            while (timestamps.size() < 10) {
                UUID uuid = generateUUIDv7Object();
                long ts = extractTimestamp(uuid);

                if (timestamps.isEmpty() || ts != timestamps.get(timestamps.size() - 1)) {
                    timestamps.add(ts);
                }

                if (System.currentTimeMillis() - start > 1000) {
                    break; // 超时保护
                }
            }

            // 验证时间戳递增
            for (int i = 1; i < timestamps.size(); i++) {
                assertTrue(timestamps.get(i) > timestamps.get(i - 1),
                        "时间戳应跨毫秒递增: " + timestamps.get(i - 1) + " -> " + timestamps.get(i));
            }
        }
    }

    @Nested
    @DisplayName("功能性测试")
    class FunctionalTests {

        @Test
        @DisplayName("可排序性验证")
        void testSortability() {
            List<String> uuids = new ArrayList<>();
            List<UUID> uuidObjects = new ArrayList<>();

            // 生成100个带延迟的UUID
            for (int i = 0; i < 100; i++) {
                String str = generateUUIDv7();
                UUID obj = generateUUIDv7Object();
                uuids.add(str);
                uuidObjects.add(obj);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }

            // 字符串排序应与对象排序一致
            List<String> sortedStrings = new ArrayList<>(uuids);
            Collections.sort(sortedStrings);

            List<UUID> sortedObjects = new ArrayList<>(uuidObjects);
            Collections.sort(sortedObjects);

            // 验证排序一致性
            for (int i = 0; i < uuids.size(); i++) {
                int stringIndex = sortedStrings.indexOf(uuids.get(i));
                int objectIndex = sortedObjects.indexOf(uuidObjects.get(i));
                assertEquals(stringIndex, objectIndex,
                        "字符串排序与对象排序位置应一致");
            }
        }

        @Test
        @DisplayName("数据库友好性测试")
        void testDatabaseFriendliness() {
            // 验证可作为数据库主键的特性
            List<String> uuids = new ArrayList<>();

            for (int i = 0; i < 1000; i++) {
                String uuid = generateUUIDv7();
                uuids.add(uuid);

                // 验证无SQL注入风险字符
                assertFalse(uuid.contains("'"), "不应包含单引号");
                assertFalse(uuid.contains("\""), "不应包含双引号");
                assertFalse(uuid.contains(";"), "不应包含分号");

                // 验证适合URL编码
                try {
                    java.net.URLEncoder.encode(uuid, "UTF-8");
                } catch (Exception e) {
                    fail("UUID应能URL编码: " + uuid);
                }
            }

            // 排序后插入顺序验证
            List<String> sorted = new ArrayList<>(uuids);
            Collections.sort(sorted);

            // UUIDv7排序后应与时间顺序大致一致
            int correctOrderCount = 0;
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i).compareTo(sorted.get(i - 1)) > 0) {
                    correctOrderCount++;
                }
            }

            double orderPercentage = (double) correctOrderCount / (sorted.size() - 1);
            assertTrue(orderPercentage > 0.99,
                    "UUIDv7应有超过99%的时间有序性: " + (orderPercentage * 100) + "%");
        }
    }
}
