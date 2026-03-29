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

package cn.xbatis.core.mybatis.mapper.context.strategy;

public enum UpdateBatchInMode {
    /**
     * 默认 只对设定的已支持的数据库使用多列进行多列in操作
     */
    DEFAULT,
    /**
     * 强制 多列进行多列in操作
     */
    FORCE_MULTI_ROW,
    /**
     * 不使用多列in操作
     */
    NOT_MULTI_ROW,
}
