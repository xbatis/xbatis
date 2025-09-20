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


import cn.xbatis.core.mybatis.mapper.context.strategy.SaveOrUpdateStrategy;
import cn.xbatis.core.mybatis.mapper.mappers.utils.SaveOrUpdateModelMethodUtil;
import cn.xbatis.db.Model;
import db.sql.api.Getter;

import java.util.Collection;
import java.util.function.Consumer;

public interface BasicSaveOrUpdateModelMapper extends BasicBaseMapper {


    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param model
     * @param <M>
     * @return
     */
    default <T, M extends Model<T>> int saveOrUpdate(M model, Consumer<SaveOrUpdateStrategy<M>> saveOrUpdateStrategy) {
        SaveOrUpdateStrategy strategy = new SaveOrUpdateStrategy();
        saveOrUpdateStrategy.accept(strategy);
        return SaveOrUpdateModelMethodUtil.saveOrUpdate(getBasicMapper(), model, strategy);
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param model
     * @param <M>
     * @return
     */
    default <T, M extends Model<T>> int saveOrUpdate(M model) {
        return this.saveOrUpdate(model, false);
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param model
     * @param allFieldForce 所有字段都强制保存或修改,null值将会以NULL的形式插入
     * @param <M>
     * @return
     */
    default <T, M extends Model<T>> int saveOrUpdate(M model, boolean allFieldForce) {
        return this.saveOrUpdate(model, saveOrUpdateStrategy -> {
            saveOrUpdateStrategy.allField(allFieldForce);
        });
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param model
     * @param forceFields 强制字段
     * @param <M>
     * @return 影响条数
     */
    default <T, M extends Model<T>> int saveOrUpdate(M model, Getter<M>... forceFields) {
        return this.saveOrUpdate(model, saveOrUpdateStrategy -> {
            saveOrUpdateStrategy.forceFields(forceFields);
        });
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list 实体类Model 对象List
     * @return 影响条数
     */
    default <M extends Model<T>, T> int saveOrUpdateModel(Collection<M> list, Consumer<SaveOrUpdateStrategy<M>> saveOrUpdateStrategy) {
        SaveOrUpdateStrategy strategy = new SaveOrUpdateStrategy();
        saveOrUpdateStrategy.accept(strategy);
        return SaveOrUpdateModelMethodUtil.saveOrUpdate(getBasicMapper(), list, strategy);
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list 实体类Model 对象List
     * @return 影响条数
     */
    default <T, M extends Model<T>> int saveOrUpdateModel(Collection<M> list) {
        return this.saveOrUpdateModel(list, false);
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list          实体类Model 对象List
     * @param allFieldForce 所有字段都强制保存或修改,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T, M extends Model<T>> int saveOrUpdateModel(Collection<M> list, boolean allFieldForce) {
        return this.saveOrUpdateModel(list, saveOrUpdateStrategy -> {
            saveOrUpdateStrategy.allField(allFieldForce);
        });
    }

    /**
     * 实体类Model新增或修改
     * 先查是否存在，再进行新增或修改
     *
     * @param list        实体类Model 对象List
     * @param forceFields 强制字段
     * @return 影响条数
     */
    default <T, M extends Model<T>> int saveOrUpdateModel(Collection<M> list, Getter<M>... forceFields) {
        return this.saveOrUpdateModel(list, saveOrUpdateStrategy -> {
            saveOrUpdateStrategy.forceFields(forceFields);
        });
    }
}
