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

/**
 * 后续使用
 *
 * @see Generators
 */
@Deprecated
public class IdentifierGeneratorType {

    /**
     * 基于java uuid获取
     */
    public static final String UUID = Generators.UUID;

    /**
     * 基于雪花算法
     * 此方法已不建议使用，后续使用 xbId
     */
    @Deprecated
    public static final String mpNextId = Generators.nextId;

    /**
     * 默认生成器
     */
    public static final String DEFAULT = Generators.DEFAULT;
}
