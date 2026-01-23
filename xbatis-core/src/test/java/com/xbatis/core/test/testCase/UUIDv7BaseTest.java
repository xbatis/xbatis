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

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv7生成器测试基类
 * 所有实现都应通过这些测试
 */
public abstract class UUIDv7BaseTest {

    // UUIDv7正则表达式 (RFC 9562格式)
    private static final Pattern UUIDv7_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    // 获取待测试的生成器实现
    protected abstract String generateUUIDv7();

    protected abstract UUID generateUUIDv7Object();

    // 工具方法：从UUIDv7提取时间戳
    protected long extractTimestamp(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        return (msb >>> 16) & 0xFFFFFFFFFFFFL; // 48位时间戳
    }

    // ============== 基础格式测试 ==============

    @Test
    void testFormatCompliance() {
        String uuid = generateUUIDv7();

        assertNotNull(uuid, "UUID不应为null");
        assertEquals(36, uuid.length(), "UUID长度应为36字符");
        assertEquals('-', uuid.charAt(8), "第9字符应为'-'");
        assertEquals('-', uuid.charAt(13), "第14字符应为'-'");
        assertEquals('-', uuid.charAt(18), "第19字符应为'-'");
        assertEquals('-', uuid.charAt(23), "第24字符应为'-'");
    }

    @Test
    void testVersionField() {
        String uuid = generateUUIDv7();
        // 版本位在第15个字符（索引14），应为'7'
        assertEquals('7', uuid.charAt(14), "版本位必须为'7'");

        // 验证版本位二进制值
        UUID uuidObj = generateUUIDv7Object();
        int version = (int) ((uuidObj.getMostSignificantBits() >>> 12) & 0x0F);
        assertEquals(7, version, "版本号必须为7");
    }

    @Test
    void testVariantField() {
        String uuid = generateUUIDv7();
        // 变体位在第20个字符（索引19），应为8、9、A或B
        char variantChar = uuid.charAt(19);
        assertTrue(variantChar == '8' || variantChar == '9' ||
                        variantChar == 'a' || variantChar == 'b' ||
                        variantChar == 'A' || variantChar == 'B',
                "变体位必须为8、9、A或B");

        // 验证变体位二进制值（10xxxxxx）
        UUID uuidObj = generateUUIDv7Object();
        long variantBits = uuidObj.getLeastSignificantBits() >>> 62;
        assertEquals(2, variantBits, "变体位必须为2（RFC 9562要求）");
    }

    @Test
    void testRegexPattern() {
        for (int i = 0; i < 100; i++) {
            String uuid = generateUUIDv7();
            assertTrue(UUIDv7_PATTERN.matcher(uuid).matches(),
                    "UUID必须匹配RFC 9562格式: " + uuid);
        }
    }

    // ============== 时间有序性测试 ==============

    @Test
    void testTimeOrdering() {
        final int count = 1000;
        String[] uuids = new String[count];

        // 生成一批UUID
        for (int i = 0; i < count; i++) {
            uuids[i] = generateUUIDv7();
            // 微调时间，确保不同时间戳
            try {
                Thread.sleep(0, 1000);
            } catch (InterruptedException e) {
            }
        }

        // 验证字符串排序与生成顺序一致（时间有序）
        String[] sorted = uuids.clone();
        java.util.Arrays.sort(sorted);

        assertArrayEquals(uuids, sorted,
                "UUIDv7应保持时间有序性（字符串排序与生成顺序一致）");
    }

    @Test
    void testTimestampMonotonicity() {
        final int count = 100;
        long lastTimestamp = 0;

        for (int i = 0; i < count; i++) {
            UUID uuid = generateUUIDv7Object();
            long timestamp = extractTimestamp(uuid);

            if (i > 0) {
                assertTrue(timestamp >= lastTimestamp,
                        "时间戳必须单调递增: " + lastTimestamp + " -> " + timestamp);
            }

            lastTimestamp = timestamp;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    @Test
    void testTimestampAccuracy() {
        long before = System.currentTimeMillis();
        UUID uuid = generateUUIDv7Object();
        long after = System.currentTimeMillis();

        long uuidTimestamp = extractTimestamp(uuid);

        assertTrue(uuidTimestamp >= before && uuidTimestamp <= after,
                "UUID时间戳应在生成时间范围内: " + before + " ≤ " +
                        uuidTimestamp + " ≤ " + after);

        // 允许2ms的时钟偏差
        long diff = Math.abs(uuidTimestamp - before);
        assertTrue(diff < 100, "时间戳偏差不应超过100ms: " + diff + "ms");
    }

    // ============== 唯一性测试 ==============

    @Test
    void testUniqueness() {
        final int count = 100_000;
        Set<String> uuidSet = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            String uuid = generateUUIDv7();
            assertTrue(uuidSet.add(uuid),
                    "发现重复UUID: " + uuid + " 第" + i + "次生成");
        }

        assertEquals(count, uuidSet.size(), "应生成" + count + "个唯一UUID");
    }

    // ============== 性能测试 ==============

    @Test
    void testGenerationSpeed() {
        final int warmup = 10_000;
        final int iterations = 100_000;

        // 预热
        for (int i = 0; i < warmup; i++) {
            generateUUIDv7();
        }

        // 性能测试
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            generateUUIDv7();
        }
        long endTime = System.nanoTime();

        long totalNanos = endTime - startTime;
        double nanosPerUUID = (double) totalNanos / iterations;
        double uuidsPerSecond = 1_000_000_000.0 / nanosPerUUID;

        System.out.printf("性能: %.0f UUID/秒 | %.2f 纳秒/UUID%n",
                uuidsPerSecond, nanosPerUUID);

        // 期望性能：至少10万UUID/秒
        assertTrue(uuidsPerSecond > 100_000,
                "生成速度应超过100,000 UUID/秒，实际: " + uuidsPerSecond);
    }

    // ============== 兼容性测试 ==============

    @Test
    void testJavaUUIDCompatibility() {
        for (int i = 0; i < 100; i++) {
            UUID uuid = generateUUIDv7Object();

            // 验证java.util.UUID方法正常工作
            String str = uuid.toString();
            UUID parsed = UUID.fromString(str);

            assertEquals(uuid, parsed, "toString/fromString应可逆");
            assertEquals(uuid.hashCode(), parsed.hashCode(), "hashCode应一致");
            assertEquals(uuid.variant(), parsed.variant(), "变体位应一致");
            assertEquals(uuid.version(), parsed.version(), "版本位应一致");
        }
    }

    @Test
    void testStringParsing() {
        String uuidStr = generateUUIDv7();

        // 应能正确解析为java.util.UUID
        UUID parsed = assertDoesNotThrow(
                () -> UUID.fromString(uuidStr),
                "UUID字符串应能被java.util.UUID解析: " + uuidStr
        );

        assertEquals(uuidStr.toLowerCase(), parsed.toString().toLowerCase(),
                "解析后字符串应相同（忽略大小写）");
    }
}
