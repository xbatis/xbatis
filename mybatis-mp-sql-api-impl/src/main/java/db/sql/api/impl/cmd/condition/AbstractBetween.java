/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.io.Serializable;

public abstract class AbstractBetween<T extends AbstractBetween<T>> extends BaseCondition<T, Cmd, Cmd[]> {

    private final Cmd field;

    private final Cmd[] value;

    public AbstractBetween(char[] operator, Cmd field, Cmd value, Cmd value2) {
        super(operator);
        this.field = field;
        this.value = new Cmd[]{value, value2};
    }

    public AbstractBetween(Cmd key, Cmd value1, Cmd value2) {
        this(SqlConst.BETWEEN, key, value1, value2);
    }

    public AbstractBetween(Cmd key, Serializable value1, Serializable value2) {
        this(key, Methods.cmd(value1), Methods.cmd(value2));
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        field.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(getOperator());
        value[0].sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.AND);
        value[1].sql(module, this, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public Cmd getField() {
        return this.field;
    }

    @Override
    public Cmd[] getValue() {
        return this.value;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.field, this.value);
    }
}
