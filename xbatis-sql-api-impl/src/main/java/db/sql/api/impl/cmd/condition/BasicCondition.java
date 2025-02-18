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

package db.sql.api.impl.cmd.condition;


import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.tookit.CmdUtils;

public abstract class BasicCondition<T extends BasicCondition<T>> extends BaseCondition<T, Cmd, Cmd> {

    protected Cmd field;

    protected Cmd value;

    public BasicCondition(char[] operator, Cmd field, Cmd value) {
        super(operator);
        this.field = field;
        this.value = value;
    }

    @Override
    public Cmd getField() {
        return field;
    }

    @Override
    public Cmd getValue() {
        return value;
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder = field.sql(module, this, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(getOperator());
        sqlBuilder = value.sql(module, this, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.field, this.value);
    }
}
