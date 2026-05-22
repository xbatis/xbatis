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

package cn.xbatis.core.mvc;

import db.sql.api.Getter;
import db.sql.api.GetterFun;

import java.util.Optional;

/**
 * Dao 接口
 * 只定义基础的 xxxById 方法
 * @param <T>
 * @param <ID>
 */
public interface Dao<T, ID> {
    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 单个实体的 Optional对象
     */
    default Optional<T> getOptionalById(ID id) {
        return Optional.ofNullable(getById(id));
    }

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
     * @return 单个目标类型的 Optional对象
     */
    default <T2> Optional<T2> getOptionalById(Class<T2> targetType, ID id) {
        return Optional.ofNullable(getById(targetType, id));
    }

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
     * 根据ID查询，指定字段的值
     * @param id
     * @param getter 返回字段对应的值
     * @return 指定字段的值
     * @param <V>
     */
    <V> V getValueById(ID id, GetterFun<T, V> getter);
}
