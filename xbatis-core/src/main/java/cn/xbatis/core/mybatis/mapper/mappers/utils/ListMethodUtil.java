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
import cn.xbatis.core.sql.util.QueryUtil;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.DbType;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.Objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class ListMethodUtil {

    private static <T> List<T> listByIds(BasicMapper basicMapper, TableInfo tableInfo, Getter<T>[] selectFields, Consumer<Where> whereConsumer) {
        return list(basicMapper, tableInfo, null, WhereUtil.create(tableInfo, whereConsumer), selectFields);
    }

    public static <T> List<T> listByIds(BasicMapper basicMapper, TableInfo tableInfo, Serializable[] ids, Getter<T>[] selectFields) {
        return listByIds(basicMapper, tableInfo, selectFields, where -> WhereUtil.appendIdsWhere(where, tableInfo, ids));
    }

    public static <T, ID extends Serializable> List<T> listByIds(BasicMapper basicMapper, TableInfo tableInfo, Collection<ID> ids, Getter<T>[] selectFields) {
        return listByIds(basicMapper, tableInfo, selectFields, where -> WhereUtil.appendIdsWhere(where, tableInfo, ids));
    }

    public static <T> List<T> list(BasicMapper basicMapper, TableInfo tableInfo, Integer limit, Consumer<Where> consumer) {
        return list(basicMapper, tableInfo, limit, consumer, null);
    }

    public static <T> List<T> list(BasicMapper basicMapper, TableInfo tableInfo, Integer limit, Consumer<Where> consumer, Getter<T>[] selectFields) {
        return list(basicMapper, tableInfo, limit, WhereUtil.create(tableInfo, consumer), selectFields);
    }

    public static <T> List<T> list(BasicMapper basicMapper, TableInfo tableInfo, Integer limit, Where where, Getter<T>[] selectFields) {
        return basicMapper.list(QueryUtil.buildNoOptimizationQuery(tableInfo, where, query -> {
            QueryUtil.fillQueryDefault(query, tableInfo, selectFields);
            if (Objects.nonNull(limit)) {
                query.limit(limit);
                query.dbAdapt(((q, selector) -> {
                    selector.when(DbType.SQL_SERVER, () -> {
                        if (Objects.isNull(q.getOrderBy())) {
                            if (tableInfo.getIdFieldInfos().isEmpty()) {
                                query.$select().top(limit);
                                query.removeLimit();
                            } else {
                                tableInfo.getIdFieldInfos().forEach(item -> q.orderBy(q.$(tableInfo.getType(), item.getField().getName())));
                            }
                        }
                    }).otherwise();
                }));
            }
        }));
    }

    public static <T> List<T> listAll(BasicMapper basicMapper, TableInfo tableInfo) {
        return basicMapper.list(QueryUtil.buildNoOptimizationQuery(tableInfo, q -> QueryUtil.fillQueryDefault(q, tableInfo, null)));
    }
}
