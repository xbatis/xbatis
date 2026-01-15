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

package db.sql.api.impl.cmd.struct.update;


import db.sql.api.Cmd;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IInsert;
import db.sql.api.cmd.struct.update.IUpdateSets;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class UpdateSets implements IUpdateSets<TableField, Cmd, UpdateSet> {

    private List<UpdateSet> updateSets;


    public UpdateSets set(TableField field, Cmd value) {
        if (this.updateSets == null) {
            this.updateSets = new ArrayList<>(6);
        }
        this.updateSets.add(new UpdateSet(field, value));
        return this;
    }

    @Override
    public List<UpdateSet> getUpdateSets() {
        return updateSets;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (context.getDbType() == DbType.CLICK_HOUSE) {
            sqlBuilder.append(SqlConst.BLANK).append(SqlConst.UPDATE);
        } else {
            if (module instanceof IInsert && (context.getDbType().getDbModel() == DbModel.MYSQL || context.getDbType() == DbType.MYSQL || context.getDbType() == DbType.MARIA_DB || context.getDbType() == DbType.H2)) {
                sqlBuilder.append(SqlConst.BLANK).append(SqlConst.UPDATE);
            } else if (module instanceof IInsert && (context.getDbType() == DbType.GAUSS)) {
                sqlBuilder.append(SqlConst.BLANK);
            } else {
                sqlBuilder.append(SqlConst.SET);
            }
        }
        return CmdUtils.join(module, this, context, sqlBuilder, this.updateSets, SqlConst.DELIMITER);
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.updateSets);
    }
}
