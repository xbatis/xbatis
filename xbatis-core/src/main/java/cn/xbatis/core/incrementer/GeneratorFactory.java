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
public class GeneratorFactory {

    private static final Map<String, Generator<?>> GENERATOR_MAP = new HashMap<>();

    static {
        IdWorkerGenerator idWorkerGenerator = new IdWorkerGenerator();
        GENERATOR_MAP.put(GeneratorTypes.DEFAULT, idWorkerGenerator);
        GENERATOR_MAP.put(GeneratorTypes.UUID, new UUIDGenerator());
        GENERATOR_MAP.put(GeneratorTypes.nextId, idWorkerGenerator);
    }

    private GeneratorFactory() {
    }

    /**
     * 获取自增器
     *
     * @param name
     * @return
     */
    public static <T> Generator<T> getIdentifierGenerator(String name) {
        if (name == null) {
            throw new RuntimeException("Generator name can't be null");
        }
        Generator<T> generator = (Generator<T>) GENERATOR_MAP.get(name);
        if (generator == null) {
            throw new RuntimeException(name + " Generator is not exists");
        }
        return generator;
    }

    /**
     * 注册自增器
     * 除了DEFAULT 注册器可以重新注册 其他均不行
     *
     * @param name
     * @param generator
     */
    public static void register(String name, Generator<?> generator) {
        if (!GeneratorTypes.DEFAULT.equals(name) && GENERATOR_MAP.containsKey(name)) {
            throw new RuntimeException(name + " Generator already exists");
        }
        GENERATOR_MAP.put(name, generator);
    }
}
