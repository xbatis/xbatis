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

package db.sql.api.impl.cmd.basic;


import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;

public class AllField extends AbstractDatasetField<AllField> {

    public final static AllField ALL = new AllField();

    public AllField() {
        super(null, SqlConst.ALL);
    }

    public AllField(IDataset table) {
        super(table, SqlConst.ALL);
    }

    @Override
    public AllField as(String alias) {
        throw new RuntimeException("AllField不能设置别名");
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (module instanceof Select) {
            if (getTable() != null) {
                if (getTable().getAlias() != null) {
                    sqlBuilder.append(getTable().getAlias()).append(SqlConst.DOT);
                }
            }
        }
        return sqlBuilder.append(SqlConst.ALL);
    }
}
