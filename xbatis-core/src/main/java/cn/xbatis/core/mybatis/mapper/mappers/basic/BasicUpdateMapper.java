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

package cn.xbatis.core.mybatis.mapper.mappers.basic;

import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateBatchStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.mybatis.mapper.mappers.utils.UpdateMethodUtil;
import cn.xbatis.core.util.UpdateProxyUtil;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface BasicUpdateMapper extends BasicBaseMapper {

    /**
     * 局部修改（精准修改，只修改set过的和updateDefaultValueFillAlways=true的字段）
     *
     * @param entity 目标实体类
     * @param proxy  实体类proxy实例
     * @param <E>    实体类类型
     * @return 修改条数
     */
    default <E> int partialUpdate(E entity, Consumer<E> proxy) {
        Set<String> updateFields = new HashSet<>();
        E e2 = UpdateProxyUtil.of(entity, field -> {
            updateFields.add(field);
        });
        proxy.accept(e2);
        if (updateFields.isEmpty()) {
            return 0;
        }
        UpdateStrategy<E> updateStrategy = UpdateStrategy.create();
        updateStrategy.updateFields(updateFields);
        return this.update(entity, updateStrategy);
    }

    /**
     * 实体类修改
     *
     * @param entity         实体类对象
     * @param updateStrategy 策略
     * @return 影响条数
     */
    default <T> int update(T entity, UpdateStrategy<T> updateStrategy) {
        return UpdateMethodUtil.update(getBasicMapper(), Tables.get(entity.getClass()), entity, updateStrategy);
    }

    /**
     * 实体类修改
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    default <T> int update(T entity) {
        return this.update(entity, false);
    }

    /**
     * 实体类修改
     *
     * @param entity        实体类对象
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    default <T> int update(T entity, boolean allFieldForce) {
        return UpdateMethodUtil.update(getBasicMapper(), entity, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce);
        });
    }

    /**
     * 实体类修改
     *
     * @param entity      实体类
     * @param forceFields 强制更新指定，解决需要修改为null的需求
     * @return 影响条数
     */
    default <T> int update(T entity, Getter<T>... forceFields) {
        return UpdateMethodUtil.update(getBasicMapper(), entity, updateStrategy -> {
            updateStrategy.forceFields(forceFields);
        });
    }

    /**
     * 动态条件修改
     *
     * @param entity   实体类
     * @param consumer where
     * @return 影响条数
     */
    default <T> int update(T entity, Consumer<Where> consumer) {
        return UpdateMethodUtil.update(getBasicMapper(), entity, updateStrategy -> {
            updateStrategy.on(consumer);
        });
    }

    /**
     * 指定where 修改
     *
     * @param entity 实体类对象
     * @param where  where
     * @return 影响条数
     */
    default <T> int update(T entity, Where where) {
        return UpdateMethodUtil.update(getBasicMapper(), entity, updateStrategy -> {
            updateStrategy.on(where);
        });
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list           实体类对象List
     * @param updateStrategy 策略
     * @return 影响条数
     */
    default <T> int update(Collection<T> list, UpdateStrategy<T> updateStrategy) {
        return UpdateMethodUtil.updateList(getBasicMapper(), list, updateStrategy);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    default <T> int update(Collection<T> list) {
        return this.update(list, false);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list          实体类对象List
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    default <T> int update(Collection<T> list, boolean allFieldForce) {
        return UpdateMethodUtil.updateList(getBasicMapper(), list, updateStrategy -> {
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
    default <T> int update(Collection<T> list, Getter<T>... forceFields) {
        return UpdateMethodUtil.updateList(getBasicMapper(), list, updateStrategy -> {
            updateStrategy.forceFields(forceFields);
        });
    }

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list 实体类的集合
     * @param <T>  实体类的类型
     * @return 影响条数
     */
    default <T> int updateBatch(Collection<T> list) {
        return updateBatch(list, (Getter<T>[]) null);
    }

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list        实体类的集合
     * @param batchFields 必须指定字段
     * @param <T>         实体类的类型
     * @return 影响条数
     */
    default <T> int updateBatch(Collection<T> list, Getter<T>... batchFields) {
        return UpdateMethodUtil.updateBatch(getBasicMapper(), list, batchFields);
    }

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list                实体类集合
     * @param updateBatchStrategy 指定策略
     * @param <T>                 实体类
     * @return 影响条数
     */
    default <T> int updateBatch(Collection<T> list, UpdateBatchStrategy<T> updateBatchStrategy) {
        return UpdateMethodUtil.updateBatch(getBasicMapper(), list, updateBatchStrategy);
    }
}
