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
import db.sql.api.cmd.struct.query.IReturning;
import db.sql.api.impl.cmd.dbFun.Function;
import db.sql.api.impl.tookit.Lists;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class Returning implements IReturning<Returning> {

    private final List<Cmd> returningFields = new ArrayList<>(6);

    @Override
    public List<Cmd> getReturningField() {
        return returningFields;
    }

    @Override
    public Returning returningIgnore(Cmd column) {
        returningFields.remove(column);
        return this;
    }

    @Override
    public Returning returning(Cmd field) {
        returningFields.add(field);
        return this;
    }

    @Override
    public Returning returning(Cmd... fields) {
        Lists.merge(this.returningFields, fields);
        return this;
    }

    @Override
    public Returning returning(List<Cmd> fields) {
        this.returningFields.addAll(fields);
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (!(parent instanceof Function)) {
            sqlBuilder.append(SqlConst.RETURNING);
        }
        sqlBuilder = CmdUtils.join(this, this, context, sqlBuilder, this.getReturningField(), SqlConst.DELIMITER);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.returningFields);
    }
}
