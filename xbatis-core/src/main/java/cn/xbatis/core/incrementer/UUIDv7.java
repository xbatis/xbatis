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
    private static final AtomicLong STATE = new AtomicLong(0); // 高48位:ms时间戳 低16位:计数器
    private static final SecureRandom RANDOM = new SecureRandom();

    public static UUID next() {
        long ts = System.currentTimeMillis(), state, counter;
        long now = ts << 16;
        while (true) {
            state = STATE.get();
            long packed = Math.max(state & ~0xFFFFL, now); // 时钟回拨时沿用旧时间戳
            counter = (packed == (state & ~0xFFFFL)) ? (state & 0xFFFFL) + 1 : 0;
            if (counter > 0xFFFL) { Thread.yield(); continue; } // 单毫秒计数耗尽则自旋
            if (STATE.compareAndSet(state, packed | counter)) { ts = packed >>> 16; break; }
        }
        long msb = (ts << 16) | 0x7000L | (counter & 0xFFFL);
        long lsb = (RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }

    public static void main(String[] args) {
        String uuid = next().toString();
        System.out.println(uuid);
        System.out.println(uuid.charAt(14) == '7');
    }
}
