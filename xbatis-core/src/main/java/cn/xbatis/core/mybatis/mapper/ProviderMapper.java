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

import cn.xbatis.core.mybatis.mapper.context.*;
import cn.xbatis.core.mybatis.provider.MybatisSQLProvider;
import db.sql.api.DbType;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface ProviderMapper {

    /**
     * 动态查询 返回单个
     *
     * @param queryContext 上下文
     * @return 返回单个查询
     * @see MybatisSQLProvider#getCmdQuery (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.GET_QUERY_NAME)
    <R> R $get(SQLCmdQueryContext queryContext, RowBounds rowBounds);

    /**
     * ID查询 返回单个
     *
     * @param queryContext 上下文
     * @return 返回单个查询
     * @see MybatisSQLProvider#getByIdCmdQuery (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.GET_BY_ID_QUERY_NAME)
    <R> R $getById(SQLCmdQueryContext queryContext, RowBounds rowBounds);

    /**
     * @param insertContext 上下文
     * @return 影响条数
     * @see MybatisSQLProvider#save(BaseSQLCmdContext, ProviderContext, DbType) (SQLCmdInsertContext, ProviderContext)
     */
    @InsertProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.SAVE_NAME)
    int $save(SQLCmdInsertContext insertContext);


    /**
     * @param insertContext 上下文
     * @return 影响条数
     * @see MybatisSQLProvider#save(BaseSQLCmdContext, ProviderContext, DbType) (SQLCmdInsertContext, ProviderContext)
     */
    @InsertProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.SAVE_NAME)
    int $saveEntity(EntityInsertContext insertContext);


    /**
     * @param insertContext 上下文
     * @return 影响条数
     * @see MybatisSQLProvider#save(BaseSQLCmdContext, ProviderContext, DbType) (SQLCmdInsertContext, ProviderContext)
     */
    @InsertProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.SAVE_NAME)
    int $saveModel(ModelInsertContext insertContext);


    /**
     * @param updateContext 上下文
     * @return 修改的条数
     * @see MybatisSQLProvider#update(SQLCmdUpdateContext, ProviderContext, DbType)
     */
    @UpdateProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.UPDATE_NAME)
    int $update(SQLCmdUpdateContext updateContext);

    /**
     * @param deleteContext 上下文
     * @return 删除的条数
     * @see MybatisSQLProvider#delete(SQLCmdDeleteContext, ProviderContext, DbType)
     */
    @UpdateProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.DELETE_NAME)
    int $delete(SQLCmdDeleteContext deleteContext);

    /**
     * 列表查询
     *
     * @param queryContext 上下文
     * @return 返回查询的结果
     * @see MybatisSQLProvider#cmdCount(SQLCmdCountQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.QUERY_NAME)
    <T> List<T> $list(SQLCmdQueryContext queryContext);

    /**
     * 游标查询
     *
     * @param queryContext 上下文
     * @return 返回游标
     * @see MybatisSQLProvider#cmdQuery(SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.QUERY_NAME)
    <T> Cursor<T> $cursor(SQLCmdQueryContext queryContext);

    /**
     * count查询
     *
     * @param queryContext 上下文
     * @return 返回count数
     * @see MybatisSQLProvider#cmdCount(SQLCmdCountQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.COUNT_NAME)
    Integer $count(SQLCmdCountQueryContext queryContext);

    /**
     * count查询 - 从query中
     *
     * @param queryContext 上下文
     * @return 返回count数
     * @see MybatisSQLProvider#countFromQuery(SQLCmdCountFromQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.QUERY_COUNT_NAME)
    Integer $countFromQuery(SQLCmdCountFromQueryContext queryContext);

    /**
     * 将结果转成map
     *
     * @param queryContext 查询上下文
     * @param <K>          指定返回map的key的属性
     * @param <V>          指定返回map的value的类型
     * @return map
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.QUERY_NAME)
    <K, V> Map<K, V> $mapWithKey(MapKeySQLCmdQueryContext queryContext);


    /**
     * 动态查询 返回单个
     *
     * @param updateContext 上下文
     * @return 返回修改结果
     * @see MybatisSQLProvider#updateAndReturning (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.UPDATE_AND_RETURNING_NAME, affectData = true)
    <R> R $updateAndGet(SQLCmdUpdateContext updateContext, RowBounds rowBounds);

    /**
     * 动态查询 返回修改后多个记录
     *
     * @param updateContext 上下文
     * @return 返回修改结果
     * @see MybatisSQLProvider#updateAndReturning (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.UPDATE_AND_RETURNING_NAME, affectData = true)
    <R> List<R> $updateAndList(SQLCmdUpdateContext updateContext);


    /**
     * 动态删除 返回单个删除记录
     *
     * @param deleteContext 上下文
     * @return 返回修改结果
     * @see MybatisSQLProvider#deleteAndReturning (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.DELETE_AND_RETURNING_NAME, affectData = true)
    <R> R $deleteAndReturning(SQLCmdDeleteContext deleteContext, RowBounds rowBounds);

    /**
     * 动态删除 返回多个删除记录
     *
     * @param deleteContext 上下文
     * @return 返回修改结果
     * @see MybatisSQLProvider#deleteAndReturning (SQLCmdQueryContext, ProviderContext, DbType)
     */
    @SelectProvider(type = MybatisSQLProvider.class, method = MybatisSQLProvider.DELETE_AND_RETURNING_NAME, affectData = true)
    <R> List<R> $deleteAndReturningList(SQLCmdDeleteContext deleteContext);
}
