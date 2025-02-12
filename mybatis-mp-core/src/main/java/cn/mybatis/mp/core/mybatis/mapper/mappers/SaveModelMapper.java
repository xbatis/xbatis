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

import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.SaveModelMethodUtil;
import cn.mybatis.mp.core.sql.executor.Insert;
import cn.mybatis.mp.db.Model;
import db.sql.api.Getter;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public interface SaveModelMapper<T> extends BaseMapper<T> {
    /**
     * 实体类新增
     *
     * @param model    实体类Model实例
     * @param consumer 保存策略
     * @return 影响条数
     */
    default <M extends Model<T>> int save(M model, Consumer<SaveStrategy<M>> consumer) {
        SaveStrategy strategy = new SaveStrategy();
        consumer.accept(strategy);
        return SaveModelMethodUtil.save(getBasicMapper(), model, strategy);
    }

    /**
     * 实体类新增
     *
     * @param model
     * @return 影响条数
     */
    default <M extends Model<T>> int save(M model) {
        return save(model, false);
    }

    /**
     * 实体类新增
     *
     * @param model         实体类Model实例
     * @param allFieldForce 所有字段都强制保存,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <M extends Model<T>> int save(M model, boolean allFieldForce) {
        return this.save(model, saveStrategy -> {
            saveStrategy.allFieldSave(allFieldForce);
        });
    }

    /**
     * 实体类新增
     *
     * @param model       实体类Model实例
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <M extends Model<T>> int save(M model, Getter<M>... forceFields) {
        return this.save(model, saveStrategy -> {
            saveStrategy.forceFields(forceFields);
        });
    }


    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @param consumer 保存策略
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModel(Collection<M> list, Consumer<SaveStrategy<M>> consumer) {
        SaveStrategy<M> strategy = new SaveStrategy();
        consumer.accept(strategy);
        return SaveModelMethodUtil.saveList(getBasicMapper(), list, strategy);
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModel(Collection<M> list) {
        return saveModel(list, false);
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @param allFieldForce 所有字段都强制保存,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModel(Collection<M> list, boolean allFieldForce) {
        return this.saveModel(list, saveStrategy -> {
            saveStrategy.allFieldSave(allFieldForce);
        });
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModel(Collection<M> list, Getter<M>... forceFields) {
        return this.saveModel(list, saveStrategy -> {
            saveStrategy.forceFields(forceFields);
        });
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     *
     * @param list     需要插入数据
     * @param strategy 插入策略
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModelBatch(Collection<M> list, Consumer<SaveBatchStrategy<M>> strategy) {
        SaveBatchStrategy saveBatchStrategy = new SaveBatchStrategy<>();
        strategy.accept(saveBatchStrategy);
        return SaveModelMethodUtil.saveBatch(getBasicMapper(), new Insert(), list, saveBatchStrategy);
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     *
     * @param list
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModelBatch(Collection<M> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return SaveModelMethodUtil.saveBatch(getBasicMapper(), list);
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     * <p>
     * 会自动加入 主键 租户ID 逻辑删除列 乐观锁
     * 自动设置 默认值,不会忽略NULL值字段
     *
     * @param list
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <M extends Model<T>> int saveModelBatch(Collection<M> list, Getter<M>... forceFields) {
        return this.saveModelBatch(list, saveBatchStrategy -> {
            saveBatchStrategy.forceFields(forceFields);
        });
    }
}
