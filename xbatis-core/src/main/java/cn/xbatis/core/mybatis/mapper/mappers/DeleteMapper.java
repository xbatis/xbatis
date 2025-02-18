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

package cn.xbatis.core.mybatis.mapper.mappers;

import cn.xbatis.core.mybatis.mapper.mappers.utils.DeleteMethodUtil;
import db.sql.api.impl.cmd.struct.Where;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Consumer;

public interface DeleteMapper<T> extends BaseMapper<T> {
    /**
     * 根据实体类删除
     *
     * @param entity 实体类实例
     * @return 影响条数
     */
    default int delete(T entity) {
        return DeleteMethodUtil.delete(getBasicMapper(), getTableInfo(), entity);
    }

    /**
     * 多个删除
     *
     * @param list 实体类实例list
     * @return 影响条数
     */
    default int delete(Collection<T> list) {
        return DeleteMethodUtil.delete(getBasicMapper(), getTableInfo(), list);
    }


    /**
     * 根据id删除
     *
     * @param id ID
     * @return 影响条数
     */
    default <ID extends Serializable> int deleteById(ID id) {
        return DeleteMethodUtil.deleteById(getBasicMapper(), getTableInfo(), id);
    }

    /**
     * 批量删除多个
     *
     * @param ids 多个ID
     * @return 影响条数
     */
    default <ID extends Serializable> int deleteByIds(ID... ids) {
        return DeleteMethodUtil.deleteByIds(getBasicMapper(), getTableInfo(), ids);
    }

    /**
     * 批量删除多个
     *
     * @param ids 多个ID
     * @return 影响数量
     */
    default <ID extends Serializable> int deleteByIds(Collection<ID> ids) {
        return DeleteMethodUtil.deleteByIds(getBasicMapper(), getTableInfo(), ids);
    }

    /**
     * 动态条件删除
     *
     * @param consumer
     * @return 影响条数
     */
    default int delete(Consumer<Where> consumer) {
        return DeleteMethodUtil.delete(getBasicMapper(), getTableInfo(), consumer);
    }

    /**
     * 动态条件删除
     *
     * @param where
     * @return 影响条数
     */
    default int delete(Where where) {
        return DeleteMethodUtil.delete(getBasicMapper(), getTableInfo(), where);
    }

    /**
     * 删除所有数据
     *
     * @return 影响条数
     */
    default int deleteAll() {
        return DeleteMethodUtil.deleteAll(getBasicMapper(), getTableInfo());
    }

    /**
     * TRUNCATE TABLE
     *
     * @return 影响条数
     */
    default int truncate() {
        return DeleteMethodUtil.truncate(getBasicMapper(), getTableInfo());
    }
}
