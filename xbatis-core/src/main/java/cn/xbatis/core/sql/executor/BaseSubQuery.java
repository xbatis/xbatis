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

import cn.xbatis.core.XbatisConfig;
import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.util.SelectClassUtil;
import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.cmd.executor.AbstractSubQuery;
import db.sql.api.impl.cmd.struct.Where;

import java.util.List;
import java.util.Map;

public abstract class BaseSubQuery<Q extends BaseSubQuery<Q>> extends AbstractSubQuery<Q, MybatisCmdFactory> {

    private final String alias;

    public BaseSubQuery(String alias) {
        super(new MybatisCmdFactory("st"));
        this.alias = alias;
    }

    public BaseSubQuery(String alias, Where where) {
        super(where);
        this.alias = alias;
    }

    @Override
    protected void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts) {
        super.initCmdSorts(cmdSorts);
        cmdSorts.put(cn.xbatis.core.sql.executor.Where.class, cmdSorts.get(db.sql.api.impl.cmd.struct.Where.class));
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Q as(String alias) {
        throw new RuntimeException("not support");
    }

    @Override
    public final Q select(Class entity, int storey) {
        SelectClassUtil.select(this, entity, storey);
        return (Q) this;
    }

    @Override
    @SafeVarargs
    public final Q select(int storey, Class... entities) {
        SelectClassUtil.select(this, storey, entities);
        return (Q) this;
    }

    @Override
    public List<SQLListener> getSQLListeners() {
        return XbatisConfig.getSQLListeners();
    }

    /**************以下为去除警告************/


    @Override
    @SafeVarargs
    public final Q select(Class... entities) {
        return super.select(entities);
    }

    @Override
    @SafeVarargs
    public final Q select(Cmd... cmds) {
        return super.select(cmds);
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
    public final Q from(Class... entities) {
        return super.from(entities);
    }

    @Override
    @SafeVarargs
    public final Q from(int storey, Class... entities) {
        return super.from(storey, entities);
    }

    @Override
    @SafeVarargs
    public final <T> Q groupBy(Getter<T>... columns) {
        return super.groupBy(columns);
    }

    @Override
    @SafeVarargs
    public final Q groupBy(Cmd... cmds) {
        return super.groupBy(cmds);
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
    public final Q orderBy(Cmd... cmds) {
        return super.orderBy(cmds);
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
    public final Q orderByDesc(Cmd... cmds) {
        return super.orderByDesc(cmds);
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
