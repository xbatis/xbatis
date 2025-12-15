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
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.Distinct;
import db.sql.api.cmd.struct.query.ISelect;
import db.sql.api.impl.cmd.dbFun.Count;
import db.sql.api.impl.cmd.dbFun.Function;
import db.sql.api.impl.tookit.Lists;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Select implements ISelect<Select> {

    private final List<Cmd> selectFields = new ArrayList<>(6);

    private boolean distinct = false;

    private Integer top;

    @Override
    public List<Cmd> getSelectField() {
        return selectFields;
    }

    @Override
    public Select selectIgnore(Cmd column) {
        selectFields.remove(column);
        return this;
    }

    @Override
    public Select top(int count) {
        this.top = count;
        return this;
    }

    @Override
    public Select distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public boolean isDistinct() {
        return this.distinct;
    }

    @Override
    public Select select(Cmd field) {
        selectFields.add(field);
        return this;
    }

    @Override
    public Select select(Cmd... fields) {
        Lists.merge(this.selectFields, fields);
        return this;
    }

    @Override
    public Select select(List<Cmd> fields) {
        this.selectFields.addAll(fields);
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (!(parent instanceof Function)) {
            sqlBuilder.append(SqlConst.SELECT);
        }
        if (Objects.nonNull(top)) {
            if (context.getDbType() == DbType.SQL_SERVER) {
                sqlBuilder.append(" TOP ").append(top).append(SqlConst.BLANK);
            }
        }
        if (distinct) {
            Distinct.INSTANCE.sql(module, this, context, sqlBuilder);
            if ((context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.GAUSS || context.getDbType() == DbType.H2) && parent instanceof Count) {
                sqlBuilder.append(SqlConst.BRACKET_LEFT);
            }
        }
        sqlBuilder = CmdUtils.join(this, this, context, sqlBuilder, this.getSelectField(), SqlConst.DELIMITER);
        if (distinct) {
            if ((context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.GAUSS || context.getDbType() == DbType.H2) && parent instanceof Count) {
                sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            }
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.selectFields);
    }
}
