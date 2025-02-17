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
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.insert.IInsertFields;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.Lists;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class InsertFields implements IInsertFields<TableField> {

    protected List<TableField> tableFields;
    //SQL SERVER 专用
    private String output;

    public InsertFields field(TableField field) {
        if (tableFields == null) {
            this.tableFields = new ArrayList<>(10);
        }
        this.tableFields.add(field);
        return this;
    }

    public InsertFields field(TableField... fields) {
        if (tableFields == null) {
            this.tableFields = new ArrayList<>(10);
        }
        Lists.merge(this.tableFields, fields);
        return this;
    }

    public InsertFields field(List<TableField> fields) {
        if (tableFields == null) {
            this.tableFields = new ArrayList<>(10);
        }
        this.tableFields.addAll(fields);
        return this;
    }

    @Override
    public List<TableField> getFields() {
        return this.tableFields;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {

        sqlBuilder.append(SqlConst.BLANK).append(SqlConst.BRACKET_LEFT);
        boolean isFirst = true;
        for (TableField tableField : this.tableFields) {
            if (!isFirst) {
                sqlBuilder.append(SqlConst.DELIMITER);
            }
            sqlBuilder.append(tableField.getName(context.getDbType()));
            isFirst = false;
        }
        sqlBuilder.append(SqlConst.BRACKET_RIGHT).append(SqlConst.BLANK);

        if (context.getDbType() == DbType.SQL_SERVER) {
            if (this.output != null) {
                sqlBuilder.append(SqlConst.BLANK).append(this.output).append(SqlConst.BLANK);
            }
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.tableFields);
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
