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

package cn.xbatis.core.sql.util;

import cn.xbatis.core.db.reflect.Conditions;
import cn.xbatis.core.db.reflect.ModelInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.executor.Where;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.Model;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.executor.AbstractDelete;
import db.sql.api.impl.cmd.executor.AbstractQuery;
import db.sql.api.impl.cmd.executor.AbstractUpdate;
import db.sql.api.impl.cmd.struct.ConditionChain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

import static db.sql.api.impl.cmd.basic.TRUE.TRUE;

public final class WhereUtil {

    /**
     * 创建
     *
     * @return Where
     */
    public static Where create() {
        return Where.create();
    }

    /**
     * 创建和消费
     *
     * @param consumer
     * @return Where
     */
    public static Where create(Consumer<Where> consumer) {
        Where where = create();
        consumer.accept(where);
        return where;
    }

    /**
     * 一个安全where 包含 一个类似 1=1的 条件
     *
     * @return Where
     */
    public static Where safeWhere() {
        return (Where) create().and(TRUE);
    }

    public static Where create(TableInfo tableInfo) {
        return create(tableInfo, null);
    }

    public static Where create(TableInfo tableInfo, Consumer<db.sql.api.impl.cmd.struct.Where> consumer) {
        Where where = create();
        CmdFactory cmdFactory = where.getConditionFactory().getCmdFactory();
        ((MybatisCmdFactory) cmdFactory).cacheTableInfo(tableInfo);
        if (consumer != null) {
            consumer.accept(where);
        }
        return where;
    }

    /**
     * where 动态对象转条件
     *
     * @param object
     * @return Where
     */
    public static Where where(Object object) {
        return where(create(), object);
    }

    public static <T extends db.sql.api.impl.cmd.struct.Where> T where(T where, Object object) {
        if (object != null) {
            where(where.conditionChain(), object);
        }
        return where;
    }

    public static <F extends CmdFactory, Q extends AbstractQuery<Q, F>> Q where(Q query, Object object) {
        if (object != null) {
            where(query.$where().conditionChain(), object);
        }
        return query;
    }

    public static <F extends CmdFactory, Q extends AbstractUpdate<Q, F>> Q where(Q query, Object object) {
        if (object != null) {
            where(query.$where().conditionChain(), object);
        }
        return query;
    }

    public static <F extends CmdFactory, Q extends AbstractDelete<Q, F>> Q where(Q query, Object object) {
        if (object != null) {
            where(query.$where().conditionChain(), object);
        }
        return query;
    }

    public static void where(ConditionChain conditionChain, Object object) {
        if (object != null) {
            Conditions.get(object.getClass()).appendCondition(conditionChain, object);
        }
    }

    /**
     * 拼接id条件
     *
     * @param where
     * @param tableInfo
     * @param id
     */
    public static void appendIdWhere(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, Serializable id) {
        TableInfoUtil.checkId(tableInfo);
        CmdFactory $ = where.getConditionFactory().getCmdFactory();
        Objects.requireNonNull(id, "id can't be null");
        where.eq($.field(tableInfo.getType(), tableInfo.getSingleIdFieldInfo(true).getField().getName(), 1), id);
    }

    /**
     * 拼接id条件
     *
     * @param where
     * @param tableInfo
     * @param entity
     */
    public static void appendIdWhereWithEntity(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, Object entity) {
        CmdFactory $ = where.getConditionFactory().getCmdFactory();
        if (tableInfo.getIdFieldInfos().isEmpty()) {
            throw new RuntimeException(tableInfo.getType().getName() + " has no id");
        }

        tableInfo.getIdFieldInfos().forEach(item -> {
            Object id;
            try {
                id = item.getReadFieldInvoker().invoke(entity, null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Objects.requireNonNull(id, "id can't be null");
            where.eq($.field(tableInfo.getType(), item.getField().getName(), 1), id);
        });
    }

    /**
     * 拼接id条件
     *
     * @param where
     * @param modelInfo
     * @param model
     */
    public static void appendIdWhereWithModel(db.sql.api.impl.cmd.struct.Where where, ModelInfo modelInfo, Model<?> model) {
        if (modelInfo.getIdFieldInfos().isEmpty()) {
            throw new RuntimeException(modelInfo.getType().getName() + " has no id");
        }
        CmdFactory $ = where.getConditionFactory().getCmdFactory();
        modelInfo.getIdFieldInfos().forEach(item -> {
            Object id;
            try {
                id = item.getReadFieldInvoker().invoke(model, null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Objects.requireNonNull(id, "id can't be null");
            where.eq($.field(modelInfo.getTableInfo().getType(), item.getTableFieldInfo().getField().getName(), 1), id);
        });
    }

    /**
     * 使用 IN 拼接多个id条件
     *
     * @param where     where
     * @param tableInfo 表信息
     * @param ids
     */
    public static void appendIdsWhere(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, Serializable[] ids) {
        Objects.requireNonNull(ids, "id can't be null");
        for (Serializable id : ids) {
            Objects.requireNonNull(id, "id can't be null");
        }
        appendWhereWithIdTableField(where, tableInfo, idTableField -> where.in(idTableField, ids));
    }

    /**
     * 使用 IN 拼接多个id条件
     *
     * @param where     where
     * @param tableInfo 表信息
     * @param ids
     */
    public static <ID extends Serializable> void appendIdsWhere(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, Collection<ID> ids) {
        Objects.requireNonNull(ids, "id can't be null");
        ids.forEach(id -> Objects.requireNonNull(id, "id can't be null"));
        appendWhereWithIdTableField(where, tableInfo, idTableField -> where.in(idTableField, ids));
    }

    private static void appendWhereWithIdTableField(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, Consumer<TableField> consumer) {
        TableInfoUtil.checkId(tableInfo);
        consumer.accept(where.getConditionFactory().getCmdFactory().field(tableInfo.getType(), tableInfo.getSingleIdFieldInfo(true)
                .getField().getName(), 1));
    }
}
