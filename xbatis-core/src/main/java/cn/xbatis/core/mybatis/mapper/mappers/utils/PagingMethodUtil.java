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
import cn.xbatis.page.IPager;
import db.sql.api.DbType;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.Objects;

import java.util.function.Consumer;

public final class PagingMethodUtil {

    public static <T, P extends IPager<T>> P paging(BasicMapper basicMapper, TableInfo tableInfo, P pager, Consumer<Where> consumer) {
        return paging(basicMapper, tableInfo, pager, consumer, null);
    }

    public static <T, P extends IPager<T>> P paging(BasicMapper basicMapper, TableInfo tableInfo, P pager, Consumer<Where> consumer, Getter<T>[] selectFields) {
        return paging(basicMapper, tableInfo, pager, WhereUtil.create(tableInfo, consumer), selectFields);
    }

    public static <T, P extends IPager<T>> P paging(BasicMapper basicMapper, TableInfo tableInfo, P pager, Where where) {
        return paging(basicMapper, tableInfo, pager, where, null);
    }

    public static <T, P extends IPager<T>> P paging(BasicMapper basicMapper, TableInfo tableInfo, P pager, Where where, Getter<T>[] selectFields) {
        return basicMapper.paging(QueryUtil.buildQuery(tableInfo, where, query -> {
            QueryUtil.fillQueryDefault(query, tableInfo, selectFields);
            query.dbAdapt(((q, selector) -> {
                selector.when(DbType.SQL_SERVER, () -> {
                    if (Objects.isNull(q.getOrderBy())) {
                        if (tableInfo.getIdFieldInfos().isEmpty()) {
                            //没有主键 取第一个
                            q.orderBy(q.$(tableInfo.getType(), tableInfo.getTableFieldInfos().get(0).getField().getName()));
                        } else {
                            tableInfo.getIdFieldInfos().forEach(item -> q.orderBy(q.$(tableInfo.getType(), item.getField().getName())));
                        }
                    }
                }).otherwise();
            }));
        }), pager);
    }
}
