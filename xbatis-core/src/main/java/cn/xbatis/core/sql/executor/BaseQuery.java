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

package cn.xbatis.core.sql.executor;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.executor.statement.Fetchable;
import cn.xbatis.core.mybatis.executor.statement.Timeoutable;
import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.util.SelectClassUtil;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.cmd.executor.AbstractQuery;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.OptimizeOptions;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseQuery<Q extends BaseQuery<Q, T>, T> extends AbstractQuery<Q, MybatisCmdFactory> implements Timeoutable<Q>, Fetchable<Q> {

    protected final OptimizeOptions optimizeOptions = new OptimizeOptions();
    protected Class returnType;
    protected Consumer<T> onRowEvent;
    protected Integer timeout;
    protected Integer fetchSize;
    protected Integer fetchDirection;

    public BaseQuery() {
        this(new MybatisCmdFactory());
    }

    public BaseQuery(MybatisCmdFactory mybatisCmdFactory) {
        super(mybatisCmdFactory);
    }

    public BaseQuery(Where where) {
        super(where);
    }

    public Q optimizeOptions(Consumer<OptimizeOptions> consumer) {
        consumer.accept(this.optimizeOptions);
        return (Q) this;
    }

    public OptimizeOptions getOptimizeOptions() {
        return optimizeOptions;
    }

    public Class getReturnType() {
        return returnType;
    }

    public <T2, Q2 extends BaseQuery<Q2, T2>> BaseQuery<Q2, T2> setReturnType(Class<T2> returnType) {
        this.returnType = returnType;
        return (BaseQuery<Q2, T2>) this;
    }

    public <T2, Q2 extends BaseQuery<Q2, T2>> BaseQuery<Q2, T2> setReturnType(Class<T2> returnType, Consumer<T2> consumer) {
        return (BaseQuery<Q2, T2>) this.setReturnType(returnType).onRowEvent(consumer);
    }

    public Q onRowEvent(Consumer<T> consumer) {
        this.onRowEvent = consumer;
        return (Q) this;
    }

    public Consumer<T> getOnRowEvent() {
        return onRowEvent;
    }

    @Override
    public Q timeout(Integer timeout) {
        this.timeout = timeout;
        return (Q) this;
    }

    @Override
    public Q fetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
        return (Q) this;
    }

    /**
     * 追加非null，非空的字段值的条件
     *
     * @param object 对象类上必须有实体类注解或@ConditionTarget
     * @return Q
     * @see cn.xbatis.db.annotations.ConditionTarget @ConditionTarget 条件目标注解
     * @see cn.xbatis.db.annotations.Condition @Condition条件注解
     */
    public Q where(Object object) {
        return WhereUtil.where((Q) this, object);
    }

    /**
     * 给表设置别名
     *
     * @param entity 实体
     * @param as     别名
     * @param <T>    实体类类型
     * @return 自己
     */
    public <T> Q tableAs(Class<T> entity, String as) {
        return tableAs(entity, 1, as);
    }

    /**
     * 给表设置别名
     *
     * @param entity 实体
     * @param storey 层级
     * @param as     别名
     * @param <T>    实体类类型
     * @return 自己
     */
    public <T> Q tableAs(Class<T> entity, int storey, String as) {
        conditionFactory.getCmdFactory().table(entity, storey).as(as);
        return (Q) this;
    }

    /**
     * use like fetchDirection(ResultSet.TYPE_FORWARD_ONLY)
     *
     * @param direction direction value:all in ResultSet
     * @return
     */
    @Override
    public Q fetchDirection(Integer direction) {
        this.fetchDirection = direction;
        return (Q) this;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public Integer getFetchSize() {
        return fetchSize;
    }

    @Override
    public Integer getFetchDirection() {
        return fetchDirection;
    }

    @Override
    protected void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts) {
        super.initCmdSorts(cmdSorts);
        cmdSorts.put(cn.xbatis.core.sql.executor.Where.class, cmdSorts.get(Where.class));
    }

    @Override
    public Q select(Class entity, int storey) {
        SelectClassUtil.select(this, entity, storey);
        return (Q) this;
    }

    @Override
    public List<SQLListener> getSQLListeners() {
        return XbatisGlobalConfig.getSQLListeners();
    }

    private <T2> SubQuery buildExistsSubQuery(Class<T2> entity, BiConsumer<Q, SubQuery> consumer) {
        SubQuery subQuery = this.$().createSubQuery();
        consumer.accept((Q) this, subQuery);
        if (subQuery.getSelect() == null || subQuery.getSelect().getSelectField().isEmpty()) {
            subQuery.select1();
        }
        if (subQuery.getFrom() == null) {
            subQuery.from(entity);
        }
        return subQuery;
    }

    public <T2> Q exists(Class<T2> entity, BiConsumer<Q, SubQuery> consumer) {
        return this.exists(true, entity, consumer);
    }

    public <T2> Q exists(boolean when, Class<T2> entity, BiConsumer<Q, SubQuery> consumer) {
        if (!when) {
            return (Q) this;
        }
        return this.exists(this.buildExistsSubQuery(entity, consumer));
    }


    public <T2> Q notExists(Class<T2> entity, BiConsumer<Q, SubQuery> consumer) {
        return this.notExists(true, entity, consumer);
    }

    public <T2> Q notExists(boolean when, Class<T2> entity, BiConsumer<Q, SubQuery> consumer) {
        if (!when) {
            return (Q) this;
        }
        return this.notExists(this.buildExistsSubQuery(entity, consumer));
    }

    /**************以下为去除警告************/
    @Override
    public Q select(int storey, Class... entities) {
        SelectClassUtil.select(this, storey, entities);
        return (Q) this;
    }

    @Override
    @SafeVarargs
    public final <T> Q select(int storey, Getter<T>... columns) {
        return super.select(storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q select(Getter<T>... columns) {
        return super.select(1, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q select(boolean when, Getter<T>... columns) {
        return super.select(when, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q select(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.select(dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q select(boolean when, int storey, Getter<T>... columns) {
        return super.select(when, storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q select(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.select(when, dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q selectIgnore(Getter<T>... columns) {
        return super.selectIgnore(columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q selectIgnore(int storey, Getter<T>... columns) {
        return super.selectIgnore(storey, columns);
    }

    @Override
    @SafeVarargs
    public final Q from(IDataset<?, ?>... tables) {
        return super.from(tables);
    }

    @Override
    @SafeVarargs
    public final <T> Q groupBy(Getter<T>... columns) {
        return super.groupBy(columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q groupBy(int storey, Getter<T>... columns) {
        return super.groupBy(storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q groupBy(boolean when, Getter<T>... columns) {
        return super.groupBy(when, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q groupBy(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.groupBy(dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q groupBy(boolean when, int storey, Getter<T>... columns) {
        return super.groupBy(when, storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q groupBy(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.groupBy(when, dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(Getter<T>... columns) {
        return super.orderBy(columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(int storey, Getter<T>... columns) {
        return super.orderBy(storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(boolean when, Getter<T>... columns) {
        return super.orderBy(when, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderBy(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.orderBy(dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderBy(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.orderBy(when, dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(boolean when, int storey, Getter<T>... columns) {
        return super.orderBy(when, storey, columns);
    }

    @Override
    @SafeVarargs
    public final Q orderBy(IOrderByDirection orderByDirection, Cmd... cmds) {
        return super.orderBy(orderByDirection, cmds);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(IOrderByDirection orderByDirection, Getter<T>... columns) {
        return super.orderBy(orderByDirection, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(IOrderByDirection orderByDirection, int storey, Getter<T>... columns) {
        return super.orderBy(orderByDirection, storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(boolean when, IOrderByDirection orderByDirection, Getter<T>... columns) {
        return super.orderBy(when, orderByDirection, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderBy(IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, Getter<T>... columns) {
        return super.orderBy(dataset, orderByDirection, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderBy(boolean when, IOrderByDirection orderByDirection, int storey, Getter<T>... columns) {
        return super.orderBy(when, orderByDirection, storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderBy(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, IOrderByDirection orderByDirection, Getter<T>... columns) {
        return super.orderBy(when, dataset, orderByDirection, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderByDesc(Getter<T>... columns) {
        return super.orderByDesc(columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderByDesc(int storey, Getter<T>... columns) {
        return super.orderByDesc(storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderByDesc(boolean when, Getter<T>... columns) {
        return super.orderByDesc(when, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderByDesc(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.orderByDesc(dataset, columns);
    }

    @Override
    @SafeVarargs
    public final <T> Q orderByDesc(boolean when, int storey, Getter<T>... columns) {
        return super.orderByDesc(when, storey, columns);
    }

    @Override
    @SafeVarargs
    public final <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> Q orderByDesc(boolean when, IDataset<DATASET, DATASET_FIELD> dataset, Getter<T>... columns) {
        return super.orderByDesc(when, dataset, columns);
    }

    /**************以上为去除警告************/
}

