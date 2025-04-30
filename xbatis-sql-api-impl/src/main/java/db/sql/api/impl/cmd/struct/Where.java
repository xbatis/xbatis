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

package db.sql.api.impl.cmd.struct;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.struct.IWhere;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;
import java.util.function.Function;

public class Where implements IWhere<Where, TableField, Cmd, Object, ConditionChain> {

    private final ConditionFactory conditionFactory;

    private ConditionChain conditionChain;

    private ConditionChain extConditionChain;

    public Where(ConditionFactory conditionFactory) {
        this.conditionFactory = conditionFactory;
    }

    public boolean hasContent() {
        return Objects.nonNull(conditionChain) && conditionChain.hasContent();
    }

    @Override
    public ConditionChain conditionChain() {
        if (this.conditionChain == null) {
            this.conditionChain = conditionFactory.newConditionChain(null);
        }
        return conditionChain;
    }

    public ConditionChain extConditionChain() {
        if (this.extConditionChain == null) {
            this.extConditionChain = conditionFactory.newConditionChain(null);
        }
        return extConditionChain;
    }

    public ConditionFactory getConditionFactory() {
        return conditionFactory;
    }

    public ConditionChain getConditionChain() {
        return conditionChain;
    }

    @Override
    public <T> Where and(Getter<T> column, int storey, Function<TableField, ICondition> f) {
        conditionChain().and(column, storey, f);
        return this;
    }

    @Override
    public <T> Where or(Getter<T> column, int storey, Function<TableField, ICondition> f) {
        conditionChain().or(column, storey, f);
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if ((extConditionChain == null || !extConditionChain.hasContent()) && (this.conditionChain == null || !conditionChain.hasContent())) {
            return sqlBuilder;
        }
        sqlBuilder.append(SqlConst.WHERE);

        if (extConditionChain != null && extConditionChain.hasContent() && this.conditionChain != null && conditionChain.hasContent()) {
            //2的 ConditionChain 都不为空 分别一括号包裹
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.conditionChain.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            sqlBuilder.append(SqlConst.AND);
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            this.extConditionChain.sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);

        } else if (extConditionChain != null && extConditionChain.hasContent()) {
            this.extConditionChain.sql(module, this, context, sqlBuilder);
        } else {
            this.conditionChain.sql(module, this, context, sqlBuilder);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.conditionChain) || CmdUtils.contain(cmd, this.extConditionChain);
    }

    /**
     * 给表设置别名
     *
     * @param entity 实体
     * @param as     别名
     * @param <T>    实体类类型
     * @return 自己
     */
    public <T> Where tableAs(Class<T> entity, String as) {
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
    public <T> Where tableAs(Class<T> entity, int storey, String as) {
        conditionFactory.getCmdFactory().table(entity, storey).as(as);
        return this;
    }
}
