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

package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.BasicValue;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static db.sql.api.impl.tookit.SqlConst.CASE;

public class Case extends BasicFunction<Case> {

    private final List<Cmd> values = new ArrayList<>(3);

    public Case() {
        super(CASE, null);
    }

    public Case when(ICondition condition, Cmd then) {
        values.add(new CaseWhen(condition, then));
        return this;
    }

    public Case when(ICondition condition, Serializable then) {
        return this.when(condition, Methods.cmd(then));
    }

    public Case when(boolean when, ICondition condition, Serializable then) {
        if (!when) {
            return this;
        }
        return this.when(condition, then);
    }

    public <V extends Serializable> Case when(ICondition condition, V then, Predicate<V> predicate) {
        return this.when(predicate.test(then), condition, then);
    }

    public Case else_(Cmd then) {
        values.add(then);
        return this;
    }

    public Case else_(Serializable then) {
        return this.else_(Methods.cmd(then));
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(SqlConst.BRACKET_LEFT).append(operator);
        for (Cmd item : values) {
            if (!(item instanceof CaseWhen)) {
                sqlBuilder.append(SqlConst.ELSE);
                if (context.getDbType() == DbType.DB2 && item instanceof BasicValue) {
                    //没办法 DB2 数据库 ELSE 部分不支持 ?
                    BasicValue basicValue = (BasicValue) item;
                    if (basicValue.getValue() instanceof Number) {
                        sqlBuilder.append(basicValue.getValue());
                    } else {
                        sqlBuilder.append("'").append(basicValue.getValue()).append("'");
                    }
                    continue;
                }
            }
            sqlBuilder = item.sql(module, this, context, sqlBuilder);
        }
        sqlBuilder.append(SqlConst.END);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.values);
    }
}

