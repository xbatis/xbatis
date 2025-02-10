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
import db.sql.api.impl.tookit.Lists;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class NotIn extends BaseCondition<NotIn, Cmd, List<Cmd>> {

    private final Cmd key;

    private final List<Cmd> values = new ArrayList<>();

    public NotIn(Cmd key) {
        super(SqlConst.NOT_IN);
        this.key = key;
    }

    public NotIn(Cmd key, Cmd value) {
        this(key);
        this.add(value);
    }

    public NotIn(Cmd key, Cmd... values) {
        this(key);
        this.add(values);
    }

    public NotIn add(Cmd value) {
        this.values.add(value);
        return this;
    }

    public NotIn add(Cmd... values) {
        Lists.merge(this.values, values);
        return this;
    }

    public NotIn add(Collection<? extends Cmd> values) {
        this.values.addAll(values);
        return this;
    }

    public NotIn add(Serializable... values) {
        for (Serializable value : values) {
            if (Objects.isNull(value)) {
                continue;
            }
            this.add(Methods.cmd(value));
        }
        return this;
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder = key.sql(module, this, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(getOperator()).append(SqlConst.BLANK).append(SqlConst.BRACKET_LEFT);
        sqlBuilder = CmdUtils.join(module, this, context, sqlBuilder, this.values, SqlConst.DELIMITER);
        sqlBuilder = sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BLANK);
        return sqlBuilder;
    }

    @Override
    public Cmd getField() {
        return this.key;
    }

    @Override
    public List<Cmd> getValue() {
        return this.values;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key, this.values);
    }
}
