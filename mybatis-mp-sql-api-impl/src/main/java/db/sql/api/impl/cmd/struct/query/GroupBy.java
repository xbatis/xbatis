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

package db.sql.api.impl.cmd.struct.query;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.query.IGroupBy;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupBy implements IGroupBy<GroupBy, Cmd> {

    private final List<Cmd> groupByFields = new ArrayList<>(2);

    @Override
    public GroupBy groupBy(Cmd field) {
        groupByFields.add(field);
        return this;
    }

    @Override
    public List<Cmd> getGroupByFields() {
        return groupByFields;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(SqlConst.GROUP_BY);
        CmdUtils.join(module, this, context, sqlBuilder, this.groupByFields, SqlConst.DELIMITER);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.groupByFields);
    }
}
