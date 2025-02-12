/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package db.sql.api.cmd;

/**
 * 修改策略
 */
public enum UpdateStrategy {
    /**
     * 值为null忽略
     */
    NULL_IGNORE,
    /**
     * 值为null时，修改为NULL
     */
    NULL_TO_NULL,

    /**
     * 抛出异常
     */
    THROW_EXCEPTION;
}
