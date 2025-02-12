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
import db.sql.api.cmd.basic.ICountAll;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.dbFun.Count;
import db.sql.api.impl.tookit.SqlConst;

public class CountAll extends Count implements ICountAll<Count> {

    public CountAll() {
        super(Methods.column("*"));
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(operator).append(SqlConst.BRACKET_LEFT);
        sqlBuilder.append("*");
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return false;
    }
}
