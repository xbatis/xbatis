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
import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.executor.IDelete;
import db.sql.api.cmd.struct.Joins;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.struct.*;
import db.sql.api.impl.cmd.struct.delete.DeleteTable;
import db.sql.api.impl.cmd.struct.ext.ExistsExt;
import db.sql.api.impl.cmd.struct.ext.InExt;
import db.sql.api.impl.cmd.struct.ext.NotExistsExt;
import db.sql.api.impl.cmd.struct.ext.NotInExt;
import db.sql.api.impl.cmd.struct.query.Returning;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDelete<SELF extends AbstractDelete<SELF, CMD_FACTORY>, CMD_FACTORY extends CmdFactory>
        extends BaseExecutor<SELF, CMD_FACTORY>
        implements IDelete<SELF,
        Table,
        TableField,
        Cmd,
        Object,
        ConditionChain,
        DeleteTable,
        From, Join, On, Where, Returning>
        , ExistsExt<SELF, AbstractSubQuery<?, ?>>,
        NotExistsExt<SELF, AbstractSubQuery<?, ?>>,
        InExt<SELF, AbstractSubQuery<?, ?>>,
        NotInExt<SELF, AbstractSubQuery<?, ?>> {

    protected final ConditionFactory conditionFactory;
    protected final CMD_FACTORY $;
    protected DeleteTable deleteTable;
    protected From from;
    protected Where where;
    protected Joins joins;
    protected Returning returning;

    public AbstractDelete(CMD_FACTORY $) {
        this.$ = $;
        this.conditionFactory = $.createConditionFactory();
    }

    public AbstractDelete(Where where) {
        this.$ = (CMD_FACTORY) where.getConditionFactory().getCmdFactory();
        this.conditionFactory = where.getConditionFactory();
        this.where = where;
        this.append(where);
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

    @Override
    public CMD_FACTORY $() {
        return $;
    }

    protected void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts) {
        int i = 0;
        cmdSorts.put(DeleteTable.class, i += 10);
        cmdSorts.put(From.class, i += 10);
        cmdSorts.put(Joins.class, i += 10);
        cmdSorts.put(Where.class, i += 10);
        cmdSorts.put(Returning.class, i += 10);
    }

    @Override
    public DeleteTable $delete(IDataset... tables) {
        if (this.deleteTable == null) {
            this.deleteTable = new DeleteTable(tables);
        }
        this.append(this.deleteTable);
        for (IDataset table : tables) {
            this.getSQLListeners().stream().forEach(item -> item.onDelete(this, table));
        }
        return this.deleteTable;
    }

    @Override
    public SELF delete(Class entity, Consumer<Table> consumer) {
        Table table = this.$.table(entity);
        this.delete(table);
        if (Objects.nonNull(consumer)) {
            consumer.accept(table);
        }
        return (SELF) this;
    }

    @Override
    public SELF delete(Class... entities) {
        int length = entities.length;
        Table[] tables = new Table[length];
        for (int i = 0; i < length; i++) {
            Class entity = entities[i];
            tables[i] = $.table(entity, 1);
        }
        return this.delete(tables);
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
        Table table = this.$.table(entity, storey);
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
        Table $secondTable = $.table(secondTable, secondTableStorey);
        Joins<Join> js = getJoins();
        if (js != null && js.getJoins().stream().anyMatch(i -> i.getSecondTable() == $secondTable)) {
            return this.join(mode, $secondTable, $.table(mainTable, mainTableStorey), consumer);
        } else {
            return this.join(mode, $.table(mainTable, mainTableStorey), $secondTable, consumer);
        }
    }

    @Override
    public SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<On> consumer) {
        return this.join(mode, this.$.table(mainTable, mainTableStorey), secondTable, consumer);
    }

    @Override
    public <E> AbstractSubQuery<?, ?> buildExistsOrNotExistsSubQuery(Class<E> entity, BiConsumer<SELF, AbstractSubQuery<?, ?>> consumer) {
        return this.conditionFactory.buildExistsOrNotExistsSubQuery(this, entity, (BiConsumer) consumer);
    }

    @Override
    public <E1, E2> AbstractSubQuery<?, ?> buildExistsOrNotExistsSubQuery(Getter<E1> sourceGetter, int sourceStorey, Getter<E2> targetGetter, BiConsumer<SELF, AbstractSubQuery<?, ?>> consumer) {
        return this.conditionFactory.buildExistsOrNotExistsSubQuery(this, sourceGetter, sourceStorey, targetGetter, (BiConsumer) consumer);
    }

    @Override
    public <E> AbstractSubQuery<?, ?> buildInOrNotInSubQuery(Getter<E> selectGetter, BiConsumer<SELF, AbstractSubQuery<?, ?>> consumer) {
        return this.conditionFactory.buildInOrNotInSubQuery(this, selectGetter, (BiConsumer) consumer);
    }

    @Override
    public <E1, E2> AbstractSubQuery<?, ?> buildInOrNotInSubQuery(Getter<E2> selectGetter, Getter<E1> sourceEqGetter, int sourceStorey, Getter<E2> targetEqGetter, BiConsumer<SELF, AbstractSubQuery<?, ?>> consumer) {
        return this.conditionFactory.buildInOrNotInSubQuery(this, selectGetter, sourceEqGetter, sourceStorey, targetEqGetter, (BiConsumer) consumer);
    }

    public Returning $returning() {
        if (returning == null) {
            returning = new Returning();
            this.append(returning);
        }
        return returning;
    }

    @Override
    public SELF returning(Cmd column) {
        $returning().returning(column);
        return (SELF) this;
    }

    @Override
    public <T> SELF returning(int storey, Getter<T>... columns) {
        $returning().returning($.fields(storey, columns));
        return (SELF) this;
    }

    @Override
    public <T> SELF returning(Getter<T> column, int storey, Function<TableField, Cmd> f) {
        if (f != null) {
            $returning().returning(f.apply($.field(column, storey)));
        } else {
            $returning().returning($.field(column, storey));
        }
        return (SELF) this;
    }

    @Override
    public <T> SELF returningIgnore(Getter<T> column, int storey) {
        $returning().returningIgnore($.field(column, storey));
        return (SELF) this;
    }

    @Override
    public SELF returning(Class entity, int storey) {
        this.returning($().allField($().table(entity, storey)));
        return (SELF) this;
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
    public SELF join(JoinMode mode, IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<On> consumer) {
        $join(mode, mainTable, this.$(secondTable), consumer);
        return (SELF) this;
    }

    @Override
    public SELF join(JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<On> consumer) {
        $join(mode, mainTable, secondTable, consumer);
        return (SELF) this;
    }


    public DeleteTable getDeleteTable() {
        return deleteTable;
    }

    public From getFrom() {
        return from;
    }

    public Joins getJoins() {
        return joins;
    }

    public Where getWhere() {
        return where;
    }

}
