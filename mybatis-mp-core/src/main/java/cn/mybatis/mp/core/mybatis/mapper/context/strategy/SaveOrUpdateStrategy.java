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
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

@lombok.Getter
public class SaveOrUpdateStrategy<T> {

    private boolean ignoreLogicDeleteWhenCheck = false;

    private boolean allField;

    private Getter[] forceFields;

    private Consumer<Where> on;

    public SaveOrUpdateStrategy<T> ignoreLogicDeleteWhenCheck(boolean ignoreLogicDeleteWhenCheck) {
        this.ignoreLogicDeleteWhenCheck = ignoreLogicDeleteWhenCheck;
        return this;
    }

    /**
     * 设置是否所有字段 新增 或 修改 - null值字段 将会被修改成NULL
     *
     * @param allField 是否所有字段 新增 或 修改
     * @return SELF
     */
    public SaveOrUpdateStrategy<T> allField(boolean allField) {
        this.allField = allField;
        return this;
    }

    /**
     * 设置强制字段 - null值字段 将会被修改成NULL
     *
     * @param forceFields 强制字段
     * @return SELF
     */
    public SaveOrUpdateStrategy<T> forceFields(Getter<T>... forceFields) {
        this.forceFields = forceFields;
        return this;
    }

    /**
     * 设置 自定义存在的where 条件
     *
     * @param where 自定义存在的where 条件
     * @return SELF
     */
    public SaveOrUpdateStrategy<T> on(Consumer<Where> where) {
        this.on = where;
        return this;
    }

    public Consumer<Where> getOn() {
        return on;
    }
}
