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

package db.sql.api.impl.cmd.struct.insert;

import db.sql.api.Cmd;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.insert.IInsertValues;
import db.sql.api.impl.cmd.executor.AbstractInsert;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class InsertValues implements IInsertValues<Cmd> {

    protected List<List<Cmd>> values;

    @Override
    public List<List<Cmd>> getValues() {
        return values;
    }

    public InsertValues add(List<Cmd> values) {
        if (this.values == null) {
            this.values = new ArrayList<>(10);
        }
        this.values.add(values);
        return this;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (Objects.isNull(values) || values.isEmpty()) {
            return sqlBuilder;
        }
        boolean oracleMuti = false;

        if ((context.getDbType().getDbModel() == DbModel.ORACLE || context.getDbType() == DbType.ORACLE) && parent instanceof AbstractInsert) {
            List<List<Cmd>> insertValues = this.getValues();
            if (Objects.nonNull(insertValues) && insertValues.size() > 1) {
                oracleMuti = true;
            }
        }

        if (!oracleMuti) {
            sqlBuilder.append(SqlConst.VALUES);
        }

        boolean isFirstLine = true;
        for (List<Cmd> values : this.values) {
            if (!isFirstLine) {
                if (oracleMuti) {
                    sqlBuilder.append(SqlConst.UNION_ALL);
                } else {
                    sqlBuilder.append(SqlConst.DELIMITER);
                }
            }

            if (!oracleMuti) {
                sqlBuilder.append(SqlConst.BLANK).append(SqlConst.BRACKET_LEFT);
            } else {
                sqlBuilder.append(SqlConst.BLANK).append(SqlConst.SELECT);
            }

            boolean isFirst = true;
            for (Cmd value : values) {
                if (!isFirst) {
                    sqlBuilder.append(SqlConst.DELIMITER);
                }
                value.sql(module, this, context, sqlBuilder);
                isFirst = false;
            }
            if (!oracleMuti) {
                sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BLANK);
            } else {
                sqlBuilder.append(SqlConst.BLANK).append(SqlConst.FROM_DUAL);
            }
            isFirstLine = false;
        }

        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.values);
    }
}
