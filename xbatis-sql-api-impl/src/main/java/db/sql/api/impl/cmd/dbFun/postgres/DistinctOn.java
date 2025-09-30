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

package db.sql.api.impl.cmd.dbFun.postgres;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.AffectLineNumber;
import db.sql.api.cmd.NoAfterDelimiter;
import db.sql.api.impl.cmd.dbFun.BasicFunction;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

public class DistinctOn extends BasicFunction<DistinctOn> implements NoAfterDelimiter, AffectLineNumber {

    private Cmd[] onKeys;

    public DistinctOn(Cmd... onKeys) {
        super(SqlConst.DISTINCT_ON, null);
        this.onKeys = onKeys;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(this.operator).append(SqlConst.BRACKET_LEFT);
        sqlBuilder = CmdUtils.join(module, this, context, sqlBuilder, this.onKeys, SqlConst.DELIMITER);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    public Cmd[] getOnKeys() {
        return onKeys;
    }
}
