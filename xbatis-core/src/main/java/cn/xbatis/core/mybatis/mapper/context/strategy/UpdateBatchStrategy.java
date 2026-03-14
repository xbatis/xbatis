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

import db.sql.api.Getter;

import java.util.function.Consumer;

/**
 * 原生sql 批量修改策略
 * 不指定batchFields，则修改全部字段
 *
 * @param <T>
 */
@lombok.Getter
public class UpdateBatchStrategy<T> {

    private boolean allFieldUpdate;
    private Getter<T>[] batchFields;
    private boolean ignoreNull;
    private boolean ignoreDefaultValue;

    public static <T> UpdateBatchStrategy<T> create() {
        return new UpdateBatchStrategy<>();
    }


    public static <T> UpdateBatchStrategy<T> create(Consumer<UpdateBatchStrategy<T>> consumer) {
        UpdateBatchStrategy<T> updateBatchStrategy = UpdateBatchStrategy.create();
        consumer.accept(updateBatchStrategy);
        return updateBatchStrategy;
    }

    public static <T> UpdateBatchStrategy<T> of(Class<T> clazz) {
        return new UpdateBatchStrategy<>();
    }

    /**
     * 设置是否所有字段 修改 - null值字段 将会被修改成NULL
     *
     * @param allFieldUpdate 是否所有字段 修改
     * @return SELF
     */
    public UpdateBatchStrategy<T> allFieldUpdate(boolean allFieldUpdate) {
        this.allFieldUpdate = allFieldUpdate;
        return this;
    }

    /**
     * 指定批量字段
     *
     * @param batchFields 批量字段
     * @return SELF
     */
    public UpdateBatchStrategy<T> batchFields(Getter<T>... batchFields) {
        this.batchFields = batchFields;
        return this;
    }

    /**
     * 设置是否忽略null字段
     * true时 会用数据库自己覆盖自己 例如 set name = name; 否则null 时 set name = NULL
     *
     * @param ignoreNull 是否忽略null字段
     * @return SELF
     */
    public UpdateBatchStrategy<T> ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    /**
     * 设置忽略默认值
     *
     * @param ignoreDefaultValue
     * @return
     */
    public UpdateBatchStrategy<T> setIgnoreDefaultValue(boolean ignoreDefaultValue) {
        this.ignoreDefaultValue = ignoreDefaultValue;
        return this;
    }
}
