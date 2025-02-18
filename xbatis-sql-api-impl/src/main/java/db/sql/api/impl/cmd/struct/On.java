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
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.IOn;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class On implements IOn<On, Join, Table, TableField, Cmd, Object, ConditionChain> {

    private final ConditionFactory conditionFactory;

    private final Join join;

    private final ConditionChain conditionChain;

    private ConditionChain extConditionChain;

    public On(ConditionFactory conditionFactory, Join join) {
        this.conditionFactory = conditionFactory;
        this.join = join;
        conditionChain = conditionFactory.newConditionChain(null);
    }

    @Override
    public Join getJoin() {
        return join;
    }

    @Override
    public ConditionChain conditionChain() {
        return conditionChain;
    }

    public ConditionChain extConditionChain() {
        if (this.extConditionChain == null) {
            this.extConditionChain = conditionFactory.newConditionChain(null);
        }
        return extConditionChain;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (!conditionChain.hasContent()) {
            throw new RuntimeException("ON has no on conditions");
        }
        sqlBuilder.append(SqlConst.ON);

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

    public ConditionFactory getConditionFactory() {
        return conditionFactory;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.conditionChain);
    }
}
