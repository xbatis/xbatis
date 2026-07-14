/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package db.sql.api.impl.cmd.condition;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.Connector;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.basic.ConditionBlock;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConditionChain implements ICondition {

    private List<ConditionBlock> conditionBlocks=new ArrayList<>();

    public ConditionChain and(ICondition condition) {
        conditionBlocks.add(new ConditionBlock(Connector.AND, condition));
        return this;
    }
    public ConditionChain and(ICondition... conditions) {
        for (ICondition condition:conditions) {
            conditionBlocks.add(new ConditionBlock(Connector.AND, condition));
        }
        return this;
    }

    public ConditionChain or(ICondition condition) {
        conditionBlocks.add(new ConditionBlock(Connector.OR, condition));
        return this;
    }

    public ConditionChain or(ICondition... conditions) {
        for (ICondition condition:conditions) {
            conditionBlocks.add(new ConditionBlock(Connector.OR, condition));
        }
        return this;
    }

    public ConditionChain andNested(Consumer<ConditionChain> chain) {
        ConditionChain chains=new ConditionChain();
        chain.accept(chains);
        if (!chains.conditionBlocks.isEmpty()) {
            conditionBlocks.add(new ConditionBlock(Connector.AND, chains));
        }
        return this;
    }

    public ConditionChain orNested(Consumer<ConditionChain> chain) {
        ConditionChain chains=new ConditionChain();
        chain.accept(chains);
        if (!chains.conditionBlocks.isEmpty()) {
            conditionBlocks.add(new ConditionBlock(Connector.OR, chains));
        }
        return this;
    }


    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (conditionBlocks.isEmpty()){
            return sqlBuilder;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (ConditionBlock conditionBlock : this.conditionBlocks) {
            if (conditionBlock.getCondition() instanceof ConditionChain) {
                ConditionChain conditionChain = (ConditionChain) conditionBlock.getCondition();
                if (conditionChain.conditionBlocks.isEmpty()) {
                    continue;
                }

            }
            if (!isFirst) {
                sb = sb.append(SqlConst.BLANK).append(conditionBlock.getConnector()).append(SqlConst.BLANK);
            }

            if (conditionBlock.getCondition() instanceof ConditionChain) {
                sb.append(SqlConst.BRACKET_LEFT);
            }

            sb = conditionBlock.getCondition().sql(module, this, context, sb);
            if (conditionBlock.getCondition() instanceof ConditionChain) {
                sb.append(SqlConst.BRACKET_RIGHT);
            }
            isFirst = false;
        }
        return sqlBuilder.append(sb);
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd,this.conditionBlocks);
    }
}
