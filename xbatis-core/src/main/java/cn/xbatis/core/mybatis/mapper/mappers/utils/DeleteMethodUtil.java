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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.executor.Delete;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.DbType;
import db.sql.api.cmd.basic.SQL1;
import db.sql.api.impl.cmd.struct.Where;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public final class DeleteMethodUtil {

    public static int deleteById(BasicMapper basicMapper, TableInfo tableInfo, Serializable id) {
        return delete(basicMapper, tableInfo, WhereUtil.create(tableInfo, w -> WhereUtil.appendIdWhere(w, tableInfo, id)));
    }

    public static int deleteByIds(BasicMapper basicMapper, TableInfo tableInfo, Serializable[] ids) {
        return delete(basicMapper, tableInfo, WhereUtil.create(tableInfo, w -> WhereUtil.appendIdsWhere(w, tableInfo, ids)));
    }

    public static <ID extends Serializable> int deleteByIds(BasicMapper basicMapper, TableInfo tableInfo, Collection<ID> ids) {
        return delete(basicMapper, tableInfo, WhereUtil.create(tableInfo, w -> WhereUtil.appendIdsWhere(w, tableInfo, ids)));
    }

    public static <E> int delete(BasicMapper basicMapper, TableInfo tableInfo, E entity) {
        if (Objects.isNull(entity)) {
            return 0;
        }
        if (tableInfo.getIdFieldInfos().isEmpty()) {
            throw new RuntimeException(tableInfo.getType().getName() + " has no id");
        }
        if (tableInfo.getType() != entity.getClass()) {
            throw new IllegalArgumentException();
        }

        return delete(basicMapper, tableInfo, WhereUtil.create(tableInfo, w -> {
            WhereUtil.appendIdWhereWithEntity(w, tableInfo, entity);
            WhereUtil.appendVersionWhere(w, tableInfo, entity);
        }));
    }

    public static <E> int delete(BasicMapper basicMapper, TableInfo tableInfo, Collection<E> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        int cnt = 0;
        for (E entity : list) {
            cnt += delete(basicMapper, tableInfo, entity);
        }
        return cnt;
    }

    public static int delete(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer) {
        return delete(basicMapper, tableInfo, WhereUtil.create(tableInfo, consumer));
    }

    public static int delete(BasicMapper basicMapper, TableInfo tableInfo, Where where) {
        if (!where.hasContent()) {
            throw new RuntimeException("delete has no where condition content ");
        }
        if (LogicDeleteUtil.isNeedLogicDelete(tableInfo)) {
            //逻辑删除处理
            return LogicDeleteUtil.logicDelete(basicMapper, tableInfo, where);
        }
        Delete delete = new Delete(where);
        delete.delete(tableInfo.getType());
        delete.from(tableInfo.getType());
        return basicMapper.delete(delete);
    }

    public static int deleteAll(BasicMapper basicMapper, TableInfo tableInfo) {
        return delete(basicMapper, tableInfo, where -> where.eq(SQL1.INSTANCE, 1));
    }

    /**
     * TRUNCATE TABLE
     *
     * @param basicMapper
     * @param tableInfo
     * @return 影响数量
     */
    public static int truncate(BasicMapper basicMapper, TableInfo tableInfo) {
        MpTable mpTable = new MpTable(tableInfo);
        XbatisGlobalConfig.getSQLListeners().stream().filter(Objects::nonNull).forEach(listener -> {
            listener.onTruncate(mpTable);
        });
        return basicMapper.dbAdapt(selectorCall -> selectorCall.when(DbType.DB2, (dbType) -> {
            return basicMapper.execute("TRUNCATE TABLE " + mpTable.getName(dbType) + " IMMEDIATE");
        }).when(DbType.SQLITE, (dbType) -> {
            int cnt = basicMapper.execute("DELETE FROM " + mpTable.getName(dbType));
            basicMapper.execute("UPDATE SQLITE_SEQUENCE SET SEQ = 0 WHERE name = '" + mpTable.getName() + "'");
            return cnt;
        }).otherwise((dbType) -> {
            return basicMapper.execute("TRUNCATE TABLE " + mpTable.getName(dbType));
        }));
    }
}
