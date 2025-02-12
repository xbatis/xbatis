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

package cn.mybatis.mp.core.mybatis.mapper.mappers;

import cn.mybatis.mp.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.UpdateMethodUtil;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.UpdateModelMethodUtil;
import cn.mybatis.mp.db.Model;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.Collection;
import java.util.function.Consumer;

public interface UpdateModelMapper<T> extends BaseMapper<T> {

    /**
     * 实体类修改
     *
     * @param model 实体类Model对象
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, UpdateStrategy<M> updateStrategy) {
        return UpdateModelMethodUtil.update(getBasicMapper(), model, updateStrategy);
    }

    /**
     * 实体类修改
     *
     * @param model 实体类对象
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model) {
        return this.update(model, false);
    }

    /**
     * 实体类修改
     *
     * @param model         实体类对象
     * @param allFieldForce 所有字段都强制保存
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, boolean allFieldForce) {
        UpdateStrategy updateStrategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.allFieldUpdate(allFieldForce);
        return this.update(model, updateStrategy);
    }

    /**
     * 实体类修改
     *
     * @param model
     * @param forceFields 强制更新指定，解决需要修改为null的需求
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, Getter<M>... forceFields) {
        UpdateStrategy updateStrategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.forceFields(forceFields);
        return this.update(model, updateStrategy);
    }

    /**
     * 动态条件修改
     *
     * @param model    实体类
     * @param consumer where
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, Consumer<Where> consumer) {
        return this.update(model, false, consumer);
    }

    /**
     * 动态条件修改
     *
     * @param model         实体类对象
     * @param allFieldForce 所有字段都强制保存
     * @param where         where
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, boolean allFieldForce, Consumer<Where> where) {
        UpdateStrategy updateStrategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.allFieldUpdate(allFieldForce);
        updateStrategy.on(where);
        return this.update(model, updateStrategy);
    }

    /**
     * 指定where条件修改
     *
     * @param model 实体类
     * @param where where
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, Where where) {
        return this.update(model, false, where);
    }

    /**
     * 指定where条件修改
     *
     * @param model         实体类对象
     * @param allFieldForce 所有字段都强制保存
     * @param where         where
     * @return 影响条数
     */
    default <M extends Model<T>> int update(M model, boolean allFieldForce, Where where) {
        UpdateStrategy updateStrategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.allFieldUpdate(allFieldForce);
        updateStrategy.on(where);
        return this.update(model, updateStrategy);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    default <T, M extends Model<T>> int updateModel(Collection<M> list, Consumer<UpdateStrategy<M>> updateStrategy) {
        UpdateStrategy strategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.accept(strategy);
        return UpdateModelMethodUtil.updateList(getBasicMapper(), list, strategy);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    default <M extends Model<T>> int updateModel(Collection<M> list) {
        return this.updateModel(list, false);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list          实体类对象List
     * @param allFieldForce 所有字段都强制保存
     * @return 影响条数
     */
    default <M extends Model<T>> int updateModel(Collection<M> list, boolean allFieldForce) {
        return this.updateModel(list, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce);
        });
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list        实体类对象List
     * @param forceFields 强制更新指定，解决需要修改为null的需求
     * @return 影响条数
     */
    default <M extends Model<T>> int updateModel(Collection<M> list, Getter<M>... forceFields) {
        return this.updateModel(list, updateStrategy -> {
            updateStrategy.forceFields(forceFields);
        });
    }
}
