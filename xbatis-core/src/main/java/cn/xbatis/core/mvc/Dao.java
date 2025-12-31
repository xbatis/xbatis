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

package cn.xbatis.core.mvc;

import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveOrUpdateStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.db.Model;
import db.sql.api.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dao 接口
 *
 * @param <T>
 * @param <ID>
 */
public interface Dao<T, ID> {

    /**
     * 获取ID的类型
     *
     * @return 主键字段的类型
     */
    Class<ID> getIdType();

    /**
     * 获取ID的类型
     *
     * @return 实体类的类型
     */
    Class<T> getEntityType();

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 单个实体
     */
    T getById(ID id);

    /**
     * 根据ID查询，指定目标类型
     *
     * @param targetType 目标类
     * @param id         ID
     * @return 单个目标类型
     */
    <T2> T2 getById(Class<T2> targetType, ID id);

    /**
     * 根据ID查询
     *
     * @param id           ID
     * @param selectFields 指定查询的列
     * @return 单个实体
     */
    @SuppressWarnings("unchecked")
    T getById(ID id, Getter<T>... selectFields);

    /**
     * 实体类保存
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    int save(T entity);

    /**
     * 实体类保存
     *
     * @param entity      实体类对象
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int save(T entity, Getter<T>... forceFields);

    /**
     * 实体类保存
     *
     * @param entity        实体类对象
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    int save(T entity, boolean allFieldForce);

    /**
     * 实体类保存
     *
     * @param entity       实体类对象
     * @param saveStrategy 保存策略
     * @return 影响条数
     */
    int save(T entity, Consumer<SaveStrategy<T>> saveStrategy);

    /**
     * 实体类保存
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    int save(Collection<T> list);

    /**
     * 实体类保存
     *
     * @param list          实体类对象List
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    int save(Collection<T> list, boolean allFieldForce);

    /**
     * 实体类保存
     *
     * @param list        实体类对象List
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int save(Collection<T> list, Getter<T>... forceFields);

    /**
     * 实体类保存
     *
     * @param list         实体类对象List
     * @param saveStrategy 保存策略
     * @return 影响条数
     */
    int save(Collection<T> list, Consumer<SaveStrategy<T>> saveStrategy);

    /**
     * 实体类Model保存
     *
     * @param model        实体类Model对象
     * @param saveStrategy 保存策略
     * @return 影响条数
     */
    <M extends Model<T>> int save(M model, Consumer<SaveStrategy<M>> saveStrategy);

    /**
     * 实体类Model保存
     *
     * @param model 实体类Model对象
     * @return 影响条数
     */
    <M extends Model<T>> int save(M model);

    /**
     * 实体类Model保存
     *
     * @param model       实体类Model对象
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int save(M model, Getter<M>... forceFields);

    /**
     * 实体类Model保存
     *
     * @param model         实体类Model对象
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int save(M model, boolean allFieldForce);

    /**
     * 实体类Model保存
     *
     * @param list 实体类Model对象List
     * @return 影响条数
     */
    <M extends Model<T>> int saveModel(Collection<M> list);

    /**
     * 实体类Model保存
     *
     * @param list          实体类Model对象List
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int saveModel(Collection<M> list, boolean allFieldForce);


    /**
     * 实体类Model保存
     *
     * @param list        实体类Model对象List
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int saveModel(Collection<M> list, Getter<M>... forceFields);


    /**
     * 实体类Model保存
     *
     * @param list         实体类Model对象List
     * @param saveStrategy 保存策略
     * @return 影响条数
     */
    <M extends Model<T>> int saveModel(Collection<M> list, Consumer<SaveStrategy<M>> saveStrategy);


    /**
     * 原生批量插入 实体类Model保存
     *
     * @param list 实体类Model对象List
     * @return 影响条数
     */
    <M extends Model<T>> int saveModelBatch(Collection<M> list);

    /**
     * 原生批量插入 实体类Model保存
     *
     * @param list              实体类Model对象List
     * @param saveBatchStrategy 保存策略
     * @return 影响条数
     */
    <M extends Model<T>> int saveModelBatch(Collection<M> list, Consumer<SaveBatchStrategy<M>> saveBatchStrategy);

    /**
     * 原生批量插入
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    int saveBatch(Collection<T> list);

    /**
     * 原生批量插入
     *
     * @param list              实体类对象List
     * @param saveBatchStrategy 策略
     * @return 影响条数
     */
    int saveBatch(Collection<T> list, Consumer<SaveBatchStrategy<T>> saveBatchStrategy);

    /**
     * 实体类修改
     *
     * @param entity         实体类对象
     * @param updateStrategy 策略
     * @return 影响条数
     */
    int update(T entity, UpdateStrategy<T> updateStrategy);

    /**
     * 实体类修改
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    int update(T entity);

    /**
     * 实体类修改
     *
     * @param entity        实体类对象
     * @param allFieldForce 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    int update(T entity, boolean allFieldForce);

    /**
     * 实体类修改
     *
     * @param entity      实体类对象
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int update(T entity, Getter<T>... forceFields);

    /**
     * 实体类保存
     *
     * @param list           实体类对象List
     * @param updateStrategy 策略
     * @return 影响条数
     */
    int update(Collection<T> list, UpdateStrategy<T> updateStrategy);

    /**
     * 实体类修改
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    int update(Collection<T> list);

    /**
     * 实体类修改
     *
     * @param list          实体类对象List
     * @param allFieldForce 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    int update(Collection<T> list, boolean allFieldForce);

    /**
     * 实体类修改
     *
     * @param list        实体类对象List
     * @param forceFields 强制修改的字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int update(Collection<T> list, Getter<T>... forceFields);


    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list
     * @return 影响条数
     */
    default int updateBatch(Collection<T> list) {
        return updateBatch(list, (Getter<T>[]) null);
    }

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list
     * @param batchFields 必须指定字段
     * @return 影响条数
     */
    int updateBatch(Collection<T> list, Getter<T>... batchFields);


    /**
     * 实体类Model修改
     *
     * @param model 实体类Model对象
     * @return 影响条数
     */
    <M extends Model<T>> int update(M model);

    /**
     * 修改策略
     *
     * @param model
     * @param updateStrategy
     * @param <M>
     * @return 影响条数
     */
    <M extends Model<T>> int update(M model, UpdateStrategy<M> updateStrategy);

    /**
     * 实体类Model修改
     *
     * @param model       实体类Model对象
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int update(M model, Getter<M>... forceFields);

    /**
     * 实体类Model修改
     *
     * @param model         实体类Model对象
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int update(M model, boolean allFieldForce);


    /**
     * 实体类Model修改
     *
     * @param list           实体类Model对象List
     * @param updateStrategy 策略
     * @return 影响条数
     */
    <M extends Model<T>> int updateModel(Collection<M> list, Consumer<UpdateStrategy<M>> updateStrategy);

    /**
     * 实体类Model修改
     *
     * @param list 实体类Model对象List
     * @return 影响条数
     */
    <M extends Model<T>> int updateModel(Collection<M> list);

    /**
     * 实体类Model修改
     *
     * @param list        实体类Model对象List
     * @param forceFields 强制字段，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int updateModel(Collection<M> list, Getter<M>... forceFields);

    /**
     * 实体类Model修改
     *
     * @param list          实体类Model对象List
     * @param allFieldForce 是否所有字段都插入，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int updateModel(Collection<M> list, boolean allFieldForce);

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list
     * @return 影响条数
     */
    default <M extends Model<T>> int updateBatchModel(Collection<M> list) {
        return updateBatchModel(list, (Getter<M>[]) null);
    }

    /**
     * 根据id批量修改操作
     * 原生批量操作 采用 update case when  then else end 操作
     *
     * @param list
     * @param batchFields 必须指定字段
     * @return 影响条数
     */
    <M extends Model<T>> int updateBatchModel(Collection<M> list, Getter<M>... batchFields);

    /**
     * 实体类保存或修改
     *
     * @param entity               实体类对象
     * @param saveOrUpdateStrategy 策略
     * @return 影响条数
     */
    int saveOrUpdate(T entity, Consumer<SaveOrUpdateStrategy<T>> saveOrUpdateStrategy);

    /**
     * 实体类保存或修改
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    int saveOrUpdate(T entity);

    /**
     * 实体类保存或修改
     *
     * @param entity        实体类对象
     * @param allFieldForce 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    int saveOrUpdate(T entity, boolean allFieldForce);

    /**
     * 实体类保存或修改
     *
     * @param entity      实体类对象
     * @param forceFields 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int saveOrUpdate(T entity, Getter<T>... forceFields);

    /**
     * 实体类保存或修改
     *
     * @param list                 实体类对象List
     * @param saveOrUpdateStrategy
     * @return 影响条数
     */
    int saveOrUpdate(Collection<T> list, Consumer<SaveOrUpdateStrategy<T>> saveOrUpdateStrategy);

    /**
     * 实体类保存或修改
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    int saveOrUpdate(Collection<T> list);

    /**
     * 实体类保存或修改
     *
     * @param list          实体类对象List
     * @param allFieldForce 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    int saveOrUpdate(Collection<T> list, boolean allFieldForce);

    /**
     * 实体类保存或修改
     *
     * @param list        实体类对象List
     * @param forceFields 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    int saveOrUpdate(Collection<T> list, Getter<T>... forceFields);


    /**
     * 实体类Model保存或修改
     *
     * @param model                实体类Model对象
     * @param saveOrUpdateStrategy 策略
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdate(M model, Consumer<SaveOrUpdateStrategy<M>> saveOrUpdateStrategy);

    /**
     * 实体类Model保存或修改
     *
     * @param model 实体类Model对象
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdate(M model);

    /**
     * 实体类Model保存或修改
     *
     * @param model         实体类Model对象
     * @param allFieldForce 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdate(M model, boolean allFieldForce);

    /**
     * 实体类Model保存或修改
     *
     * @param model       实体类Model对象
     * @param forceFields 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int saveOrUpdate(M model, Getter<M>... forceFields);

    /**
     * 实体类Model保存或修改
     *
     * @param list                 实体类Model对象List
     * @param saveOrUpdateStrategy 策略
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdateModel(Collection<M> list, Consumer<SaveOrUpdateStrategy<M>> saveOrUpdateStrategy);

    /**
     * 实体类Model保存或修改
     *
     * @param list 实体类Model对象List
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdateModel(Collection<M> list);

    /**
     * 实体类Model保存或修改
     *
     * @param list          实体类Model对象List
     * @param allFieldForce 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    <M extends Model<T>> int saveOrUpdateModel(Collection<M> list, boolean allFieldForce);

    /**
     * 实体类Model保存或修改
     *
     * @param list        实体类Model对象List
     * @param forceFields 是否所有字段都保存或修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    @SuppressWarnings("unchecked")
    <M extends Model<T>> int saveOrUpdateModel(Collection<M> list, Getter<M>... forceFields);

    /**
     * 实体类删除
     *
     * @param entity 实体类对象
     * @return 删除的数量
     */
    int delete(T entity);

    /**
     * 实体类删除
     *
     * @param list 实体类对象List
     * @return 删除的数量
     */
    int delete(Collection<T> list);

    /**
     * 根据ID删除
     *
     * @param id ID
     * @return 删除的数量
     */
    int deleteById(ID id);

    /**
     * 根据ID删除
     *
     * @param ids 多个ID
     * @return 删除的数量
     */
    @SuppressWarnings("unchecked")
    int deleteByIds(ID... ids);

    /**
     * 根据ID删除
     *
     * @param ids ID 集合
     * @return 删除的数量
     */
    int deleteByIds(Collection<ID> ids);

    /**
     * 实体类结果转成Map<ID,T>
     *
     * @param ids 多个ID
     * @return Map 结果
     */
    @SuppressWarnings("unchecked")
    Map<ID, T> map(ID... ids);

    /**
     * 实体类结果转成Map<ID,T>
     *
     * @param ids ID 集合
     * @return Map 结果
     */
    Map<ID, T> map(Collection<ID> ids);
}
