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
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.struct.query.IUnion;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class Union implements IUnion {

    private final char[] operator;

    private final IQuery unionQuery;

    public Union(IQuery unionQuery) {
        this(SqlConst.UNION, unionQuery);
    }

    public Union(char[] operator, IQuery unionQuery) {
        this.operator = operator;
        this.unionQuery = unionQuery;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(this.operator);
        unionQuery.sql(module, this, context, sqlBuilder);
        return sqlBuilder;
    }


    public char[] getOperator() {
        return operator;
    }

    @Override
    public IQuery getUnionQuery() {
        return unionQuery;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.unionQuery);
    }
}
