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

package db.sql.api.cmd.executor;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.ICmdFactory;
import db.sql.api.cmd.basic.*;
import db.sql.api.cmd.executor.method.*;
import db.sql.api.cmd.struct.*;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;
import db.sql.api.cmd.struct.query.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface IQuery<SELF extends IQuery
        , TABLE extends ITable<TABLE, TABLE_FIELD>
        , TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>
        , COLUMN extends Cmd,
        V,

        CMD_FACTORY extends ICmdFactory<TABLE, TABLE_FIELD>,
        CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>,

        WITH extends IWith<WITH>,
        SELECT extends ISelect<SELECT>,
        FROM extends IFrom,
        JOIN extends IJoin<JOIN, ON, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        ON extends IOn<ON, JOIN, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        JOINS extends Joins<JOIN>,
        WHERE extends IWhere<WHERE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        GROUPBY extends IGroupBy<GROUPBY, COLUMN>,
        HAVING extends IHaving<HAVING>,
        ORDERBY extends IOrderBy<ORDERBY>,
        LIMIT extends ILimit<LIMIT>,
        FORUPDATE extends IForUpdate<FORUPDATE>,
        IUNION extends IUnion
        >
        extends IWithMethod<SELF>,
        ISelectMethod<SELF, TABLE, TABLE_FIELD, COLUMN>,
        IFromMethod<SELF, TABLE, TABLE_FIELD>,
        IJoinMethod<SELF, JOIN, ON>,
        IWhereMethod<SELF, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        IGroupByMethod<SELF, TABLE, TABLE_FIELD, COLUMN>,
        IHavingMethod<SELF, TABLE, TABLE_FIELD, HAVING>,
        IOrderByMethod<SELF, TABLE, TABLE_FIELD, COLUMN>,
        ILimitMethod<SELF>,
        IForUpdateMethod<SELF>,
        IUnionMethod<SELF>,
        IExecutor<SELF, TABLE, TABLE_FIELD> {

    CMD_FACTORY $();

    WITH $with(IWithQuery withQuery);

    SELECT $select();

    FROM $from(IDataset<?, ?> table);

    WHERE $where();

    GROUPBY $groupBy();

    HAVING $having();

    ORDERBY $orderBy();

    LIMIT $limit();

    FORUPDATE $forUpdate();

    <T> SELF fetchEnable(Getter<T> getter, Boolean enable);

    <T> SELF fetchFilter(Getter<T> getter, Consumer<WHERE> where);

    @Override
    default SELF with(IWithQuery... withQuerys) {
        for (IWithQuery withQuery : withQuerys) {
            $with(withQuery);
        }
        return (SELF) this;
    }

    @Override
    default SELF select(Cmd column) {
        $select().select(column);
        return (SELF) this;
    }

    @Override
    default SELF select(Class entity, int storey) {
        return this.select($().allField($().table(entity, storey)));
    }

    @Override
    default SELF selectDistinct() {
        $select().distinct();
        return (SELF) this;
    }

    @Override
    default <T> SELF selectIgnore(Getter<T> column, int storey) {
        this.$select().selectIgnore($().field(column, storey));
        return (SELF) this;
    }

    default SELF from(IDataset<?, ?> table) {
        $from(table);
        return (SELF) this;
    }

    @Override
    default SELF from(IDataset<?, ?>... tables) {
        for (IDataset<?, ?> table : tables) {
            $from(table);
        }
        return (SELF) this;
    }

    default SELF where(Consumer<WHERE> whereConsumer) {
        whereConsumer.accept($where());
        return (SELF) this;
    }

    @Override
    default SELF groupBy(COLUMN column) {
        $groupBy().groupBy(column);
        return (SELF) this;
    }

    @Override
    default SELF groupBy(COLUMN... columns) {
        $groupBy().groupBy(columns);
        return (SELF) this;
    }

    @Override
    default SELF groupBy(List<COLUMN> columns) {
        $groupBy().groupBy(columns);
        return (SELF) this;
    }

    @Override
    default SELF having(Consumer<HAVING> consumer) {
        consumer.accept($having());
        return (SELF) this;
    }

    @Override
    default SELF havingAnd(ICondition condition) {
        $having().and(condition);
        return (SELF) this;
    }

    @Override
    default SELF havingOr(ICondition condition) {
        $having().or(condition);
        return (SELF) this;
    }

    @Override
    default SELF orderBy(IOrderByDirection orderByDirection, Cmd column) {
        $orderBy().orderBy(orderByDirection, column);
        return (SELF) this;
    }

    @Override
    default SELF limit(int limit) {
        return this.limit(0, limit);
    }

    @Override
    default SELF limit(int offset, int limit) {
        $limit().set(offset, limit);
        return (SELF) this;
    }

    boolean removeLimit();

    @Override
    default SELF forUpdate(boolean wait) {
        $forUpdate().setWait(wait);
        return (SELF) this;
    }

    SELECT getSelect();

    WHERE getWhere();

    FROM getFrom();

    JOINS getJoins();

    GROUPBY getGroupBy();

    ORDERBY getOrderBy();

    HAVING getHaving();

    LIMIT getLimit();

    FORUPDATE getForUpdate();

    Unions getUnions();

    @Override
    default CONDITION_CHAIN conditionChain() {
        return $where().conditionChain();
    }

    Map<String, Consumer<WHERE>> getFetchFilters();

    Map<String, Boolean> getFetchEnables();

}
