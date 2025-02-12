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

package cn.mybatis.mp.core.mybatis.mapper.context.strategy;


import db.sql.api.Getter;
import db.sql.api.cmd.basic.IConflictAction;
import db.sql.api.tookit.LambdaUtil;

import java.util.Set;
import java.util.function.Consumer;

/**
 * 批量插入策略
 *
 * @param <T>
 */
@lombok.Getter
public class SaveBatchStrategy<T> {

    private Set<String> forceFields;

    private Getter<T>[] conflictKeys;

    private String[] conflictColumns;

    private Consumer<IConflictAction<T>> conflictAction;

    /**
     * 设置强制字段 - null值字段 将会被修改成NULL
     *
     * @param forceFields 强制字段
     * @return SELF
     */
    public SaveBatchStrategy<T> forceFields(Getter<T>... forceFields) {
        this.forceFields = LambdaUtil.getFieldNames(forceFields);
        return this;
    }

    /**
     * 设置 争议/冲突字段
     *
     * @param conflictKeys 争议/冲突字段 - 重复数据的KEY
     * @return SELF
     */
    public SaveBatchStrategy<T> conflictKeys(Getter<T>... conflictKeys) {
        this.conflictKeys = conflictKeys;
        return this;
    }

    /**
     * 设置 争议/冲突字段
     *
     * @param conflictKeys 争议/冲突字段 - 重复数据的KEY
     * @return SELF
     */
    public SaveBatchStrategy<T> conflictKeys(String... conflictKeys) {
        this.conflictColumns = conflictKeys;
        return this;
    }

    /**
     * 设置发生争议（重复）的操作
     *
     * @param conflictAction
     */
    public void onConflict(Consumer<IConflictAction<T>> conflictAction) {
        this.conflictAction = conflictAction;
    }
}
