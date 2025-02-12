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

package db.sql.api.impl.cmd.condition;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class IsNotNull extends BaseCondition<IsNotNull, Cmd, NULL> {

    private final Cmd field;

    public IsNotNull(Cmd field) {
        super(SqlConst.IS_NOT);
        this.field = field;
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        field.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(getOperator());
        NULL.NULL.sql(module, this, context, sqlBuilder);
        return sqlBuilder;
    }


    @Override
    public Cmd getField() {
        return field;
    }

    @Override
    public NULL getValue() {
        return NULL.NULL;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.field);
    }
}
