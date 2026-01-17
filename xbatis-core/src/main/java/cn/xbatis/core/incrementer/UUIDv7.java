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

package cn.xbatis.core.incrementer;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDv7 {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(0);
    private static final AtomicLong COUNTER = new AtomicLong(0);

    public static UUID next() {
        long timestamp = System.currentTimeMillis();
        long lastTime = LAST_TIMESTAMP.get();

        // 处理同一毫秒内的并发
        if (timestamp == lastTime) {
            COUNTER.incrementAndGet();
        } else {
            COUNTER.set(0);
            LAST_TIMESTAMP.set(timestamp);
        }

        // 构造UUIDv7
        long msb = ((timestamp & 0xFFFFFFFFFFFFL) << 16) |
                (0x7000L) |
                (RANDOM.nextInt(0x1000) & 0x0FFFL);

        long lsb = RANDOM.nextLong();
        lsb = (lsb & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;

        // 如果同一毫秒内，调整低位
        if (timestamp == lastTime) {
            lsb ^= COUNTER.get(); // 简单混合计数器
        }

        return new UUID(msb, lsb);
    }

    public static void main(String[] args) {
        String uuid = next().toString();
        System.out.println(uuid);
        System.out.println(uuid.charAt(14) == '7');
    }
}
