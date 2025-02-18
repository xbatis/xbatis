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
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;
import org.apache.ibatis.cursor.Cursor;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Consumer;

public final class CursorMethodUtil {

    private static <T> Cursor<T> cursorByIds(BasicMapper basicMapper, TableInfo tableInfo, Getter<T>[] selectFields, Consumer<Where> whereConsumer) {
        return cursor(basicMapper, tableInfo, WhereUtil.create(tableInfo, whereConsumer), selectFields);
    }

    public static <T> Cursor<T> cursorByIds(BasicMapper basicMapper, TableInfo tableInfo, Serializable[] ids, Getter<T>[] selectFields) {
        return cursorByIds(basicMapper, tableInfo, selectFields, where -> WhereUtil.appendIdsWhere(where, tableInfo, ids));
    }

    public static <T, ID extends Serializable> Cursor<T> cursorByIds(BasicMapper basicMapper, TableInfo tableInfo, Collection<ID> ids, Getter<T>[] selectFields) {
        return cursorByIds(basicMapper, tableInfo, selectFields, where -> WhereUtil.appendIdsWhere(where, tableInfo, ids));
    }

    public static <T> Cursor<T> cursor(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer) {
        return cursor(basicMapper, tableInfo, consumer, null);
    }

    public static <T> Cursor<T> cursor(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer, Getter<T>[] selectFields) {
        return cursor(basicMapper, tableInfo, WhereUtil.create(tableInfo, consumer), selectFields);
    }

    public static <T> Cursor<T> cursor(BasicMapper basicMapper, TableInfo tableInfo, Where where, Getter<T>[] selectFields) {
        return basicMapper.cursor(QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> QueryUtil.fillQueryDefault(q, tableInfo, selectFields)));
    }

    public static <T> Cursor<T> cursorAll(BasicMapper basicMapper, TableInfo tableInfo) {
        return basicMapper.cursor(QueryUtil.buildNoOptimizationQuery(tableInfo, q -> QueryUtil.fillQueryDefault(q, tableInfo, null)));
    }
}
