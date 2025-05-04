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
import cn.xbatis.core.mybatis.mapper.mappers.utils.GetMethodUtil;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.io.Serializable;
import java.util.function.Consumer;

public interface GetBasicMapper extends BaseBasicMapper {

    /**
     * 根据ID查询
     *
     * @param entityType 实体类
     * @param id         ID
     * @return 单个当前实体类
     */
    default <T, ID extends Serializable> T getById(Class<T> entityType, ID id) {
        return this.getById(entityType, id, (Getter<T>[]) null);
    }

    /**
     * 根据ID查询，只返回指定列
     *
     * @param entityType   实体类
     * @param id           ID
     * @param selectFields select列
     * @return 单个当前实体类
     */
    default <T, ID extends Serializable> T getById(Class<T> entityType, ID id, Getter<T>... selectFields) {
        return GetMethodUtil.getById(getBasicMapper(), Tables.get(entityType), id, selectFields);
    }

    /**
     * 根据ID查询，指定目标类型
     *
     * @param entityType 实体类
     * @param targetType 目标类
     * @param id         ID
     * @return 单个目标类型
     */
    default <T, T2, ID extends Serializable> T2 getById(Class<T> entityType, Class<T2> targetType, ID id) {
        return GetMethodUtil.getById(getBasicMapper(), Tables.get(entityType), targetType, id);
    }

    /**
     * 单个查询
     *
     * @param entityType 实体类
     * @param consumer   where consumer
     * @return 单个当前实体
     */
    default <T> T get(Class<T> entityType, Consumer<Where> consumer) {
        return this.get(entityType, consumer, (Getter<T>[]) null);
    }

    /**
     * 单个查询
     *
     * @param entityType   实体类
     * @param consumer     where consumer
     * @param selectFields select列
     * @return 单个当前实体
     */
    default <T> T get(Class<T> entityType, Consumer<Where> consumer, Getter<T>... selectFields) {
        return GetMethodUtil.get(getBasicMapper(), Tables.get(entityType), consumer, selectFields);
    }

    /**
     * 单个查询
     *
     * @param entityType 实体类
     * @param where      where
     * @return 单个当前实体
     */
    default <T> T get(Class<T> entityType, Where where) {
        return get(entityType, where, (Getter<T>[]) null);
    }

    /**
     * 单个查询
     *
     * @param entityType   实体类
     * @param where        where
     * @param selectFields select列
     * @return 单个当前实体
     */
    default <T> T get(Class<T> entityType, Where where, Getter<T>... selectFields) {
        return GetMethodUtil.get(getBasicMapper(), Tables.get(entityType), where, selectFields);
    }
}
