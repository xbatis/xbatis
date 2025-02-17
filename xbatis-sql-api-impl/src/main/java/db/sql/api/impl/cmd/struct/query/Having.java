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

package db.sql.api.impl.cmd.struct.query;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.struct.query.IHaving;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.Condition;
import db.sql.api.impl.cmd.basic.ConditionBlock;
import db.sql.api.impl.cmd.basic.Connector;
import db.sql.api.impl.cmd.struct.ConditionChain;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Having implements IHaving<Having> {

    private final CmdFactory cmdFactory;

    private final List<ConditionBlock> conditionBlocks = new ArrayList<>(4);

    public Having(CmdFactory cmdFactory) {
        this.cmdFactory = cmdFactory;
    }

    public Having and(Function<CmdFactory, Condition> f) {
        conditionBlocks.add(new ConditionBlock(Connector.AND, f.apply(this.cmdFactory)));
        return this;
    }

    @Override
    public Having and(ICondition condition) {
        conditionBlocks.add(new ConditionBlock(Connector.AND, condition));
        return this;
    }

    public Having or(Function<CmdFactory, Condition> f) {
        conditionBlocks.add(new ConditionBlock(Connector.OR, f.apply(this.cmdFactory)));
        return this;
    }

    @Override
    public Having or(ICondition condition) {
        conditionBlocks.add(new ConditionBlock(Connector.OR, condition));
        return this;
    }


    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (conditionBlocks.isEmpty()) {
            return sqlBuilder;
        }
        boolean isFirst = true;

        sqlBuilder.append(SqlConst.HAVING);
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
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.conditionBlocks);
    }
}
