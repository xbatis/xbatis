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

package db.sql.api.impl.cmd.executor;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.ColumnField;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.IColumnField;
import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.*;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.executor.IWithQuery;
import db.sql.api.cmd.struct.Joins;
import db.sql.api.cmd.struct.query.Unions;
import db.sql.api.cmd.struct.query.Withs;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.*;
import db.sql.api.impl.cmd.struct.*;
import db.sql.api.impl.cmd.struct.query.*;
import db.sql.api.impl.tookit.QuerySQLUtil;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;
import db.sql.api.tookit.LambdaUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractQuery<SELF extends AbstractQuery<SELF, CMD_FACTORY>,
        CMD_FACTORY extends CmdFactory>

        extends BaseExecutor<SELF, CMD_FACTORY>
        implements IQuery<SELF,
        Table,
        TableField,
        Cmd,
        Object,
        CMD_FACTORY,
        ConditionChain,
        With,
        Select,
        From,
        Join,
        On,
        Joins<Join>,
        Where,
        GroupBy,
        Having,
        OrderBy,
        Limit,
        ForUpdate,
        Union
        >, Cmd {

    protected final ConditionFactory conditionFactory;

    protected final CMD_FACTORY $;

    protected Select select;

    protected Withs withs;

    protected From from;

    protected Where where;

    protected Joins joins;

    protected GroupBy groupBy;

    protected Having having;

    protected OrderBy orderBy;

    protected Limit limit;

    protected ForUpdate forUpdate;

    protected Unions unions;

    protected Map<String, Consumer<Where>> fetchFilters;

    protected Map<String, Boolean> fetchEnables;

    public AbstractQuery(CMD_FACTORY $) {
        this.$ = $;
        this.conditionFactory = $.createConditionFactory();
    }

    public AbstractQuery(Where where) {
        this.$ = (CMD_FACTORY) where.getConditionFactory().getCmdFactory();
        this.conditionFactory = where.getConditionFactory();
        this.where = where;
        this.append(where);
    }

    @Override
    public CMD_FACTORY $() {
        return $;
    }

    public <T> TableField $(Getter<T> getter) {
        return this.$(getter, 1);
    }

    public <T> TableField $(Getter<T> getter, int storey) {
        return $().field(getter, storey);
    }

    public Table $(Class entityType) {
        return this.$(entityType, 1);
    }

    public Table $(Class entityType, int storey) {
        return $().table(entityType, storey);
    }

    public TableField $(Class entityType, String fieldName) {
        return this.$(entityType, fieldName, 1);
    }

    public TableField $(Class entityType, String fieldName, int storey) {
        return $().field(entityType, fieldName, storey);
    }

    protected <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD $(IDataset<DATASET, DATASET_FIELD> dataset, String columnName) {
        return this.$().field(dataset, columnName);
    }

    protected <E, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD $(IDataset<DATASET, DATASET_FIELD> dataset, Getter<E> getter) {
        return this.$().field(dataset, getter);
    }

    @Override
    public <T> SELF fetchFilter(Getter<T> getter, Consumer<Where> where) {
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(getter);
        String key = lambdaFieldInfo.getType().getName() + "." + lambdaFieldInfo.getName();
        if (Objects.isNull(fetchFilters)) {
            this.fetchFilters = new HashMap<>();
        }
        fetchFilters.put(key, where);
        return (SELF) this;
    }

    @Override
    public <T> SELF fetchEnable(Getter<T> getter, Boolean enable) {
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(getter);
        String key = lambdaFieldInfo.getType().getName() + "." + lambdaFieldInfo.getName();
        if (Objects.isNull(fetchEnables)) {
            this.fetchEnables = new HashMap<>();
        }
        fetchEnables.put(key, enable);
        return (SELF) this;
    }

    @Override
    public Map<String, Consumer<Where>> getFetchFilters() {
        return fetchFilters;
    }

    @Override
    public Map<String, Boolean> getFetchEnables() {
        return fetchEnables;
    }

    public void setFetchFilters(Map<String, Consumer<Where>> fetchFilters) {
        if (Objects.nonNull(this.fetchFilters)) {
            throw new RuntimeException("Can't call setFetchFilters when the this.fetchFilters has value");
        }
        this.fetchFilters = fetchFilters;
    }

    public void setFetchEnables(Map<String, Boolean> fetchEnables) {
        if (Objects.nonNull(this.fetchEnables)) {
            throw new RuntimeException("Can't call fetchEnables when the this.fetchEnables has value");
        }
        this.fetchEnables = fetchEnables;
    }

    @Override
    protected void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts) {
        int i = 0;
        cmdSorts.put(Withs.class, i += 10);
        cmdSorts.put(Select.class, i += 10);
        cmdSorts.put(From.class, i += 10);
        cmdSorts.put(Joins.class, i += 10);
        cmdSorts.put(Where.class, i += 10);
        cmdSorts.put(GroupBy.class, i += 10);
        cmdSorts.put(Having.class, i += 10);
        cmdSorts.put(OrderBy.class, i += 10);
        cmdSorts.put(Limit.class, i += 10);
        cmdSorts.put(ForUpdate.class, i += 10);
        cmdSorts.put(Unions.class, i += 10);
        cmdSorts.put(UnionsCmdLists.class, i += 10);
    }

    @Override
    public With $with(IWithQuery withQuery) {
        if (Objects.isNull(this.withs)) {
            this.withs = new Withs();
            this.append(this.withs);
        }
        With with = new With(withQuery);
        this.withs.add(with);
        return with;
    }

    @Override
    public Select $select() {
        if (select == null) {
            select = new Select();
            this.append(select);
        }
        return select;
    }

    @Override
    public SELF selectCount1() {
        this.select(new Count1());
        return (SELF) this;
    }

    @Override
    public SELF selectCountAll() {
        this.select(new CountAll());
        return (SELF) this;
    }

    @Override
    public SELF selectCount1(Consumer<ICount1<?>> consumer) {
        Count1 count = new Count1();
        consumer.accept(count);
        this.select(count);
        return (SELF) this;
    }

    @Override
    public SELF selectCountAll(Consumer<ICountAll<?>> consumer) {
        CountAll count = new CountAll();
        consumer.accept(count);
        this.select(count);
        return (SELF) this;
    }


    /**
     * select 子查询 列
     *
     * @param column 列
     * @param storey 列存储层级
     * @param <T>    列的实体类
     * @param f      转换函数
     * @return 自己
     */
    @Override
    public <T> SELF select(Getter<T> column, int storey, Function<TableField, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.select($.field(column, storey));
        }
        return this.select(f.apply($.field(column, storey)));
    }

    @Override
    public SELF select(GetterField[] getterFields, Function<TableField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.select($.fields(getterFields));
        }
        return this.select(f.apply($.fields(getterFields)));
    }

    @Override
    public <T> SELF select(int storey, Getter<T>... columns) {
        return this.select($.fields(storey, columns));
    }

    @Override
    public SELF select(String columnName) {
        return this.select(Methods.column(columnName));
    }


    @Override
    public SELF select(String columnName, Function<IDatasetField, Cmd> f) {
        return this.select(f.apply(Methods.column(columnName)));
    }

    /**
     * select 子查询 列
     *
     * @param dataset    子查询
     * @param columnName 列
     * @param f          转换函数
     * @return
     */
    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, Cmd> f) {
        return this.select(f.apply(this.$(dataset, columnName)));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.select(this.getDatasetFields(dataset, getterFields));
        }
        return this.select(this.apply(dataset, f, getterFields));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, String columnName) {
        return this.select(this.$(dataset, columnName));
    }

    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF select(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column, Function<DATASET_FIELD, Cmd> f) {
        return this.select(f.apply(this.$(dataset, column)));
    }

    @Override
    public From $from(IDataset table) {
        if (this.from == null) {
            from = new From();
            this.append(from);
        }
        this.from.append(table);
        this.getSQLListeners().stream().forEach(item -> item.onFrom(this, table));
        return from;
    }

    @Override
    public SELF from(Class entity, int storey, Consumer<Table> consumer) {
        Table table = $.table(entity, storey);
        this.from(table);
        if (Objects.nonNull(consumer)) {
            consumer.accept(table);
        }
        return (SELF) this;
    }

    @Override
    public Join $join(JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<On> onConsumer) {
        Join join = new Join(mode, mainTable, secondTable, (joinDataset -> new On(this.conditionFactory, joinDataset)));
        if (Objects.isNull(joins)) {
            joins = new Joins();
            this.append(joins);
        }
        joins.add(join);
        if (Objects.nonNull(onConsumer)) {
            onConsumer.accept(join.getOn());
        }
        this.getSQLListeners().stream().forEach(item -> item.onJoin(this, mode, mainTable, secondTable, join.getOn()));
        return join;
    }

    @Override
    public SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<On> consumer) {
        return this.join(mode, $.table(mainTable, mainTableStorey), $.table(secondTable, secondTableStorey), consumer);
    }

    @Override
    public SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<On> consumer) {
        return this.join(mode, $.table(mainTable, mainTableStorey), secondTable, consumer);
    }

    @Override
    public Where $where() {
        if (where == null) {
            where = new Where(this.conditionFactory);
            this.append(where);
        }
        return where;
    }

    @Override
    public <T> SELF and(Getter<T> column, int storey, Function<TableField, ICondition> f) {
        $where().and(column, storey, f);
        return (SELF) this;
    }

    @Override
    public <T> SELF or(Getter<T> column, int storey, Function<TableField, ICondition> f) {
        $where().or(column, storey, f);
        return (SELF) this;
    }


    @Override
    public SELF join(JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<On> consumer) {
        $join(mode, mainTable, secondTable, consumer);
        return (SELF) this;
    }

    @Override
    public GroupBy $groupBy() {
        if (groupBy == null) {
            groupBy = new GroupBy();
            this.append(groupBy);
        }
        return groupBy;
    }

    /**
     * groupBy 子查询 列
     *
     * @param column 列
     * @param storey 列存储层级
     * @param <T>    列的实体类
     * @param f      转换函数
     * @return 自己
     */
    @Override
    public <T> SELF groupBy(Getter<T> column, int storey, Function<TableField, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.groupBy($.field(column, storey));
        }
        return this.groupBy(f.apply($.field(column, storey)));
    }

    @Override
    public SELF groupBy(GetterField[] getterFields, Function<TableField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.groupBy($.fields(getterFields));
        }
        return this.groupBy(f.apply($.fields(getterFields)));
    }

    @Override
    public <T> SELF groupBy(int storey, Getter<T>... columns) {
        return this.groupBy($.fields(storey, columns));
    }

    @Override
    public SELF groupBy(String columnName) {
        return this.groupBy(Methods.column(columnName));
    }

    @Override
    public SELF groupBy(String columnName, Function<IDatasetField, Cmd> f) {
        return this.groupBy(f.apply(Methods.column(columnName)));
    }

    /**
     * groupBy 子查询 列
     *
     * @param dataset 子查询
     * @param column  列
     * @param <T>     列的实体类
     * @return
     */
    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF groupBy(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column) {
        return this.groupBy(this.$(dataset, column));
    }

    /**
     * groupBy 子查询 列
     *
     * @param dataset    子查询
     * @param columnName 列
     * @param f          转换函数
     * @return
     */
    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF groupBy(IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, Cmd> f) {
        return this.groupBy(f.apply(this.$(dataset, columnName)));
    }


    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF groupBy(IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.groupBy(this.getDatasetFields(dataset, getterFields));
        }
        return this.groupBy(this.apply(dataset, f, getterFields));
    }


    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF groupBy(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column, Function<DATASET_FIELD, Cmd> f) {
        return this.groupBy(f.apply(this.$(dataset, column)));
    }

    @Override
    public Having $having() {
        if (having == null) {
            having = new Having(this.$);
            this.append(having);
        }
        return having;
    }

    @Override
    public <T> SELF havingAnd(boolean when, Getter<T> column, int storey, Function<TableField, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingAnd(f.apply($.field(column, storey)));
    }

    @Override
    public <T> SELF havingOr(boolean when, Getter<T> column, int storey, Function<TableField, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(f.apply($.field(column, storey)));
    }

    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingAnd(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column, Function<DATASET_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingAnd(f.apply(this.$(dataset, column)));
    }

    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingOr(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column, Function<DATASET_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(f.apply(this.$(dataset, column)));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingAnd(IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, ICondition> f) {
        return this.havingAnd(f.apply(this.$(dataset, columnName)));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingOr(IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, ICondition> f) {
        return this.havingOr(f.apply(this.$(dataset, columnName)));
    }


    @Override
    public SELF havingAnd(boolean when, GetterField[] getterFields, Function<TableField[], ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingAnd(f.apply($.fields(getterFields)));
    }

    @Override
    public SELF havingOr(boolean when, GetterField[] getterFields, Function<TableField[], ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(f.apply($.fields(getterFields)));
    }

    private <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> IDatasetField[] getDatasetFields(IDataset<DATASET, DATASET_FIELD> dataset, IColumnField... columnFields) {
        IDatasetField[] datasetFields = new IDatasetField[columnFields.length];
        for (int i = 0; i < columnFields.length; i++) {
            IColumnField columnField = columnFields[i];
            if (columnField instanceof ColumnField) {
                datasetFields[i] = this.$(dataset, ((ColumnField) columnField).getColumnName());
            } else if (columnField instanceof GetterField) {
                datasetFields[i] = this.$(dataset, ((GetterField<?>) columnField).getGetter());
            } else {
                throw new RuntimeException("Not Supported");
            }
        }
        return datasetFields;
    }

    private <R, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> R apply(IDataset<DATASET, DATASET_FIELD> dataset, Function<IDatasetField[], R> f, IColumnField... columnFields) {
        return f.apply(getDatasetFields(dataset, columnFields));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingAnd(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingAnd(f.apply(this.$(dataset, columnName)));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingOr(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, String columnName, Function<DATASET_FIELD, ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(f.apply(this.$(dataset, columnName)));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingAnd(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingAnd(this.apply(dataset, f, getterFields));
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF havingOr(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, GetterField[] getterFields, Function<IDatasetField[], ICondition> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(this.apply(dataset, f, getterFields));
    }

    @Override
    public OrderBy $orderBy() {
        if (orderBy == null) {
            orderBy = new OrderBy();
            this.append(orderBy);
        }
        return orderBy;
    }

    @Override
    public IOrderByDirection ascOrderByDirection() {
        return OrderByDirection.ASC;
    }

    @Override
    public IOrderByDirection descOrderByDirection() {
        return OrderByDirection.DESC;
    }

    @Override
    public ForUpdate $forUpdate() {
        if (forUpdate == null) {
            forUpdate = new ForUpdate();
            this.append(forUpdate);
        }
        return forUpdate;
    }

    @Override
    public Limit $limit() {
        if (this.limit == null) {
            this.limit = new Limit(0, 0);
            this.append(this.limit);
        }
        return this.limit;
    }


    /**
     * orderBy 列
     *
     * @param column 列
     * @param storey 列存储层级
     * @param <T>    列的实体类
     * @param f      转换函数
     * @return 自己
     */
    @Override
    public <T> SELF orderBy(IOrderByDirection orderByDirection, Getter<T> column, int storey, Function<TableField, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, $.field(column, storey));
        }
        return this.orderBy(orderByDirection, f.apply($.field(column, storey)));
    }

    @Override
    public SELF orderBy(IOrderByDirection orderByDirection, GetterField[] getterFields, Function<TableField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, $.fields(getterFields));
        }
        return this.orderBy(orderByDirection, f.apply($.fields(getterFields)));
    }

    @Override
    public <T> SELF orderBy(IOrderByDirection orderByDirection, int storey, Getter<T>... columns) {
        return this.orderBy(orderByDirection, $.fields(storey, columns));
    }

    @Override
    public SELF orderBy(IOrderByDirection orderByDirection, String columnName) {
        return this.orderBy(orderByDirection, Methods.column(columnName));
    }

    @Override
    public SELF orderBy(IOrderByDirection orderByDirection, String columnName, Function<IDatasetField, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, Methods.column(columnName));
        }
        return this.orderBy(orderByDirection, f.apply(Methods.column(columnName)));
    }

    /**
     * orderBy 子查询 列
     *
     * @param dataset 子查询
     * @param column  列
     * @param f       转换函数
     * @param <T>     列的实体类
     * @return
     */
    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, Getter<T> column, Function<DATASET_FIELD, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, this.$(dataset, column));
        }
        return this.orderBy(orderByDirection, f.apply(this.$(dataset, column)));
    }

    /**
     * orderBy 子查询 列
     *
     * @param dataset    子查询
     * @param columnName 列
     * @param f          转换函数
     * @return
     */
    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, String columnName, Function<DATASET_FIELD, Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, this.$(dataset, columnName));
        }
        return this.orderBy(orderByDirection, f.apply(this.$(dataset, columnName)));
    }


    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, GetterField[] getterFields, Function<IDatasetField[], Cmd> f) {
        if (Objects.isNull(f)) {
            return this.orderBy(orderByDirection, this.getDatasetFields(dataset, getterFields));
        }
        return this.orderBy(orderByDirection, this.apply(dataset, f, getterFields));
    }


    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> SELF orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, String columnName) {
        return this.orderBy(orderByDirection, this.$(dataset, columnName));
    }


    public Unions $unions() {
        if (this.unions == null) {
            this.unions = new Unions();
            this.cmds.add(unions);
        }
        return this.unions;
    }

    @Override
    public SELF union(IQuery unionQuery) {
        $unions().add(new Union(unionQuery));
        return (SELF) this;
    }

    @Override
    public SELF unionAll(IQuery unionQuery) {
        $unions().add(new Union(SqlConst.UNION_ALL, unionQuery));
        return (SELF) this;
    }


    @Override
    public Select getSelect() {
        return this.select;
    }

    @Override
    public From getFrom() {
        return this.from;
    }

    @Override
    public Joins getJoins() {
        return this.joins;
    }

    @Override
    public Where getWhere() {
        return this.where;
    }

    @Override
    public GroupBy getGroupBy() {
        return this.groupBy;
    }

    @Override
    public Having getHaving() {
        return this.having;
    }

    @Override
    public OrderBy getOrderBy() {
        return this.orderBy;
    }

    @Override
    public Limit getLimit() {
        return this.limit;
    }

    @Override
    public boolean removeLimit() {
        if (Objects.isNull(getLimit())) {
            return false;
        }
        return this.cmds().remove(getLimit());
    }

    @Override
    public Unions getUnions() {
        return this.unions;
    }

    @Override
    public ForUpdate getForUpdate() {
        return forUpdate;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        this.selectorExecute(context.getDbType());
        if (this.limit == null) {
            List<Cmd> cmdList = cmds();
            if (cmdList == null || cmdList.isEmpty()) {
                return sqlBuilder;
            }
            List<Cmd> sortedCmds = this.sortedCmds();
            if (sortedCmds == null || sortedCmds.isEmpty()) {
                return sqlBuilder;
            }
            return CmdUtils.join(this, this, context, sqlBuilder, sortedCmds);
        }
        return QuerySQLUtil.buildQuerySQL(context, module, parent, this, sqlBuilder, this.sortedCmds());
    }
}

