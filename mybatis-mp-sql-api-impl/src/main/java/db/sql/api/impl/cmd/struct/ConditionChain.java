/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package db.sql.api.impl.cmd.struct;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.ConditionBlock;
import db.sql.api.impl.cmd.basic.Connector;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ConditionChain implements IConditionChain<ConditionChain, TableField, Cmd, Object>, ICondition {

    private final ConditionFactory conditionFactory;

    private final ConditionChain parent;
    private List<ConditionBlock> conditionBlocks;
    private Connector connector = Connector.AND;

    public ConditionChain(ConditionFactory conditionFactory) {
        this(conditionFactory, null);
    }

    public ConditionChain(ConditionFactory conditionFactory, ConditionChain parent) {
        this.conditionFactory = conditionFactory;
        this.parent = parent;
    }

    @Override
    public ConditionChain setIgnoreNull(boolean bool) {
        conditionFactory.setIgnoreNull(bool);
        return this;
    }

    @Override
    public ConditionChain setIgnoreEmpty(boolean bool) {
        conditionFactory.setIgnoreEmpty(bool);
        return this;
    }

    @Override
    public ConditionChain setStringTrim(boolean bool) {
        conditionFactory.setStringTrim(bool);
        return this;
    }

    @Override
    public ConditionChain and(ICondition condition) {
        this.and();
        if (Objects.nonNull(condition)) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain or(ICondition condition) {
        this.or();
        if (Objects.nonNull(condition)) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public boolean hasContent() {
        return conditionBlocks != null && !conditionBlocks.isEmpty();
    }

    @Override
    public ConditionChain newInstance() {
        return this.conditionFactory.newConditionChain(this);
    }

    protected void appendCondition(Connector connector, ICondition condition) {
        if (condition == null) {
            return;
        }
        if (conditionBlocks == null) {
            conditionBlocks = new ArrayList<>(4);
        }
        this.conditionBlocks.add(new ConditionBlock(connector, condition));
    }

    public List<ConditionBlock> getConditionBlocks() {
        return conditionBlocks;
    }

    @Override
    public ConditionChain and() {
        this.connector = Connector.AND;
        return this;
    }

    @Override
    public ConditionChain or() {
        this.connector = Connector.OR;
        return this;
    }

    @Override
    public <T> ConditionChain and(boolean when, Getter<T> column, int storey, Function<TableField, ICondition> f) {
        this.and();
        if (!when) {
            return this;
        }
        return this.and(f.apply(this.conditionFactory.getCmdFactory().field(column, storey)));
    }

    @Override
    public <T> ConditionChain or(boolean when, Getter<T> column, int storey, Function<TableField, ICondition> f) {
        this.or();
        if (!when) {
            return this;
        }
        return this.or(f.apply(this.conditionFactory.getCmdFactory().field(column, storey)));
    }

    @Override
    public ConditionChain and(boolean when, GetterField[] getterFields, Function<TableField[], ICondition> f) {
        if (!when) {
            return this;
        }
        return this.and(f.apply(this.conditionFactory.getCmdFactory().fields(getterFields)));
    }

    @Override
    public ConditionChain or(boolean when, GetterField[] getterFields, Function<TableField[], ICondition> f) {
        if (!when) {
            return this;
        }
        return this.or(f.apply(this.conditionFactory.getCmdFactory().fields(getterFields)));
    }


    @Override
    public <T> ConditionChain empty(boolean when, Getter<T> column, int storey) {
        ICondition condition = conditionFactory.empty(when, column, storey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain empty(Cmd column) {
        ICondition condition = conditionFactory.empty(column);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notEmpty(boolean when, Getter<T> column, int storey) {
        ICondition condition = conditionFactory.notEmpty(when, column, storey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notEmpty(Cmd column) {
        ICondition condition = conditionFactory.notEmpty(column);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain eq(Cmd column, Object value) {
        ICondition condition = conditionFactory.eq(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain ne(Cmd column, Object value) {
        ICondition condition = conditionFactory.ne(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain gt(Cmd column, Object value) {
        ICondition condition = conditionFactory.gt(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain gte(Cmd column, Object value) {
        ICondition condition = conditionFactory.gte(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain lt(Cmd column, Object value) {
        ICondition condition = conditionFactory.lt(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain lte(Cmd column, Object value) {
        ICondition condition = conditionFactory.lte(column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain between(Cmd column, Object value, Object value2) {
        ICondition condition = conditionFactory.between(column, value, value2);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notBetween(Cmd column, Object value, Object value2) {
        ICondition condition = conditionFactory.notBetween(column, value, value2);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain isNull(Cmd column) {
        ICondition condition = conditionFactory.isNull(column);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain isNotNull(Cmd column) {
        ICondition condition = conditionFactory.isNotNull(column);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain like(LikeMode mode, Cmd column, String value) {
        ICondition condition = conditionFactory.like(mode, column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notLike(LikeMode mode, Cmd column, String value) {
        ICondition condition = conditionFactory.notLike(mode, column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain iLike(LikeMode mode, Cmd column, String value) {
        ICondition condition = conditionFactory.iLike(mode, column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notILike(LikeMode mode, Cmd column, String value) {
        ICondition condition = conditionFactory.notILike(mode, column, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain between(boolean when, Getter<T> column, int storey, Object value, Object value2) {
        ICondition condition = conditionFactory.between(when, column, storey, value, value2);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain eq(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.eq(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain eq(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.eq(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain gt(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.gt(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain gt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.gt(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain gte(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.gte(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain gte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.gte(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain like(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        ICondition condition = conditionFactory.like(when, mode, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain iLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        ICondition condition = conditionFactory.iLike(when, mode, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        ICondition condition = conditionFactory.notLike(when, mode, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notILike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        ICondition condition = conditionFactory.notILike(when, mode, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }


    @Override
    public <T> ConditionChain lt(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.lt(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain lt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.lt(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain lte(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.lte(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain lte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.lte(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain ne(boolean when, Getter<T> column, int storey, Object value) {
        ICondition condition = conditionFactory.ne(when, column, storey, value);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T, T2> ConditionChain ne(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        ICondition condition = conditionFactory.ne(when, column, columnStorey, value, valueStorey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notBetween(boolean when, Getter<T> column, int storey, Object value, Object value2) {
        ICondition condition = conditionFactory.notBetween(when, column, storey, value, value2);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain isNotNull(boolean when, Getter<T> column, int storey) {
        ICondition condition = conditionFactory.isNotNull(when, column, storey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain isNull(boolean when, Getter<T> column, int storey) {
        ICondition condition = conditionFactory.isNull(when, column, storey);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain in(Cmd cmd, IQuery query) {
        ICondition condition = conditionFactory.in(cmd, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain in(Cmd cmd, Serializable... values) {
        ICondition condition = conditionFactory.in(cmd, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain in(Cmd cmd, Collection<? extends Serializable> values) {
        ICondition condition = conditionFactory.in(cmd, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain in(boolean when, Getter<T> column, int storey, IQuery query) {
        ICondition condition = conditionFactory.in(when, column, storey, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain in(boolean when, Getter<T> column, int storey, Serializable[] values) {
        ICondition condition = conditionFactory.in(when, column, storey, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain in(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        ICondition condition = conditionFactory.in(when, column, storey, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain exists(boolean when, IQuery query) {
        ICondition condition = conditionFactory.exists(when, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notExists(boolean when, IQuery query) {
        ICondition condition = conditionFactory.notExists(when, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notIn(Cmd cmd, IQuery query) {
        ICondition condition = conditionFactory.notIn(cmd, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notIn(Cmd cmd, Serializable... values) {
        ICondition condition = conditionFactory.notIn(cmd, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public ConditionChain notIn(Cmd cmd, Collection<? extends Serializable> values) {
        ICondition condition = conditionFactory.notIn(cmd, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notIn(boolean when, Getter<T> column, int storey, IQuery query) {
        ICondition condition = conditionFactory.notIn(when, column, storey, query);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notIn(boolean when, Getter<T> column, int storey, Serializable[] values) {
        ICondition condition = conditionFactory.notIn(when, column, storey, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public <T> ConditionChain notIn(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        ICondition condition = conditionFactory.notIn(when, column, storey, values);
        if (condition != null) {
            this.appendCondition(this.connector, condition);
        }
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (conditionBlocks == null || conditionBlocks.isEmpty()) {
            return sqlBuilder;
        }
        if ((!(parent instanceof Where) && !(parent instanceof On)) || this.parent != null) {
            sqlBuilder.append(SqlConst.BLANK).append(SqlConst.BRACKET_LEFT);
        }
        boolean isFirst = true;
        for (ConditionBlock conditionBlock : this.conditionBlocks) {
            if (conditionBlock.getCondition() instanceof ConditionChain) {
                ConditionChain conditionChain = (ConditionChain) conditionBlock.getCondition();
                if (!conditionChain.hasContent()) {
                    continue;
                }
            }
            if (!isFirst) {
                sqlBuilder.append(SqlConst.BLANK).append(conditionBlock.getConnector()).append(SqlConst.BLANK);
            }
            conditionBlock.getCondition().sql(module, this, context, sqlBuilder);
            isFirst = false;
        }
        if ((!(parent instanceof Where) && !(parent instanceof On)) || this.parent != null) {
            sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BLANK);
        }

        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.conditionBlocks);
    }
}
