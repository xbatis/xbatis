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

package cn.xbatis.core.mybatis.mapper.mappers.basicMapper;


import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveOrUpdateStrategy;
import cn.xbatis.core.mybatis.mapper.mappers.utils.SaveOrUpdateMethodUtil;
import db.sql.api.Getter;

import java.util.Collection;
import java.util.function.Consumer;

public interface BasicSaveOrUpdateMapper extends BasicBaseMapper {

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param entity
     * @param saveOrUpdateStrategy 策略
     * @return 影响条数
     */
    default <T> int saveOrUpdate(T entity, Consumer<SaveOrUpdateStrategy<T>> saveOrUpdateStrategy) {
        SaveOrUpdateStrategy strategy = new SaveOrUpdateStrategy();
        saveOrUpdateStrategy.accept(strategy);
        return SaveOrUpdateMethodUtil.saveOrUpdate(getBasicMapper(), Tables.get(entity.getClass()), entity, strategy);
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param entity
     * @return 影响条数
     */
    default <T> int saveOrUpdate(T entity) {
        return this.saveOrUpdate(entity, false);
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param entity
     * @param allFieldForce 所有字段都强制保存或修改,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int saveOrUpdate(T entity, boolean allFieldForce) {
        return this.saveOrUpdate(entity, (saveOrUpdateStrategy) -> {
            saveOrUpdateStrategy.allField(allFieldForce);
        });
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param entity
     * @param forceFields 强制字段
     * @return 影响条数
     */
    default <T> int saveOrUpdate(T entity, Getter<T>... forceFields) {
        return this.saveOrUpdate(entity, (saveOrUpdateStrategy) -> {
            saveOrUpdateStrategy.forceFields(forceFields);
        });
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list                 实体类对象List
     * @param saveOrUpdateStrategy 策略
     * @return 影响条数
     */
    default <T> int saveOrUpdate(Collection<T> list, Consumer<SaveOrUpdateStrategy<T>> saveOrUpdateStrategy) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        T first = list.stream().findFirst().get();
        SaveOrUpdateStrategy strategy = new SaveOrUpdateStrategy();
        saveOrUpdateStrategy.accept(strategy);
        return SaveOrUpdateMethodUtil.saveOrUpdate(getBasicMapper(), Tables.get(first.getClass()), list, strategy);
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    default <T> int saveOrUpdate(Collection<T> list) {
        return this.saveOrUpdate(list, false);
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list          实体类对象List
     * @param allFieldForce 所有字段都强制保存或修改,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int saveOrUpdate(Collection<T> list, boolean allFieldForce) {
        return this.saveOrUpdate(list, (Consumer<SaveOrUpdateStrategy<T>>) (saveOrUpdateStrategy) -> {
            saveOrUpdateStrategy.allField(allFieldForce);
        });
    }

    /**
     * 实体类新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list        实体类对象List
     * @param forceFields 强制字段
     * @return 影响条数
     */
    default <T> int saveOrUpdate(Collection<T> list, Getter<T>... forceFields) {
        return this.saveOrUpdate(list, (Consumer<SaveOrUpdateStrategy<T>>) (saveOrUpdateStrategy) -> {
            saveOrUpdateStrategy.forceFields(forceFields);
        });
    }
}
