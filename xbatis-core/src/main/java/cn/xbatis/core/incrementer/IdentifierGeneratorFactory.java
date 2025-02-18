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

package cn.xbatis.core.incrementer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自增器 工厂
 */
public class IdentifierGeneratorFactory {

    private static final Map<String, IdentifierGenerator<?>> IDENTIFIER_GENERATOR_MAP = new HashMap<>();

    static {
        IdWorkerGenerator idWorkerGenerator = new IdWorkerGenerator();
        IDENTIFIER_GENERATOR_MAP.put(IdentifierGeneratorType.DEFAULT, idWorkerGenerator);
        IDENTIFIER_GENERATOR_MAP.put(IdentifierGeneratorType.UUID, new UUIDGenerator());
        IDENTIFIER_GENERATOR_MAP.put(IdentifierGeneratorType.mpNextId, idWorkerGenerator);
    }

    private IdentifierGeneratorFactory() {
    }

    /**
     * 获取自增器
     *
     * @param name
     * @return
     */
    public static <T> IdentifierGenerator<T> getIdentifierGenerator(String name) {
        if (name == null) {
            throw new RuntimeException("IdentifierGenerator name can't be null");
        }
        IdentifierGenerator<T> identifierGenerator = (IdentifierGenerator<T>) IDENTIFIER_GENERATOR_MAP.get(name);
        if (identifierGenerator == null) {
            throw new RuntimeException(name + " IdentifierGenerator is not exists");
        }
        return identifierGenerator;
    }

    /**
     * 注册自增器
     * 除了DEFAULT 注册器可以重新注册 其他均不行
     *
     * @param name
     * @param identifierGenerator
     */
    public static void register(String name, IdentifierGenerator<?> identifierGenerator) {
        if (!IdentifierGeneratorType.DEFAULT.equals(name) && IDENTIFIER_GENERATOR_MAP.containsKey(name)) {
            throw new RuntimeException(name + " IdentifierGenerator already exists");
        }
        IDENTIFIER_GENERATOR_MAP.put(name, identifierGenerator);
    }
}
