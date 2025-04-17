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

package cn.xbatis.core.mybatis.mapper;

import cn.xbatis.core.mybatis.mapper.mappers.*;
import cn.xbatis.core.sql.executor.BaseDelete;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.BaseUpdate;
import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import cn.xbatis.page.IPager;
import db.sql.api.impl.cmd.executor.SelectorCall;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 数据库 Mapper
 * $ 开头的方法一般不建议去使用
 *
 * @param <T>
 */
public interface MybatisMapper<T> extends BaseMapper, GetMapper<T>, ExistsMapper<T>, CountMapper<T>, ListMapper<T>, CursorMapper<T>,
        PagingMapper<T>, MapWithKeyMapper<T>, SaveMapper<T>, SaveOrUpdateMapper<T>, SaveModelMapper<T>, SaveOrUpdateModelMapper<T>,
        UpdateMapper<T>, UpdateModelMapper<T>, DeleteMapper<T> {

    /**
     * 选择器 不同数据库执行不同的方法
     *
     * @param consumer
     */
    <R> R dbAdapt(Consumer<SelectorCall<R>> consumer);

    /**
     * 执行原生非查询类sql
     *
     * @param sql    例如 update xx set name=? where id=?
     * @param params 例如 abc ,1
     * @return 影响的数量
     */
    default int execute(String sql, Object... params) {
        return getBasicMapper().execute(sql, params);
    }

    /**
     * 执行原生非查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 update xx set name=? where id=? RETURNING name
     * @param params     例如 abc ,1
     * @return 影响的数量
     */
    default <T> T executeAndReturning(Class<T> returnType, String sql, Object... params) {
        return getBasicMapper().executeAndReturning(returnType, sql, params);
    }

    /**
     * 执行原生非查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 update xx set name=? where id=? RETURNING name
     * @param params     例如 abc ,1
     * @return 影响的数量
     */
    default <T> List<T> executeAndReturningList(Class<T> returnType, String sql, Object... params) {
        return getBasicMapper().executeAndReturningList(returnType, sql, params);
    }

    /**
     * 执行原生单个查询查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 select xx from table where id=?
     * @param params     例如 1
     * @return 影响的数量
     */
    default <T> T select(Class<T> returnType, String sql, Object... params) {
        return getBasicMapper().select(returnType, sql, params);
    }

    /**
     * 执行原生List查询查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 select xx from table where id=?
     * @param params     例如 1
     * @return 影响的数量
     */
    default <T> List<T> selectList(Class<T> returnType, String sql, Object... params) {
        return getBasicMapper().selectList(returnType, sql, params);
    }

    @Override
    default <T2> T2 get(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().get(query);
    }

    @Override
    default boolean exists(BaseQuery<? extends BaseQuery, ?> query) {
        return getBasicMapper().exists(query);
    }

    @Override
    default int save(BaseInsert<?> insert) {
        return getBasicMapper().save(insert);
    }

    @Override
    default int update(BaseUpdate<?> update) {
        return getBasicMapper().update(update);
    }

    @Override
    default <R> R updateAndGet(UpdateChain update) {
        return getBasicMapper().updateAndGet(update);
    }

    @Override
    default <R> List<R> updateAndList(UpdateChain update) {
        return getBasicMapper().updateAndList(update);
    }

    @Override
    default int delete(BaseDelete<?> delete) {
        return getBasicMapper().delete(delete);
    }

    @Override
    default <R> R deleteAndReturning(DeleteChain delete) {
        return getBasicMapper().deleteAndReturning(delete);
    }

    @Override
    default <R> List<R> deleteAndReturningList(DeleteChain delete) {
        return getBasicMapper().deleteAndReturningList(delete);
    }

    @Override
    default <T2> List<T2> list(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().list(query);
    }

    @Override
    default <T2> Cursor<T2> cursor(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().cursor(query);
    }

    @Override
    default Integer count(BaseQuery<? extends BaseQuery, ?> query) {
        return getBasicMapper().count(query);
    }

    @Override
    default <T2, P extends IPager<T2>> P paging(BaseQuery<? extends BaseQuery, T2> query, P pager) {
        return getBasicMapper().paging(query, pager);
    }

    @Override
    default <K, T2> Map<K, T2> mapWithKey(String mapKey, BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().mapWithKey(mapKey, query);
    }

}