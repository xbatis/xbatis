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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;

public class ConflictUpdateTableField extends TableField {

    private TableField tableField;

    public ConflictUpdateTableField(TableField tableField) {
        super(tableField.getTable(), tableField.getName(), tableField.isId());
        this.tableField = tableField;
    }


    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.PGSQL
                || context.getDbType() == DbType.KING_BASE
                || context.getDbType() == DbType.GAUSS
                || context.getDbType() == DbType.SQLITE
        ) {
            sqlBuilder.append(" EXCLUDED.").append(this.getName());
        } else {
            sqlBuilder.append(" VALUES(").append(this.getName()).append(")");
        }
        return sqlBuilder;
    }
}
