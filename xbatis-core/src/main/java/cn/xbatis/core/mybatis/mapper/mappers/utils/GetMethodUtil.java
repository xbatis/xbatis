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

package cn.xbatis.core.mybatis.mapper.mappers.utils;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.util.QueryUtil;
import cn.xbatis.core.sql.util.SelectClassUtil;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.function.Consumer;

public final class GetMethodUtil {


    public static <T, V> V getVOById(BasicMapper basicMapper, TableInfo tableInfo, Class<V> returnType, Serializable id) {
        Where where = WhereUtil.create(tableInfo, w -> WhereUtil.appendIdWhere(w, tableInfo, id));
        BaseQuery<?, T> query = QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> QueryUtil.fillQueryDefault(q, tableInfo, null));
        query.setReturnType(returnType);
        return basicMapper.$getById(new SQLCmdQueryContext(query), new RowBounds(0, 1));
    }

    public static <T> T getById(BasicMapper basicMapper, TableInfo tableInfo, Serializable id, Getter<T>[] selectFields) {
        Where where = WhereUtil.create(tableInfo, w -> WhereUtil.appendIdWhere(w, tableInfo, id));
        BaseQuery<?, T> query = QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> QueryUtil.fillQueryDefault(q, tableInfo, selectFields));
        return basicMapper.$getById(new SQLCmdQueryContext(query), new RowBounds(0, 1));
    }

    public static <T> T getById(BasicMapper basicMapper, TableInfo tableInfo, Class<T> targetType, Serializable id) {
        Where where = WhereUtil.create(tableInfo, w -> WhereUtil.appendIdWhere(w, tableInfo, id));
        BaseQuery<?, T> query = QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> {
            SelectClassUtil.select(q, targetType);
            QueryUtil.fillQueryDefault(q, tableInfo);
            q.setReturnType(targetType);
        });
        return basicMapper.$getById(new SQLCmdQueryContext(query), new RowBounds(0, 1));
    }

    public static <T> T get(BasicMapper basicMapper, TableInfo tableInfo, Where where, Getter<T>[] selectFields) {
        return basicMapper.get(QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> QueryUtil.fillQueryDefault(q, tableInfo, selectFields)));
    }

    public static <T> T get(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer) {
        return get(basicMapper, tableInfo, WhereUtil.create(tableInfo, consumer), null);
    }

    public static <T> T get(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer, Getter<T>[] selectFields) {
        return basicMapper.get(QueryUtil.buildNoOptimizationQuery(tableInfo, WhereUtil.create(tableInfo, consumer),
                q -> QueryUtil.fillQueryDefault(q, tableInfo, selectFields)));
    }

}
