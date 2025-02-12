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

package db.sql.api.impl.cmd.executor;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IWithQuery;
import db.sql.api.cmd.struct.Joins;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.DatasetField;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.basic.WithQueryDataset;
import db.sql.api.impl.cmd.condition.Exists;
import db.sql.api.impl.cmd.condition.In;
import db.sql.api.impl.cmd.struct.*;
import db.sql.api.impl.cmd.struct.query.*;
import db.sql.api.impl.tookit.SqlConst;

public abstract class AbstractWithQuery<SELF extends AbstractWithQuery<SELF, CMD_FACTORY>, CMD_FACTORY extends CmdFactory> extends AbstractSubQuery<SELF, CMD_FACTORY>
        implements IWithQuery<SELF,
        Table,
        TableField,
        WithQueryDataset,
        DatasetField,
        Cmd,
        Object,
        CMD_FACTORY,
        ConditionChain,
        With,
        WithRecursive,
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
        > {

    protected String alias;

    private WithRecursive recursive;

    public AbstractWithQuery(CMD_FACTORY $) {
        super($);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public WithQueryDataset asTable(String alias) {
        return new WithQueryDataset(this, alias);
    }

    @Override
    public WithRecursive getRecursive() {
        return recursive;
    }

    @Override
    public SELF recursive(String... params) {
        this.recursive = new WithRecursive(params);
        return (SELF) this;
    }


    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (parent instanceof In || parent instanceof Exists || parent instanceof With) {
            return super.sql(module, parent, context, sqlBuilder);
        }
        return sqlBuilder.append(SqlConst.BLANK).append(this.getAlias());
    }
}

