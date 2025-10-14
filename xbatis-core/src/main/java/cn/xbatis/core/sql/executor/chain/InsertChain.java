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

package cn.xbatis.core.sql.executor.chain;

import cn.xbatis.core.mybatis.mapper.BaseMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Query;
import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.impl.cmd.basic.TableField;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class InsertChain extends BaseInsert<InsertChain> {

    private final Map<Getter<?>, Object> insertSelectFields = new HashMap<>();

    protected BaseMapper mapper;

    protected Class<?> entityType;

    protected InsertChain() {

    }

    public InsertChain(MybatisMapper<?> mapper) {
        this.mapper = mapper;
    }

    public InsertChain(BaseMapper mapper, Class<?> entityType) {
        this.mapper = mapper;
        this.entityType = entityType;
    }

    public static InsertChain of(MybatisMapper<?> mapper) {
        return new InsertChain(mapper);
    }

    public static InsertChain of(BaseMapper mapper, Class<?> entityType) {
        return new InsertChain(mapper, entityType);
    }

    /**
     * 非特殊情况 请使用of静态方法
     * 使用此方法后 后续执行查询需调用一次withMapper(Mapper)方法
     *
     * @return 自己
     */
    public static InsertChain create() {
        return new InsertChain();
    }

    public Class<?> getEntityType() {
        if (entityType != null) {
            return entityType;
        }
        if (mapper instanceof MybatisMapper) {
            this.entityType = ((MybatisMapper) mapper).getEntityType();
        } else {
            throw new RuntimeException("you need specify entityType");
        }

        return entityType;
    }

    public <T> InsertChain insertSelect(Getter<T> field, Cmd select) {
        this.insertSelectFields.put(field, select);
        return this;
    }

    public <T, T2> InsertChain insertSelect(Getter<T> field, Getter<T2> select) {
        this.insertSelectFields.put(field, select);
        return this;
    }

    public <T, T2> InsertChain insertSelect(Getter<T> field, Getter<T2> select, Function<TableField, Cmd> fun) {
        this.insertSelectFields.put(field, new SelectGetterFun<>(select, fun));
        return this;
    }

    public <T> InsertChain insertSelect(Getter<T> field, GetterField[] select, Function<TableField[], Cmd> fun) {
        this.insertSelectFields.put(field, new SelectGetterFieldsFun(select, fun));
        return this;
    }

    private void buildSelectQuery() {
        if (!insertSelectFields.isEmpty()) {
            List<Getter<?>> fields = new ArrayList<>();
            IQuery selectQuery = this.getInsertSelect().getSelectQuery();
            for (Map.Entry<Getter<?>, Object> entry : insertSelectFields.entrySet()) {
                fields.add(entry.getKey());
                if (entry.getValue() instanceof SelectGetterFun) {
                    SelectGetterFun<?> selectGetterFun = (SelectGetterFun<?>) entry.getValue();
                    selectQuery.select(selectGetterFun.field, selectGetterFun.fun);
                } else if (entry.getValue() instanceof SelectGetterFieldsFun) {
                    SelectGetterFieldsFun selectGetterFieldsFun = (SelectGetterFieldsFun) entry.getValue();
                    selectQuery.select(selectGetterFieldsFun.fields, selectGetterFieldsFun.fun);
                } else if (entry.getValue() instanceof Cmd) {
                    selectQuery.select((Cmd) entry.getValue());
                } else {
                    selectQuery.select((Getter<?>) entry.getValue());
                }
            }
            this.fields(fields.toArray(new Getter[0]));
            this.fromSelect(selectQuery);
            insertSelectFields.clear();
        }
    }

    public InsertChain insertSelectQuery(Consumer<Query<?>> consumer) {
        if (!insertSelectFields.isEmpty()) {
            Query<?> selectQuery = Query.create();
            this.fromSelect(selectQuery);
            if (db.sql.api.impl.tookit.Objects.nonNull(consumer)) {
                consumer.accept(selectQuery);
            }
        }
        return this;
    }

    private void setDefault() {
        if (this.getInsertTable() == null) {
            //自动设置实体类
            this.insert(getEntityType());
        }
    }

    private void checkAndSetMapper(BaseMapper mapper) {
        if (Objects.isNull(this.mapper)) {
            this.mapper = mapper;
            return;
        }
        if (this.mapper == mapper) {
            return;
        }
        throw new RuntimeException(" the mapper is already set, can't use another mapper");
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 操作目标实体类的mapper
     * @return 自己
     */
    public InsertChain withMapper(MybatisMapper<?> mapper) {
        this.checkAndSetMapper(mapper);
        return this;
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 一般都是BasicMapper
     * @return 自己
     */
    public InsertChain withMapper(BaseMapper mapper, Class<?> entityType) {
        this.checkAndSetMapper(mapper);
        this.entityType = entityType;
        return this;
    }

    /**
     * 执行
     *
     * @return
     */
    public int execute() {
        this.setDefault();
        return mapper.save(this);
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        this.selectorExecute(context.getDbType());
        this.buildSelectQuery();
        return super.sql(module, parent, context, sqlBuilder);
    }

    private static class SelectGetterFun<T> {

        public final Getter<T> field;

        public final Function<TableField, Cmd> fun;

        public SelectGetterFun(Getter<T> field, Function<TableField, Cmd> fun) {
            this.field = field;
            this.fun = fun;
        }
    }

    private static class SelectGetterFieldsFun {

        public final GetterField[] fields;

        public final Function<TableField[], Cmd> fun;

        public SelectGetterFieldsFun(GetterField[] fields, Function<TableField[], Cmd> fun) {
            this.fields = fields;
            this.fun = fun;
        }
    }
}
