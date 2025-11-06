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

package db.sql.api.impl.cmd.struct;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.IForShare;
import db.sql.api.impl.tookit.SqlConst;

public class ForShare implements IForShare<ForShare> {

    private boolean noWait = false;

    private String options;

    private boolean skipLocked = false;

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (options != null) {
            if (!options.toUpperCase().contains("FOR")) {
                sqlBuilder = sqlBuilder.append(SqlConst.FOR).append(SqlConst.SHARE);
            }
            sqlBuilder = sqlBuilder.append(SqlConst.BLANK).append(options);
        } else if (noWait) {
            sqlBuilder = sqlBuilder.append(SqlConst.FOR).append(SqlConst.SHARE).append(SqlConst.NO_WAIT);
        } else if (skipLocked) {
            sqlBuilder = sqlBuilder.append(SqlConst.FOR).append(SqlConst.SHARE).append(SqlConst.SKIP_LOCKED);
        } else {
            sqlBuilder = sqlBuilder.append(SqlConst.FOR).append(SqlConst.SHARE);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return false;
    }

    @Override
    public void setWait(boolean wait) {
        this.noWait = !wait;
    }

    @Override
    public void setSkipLocked(boolean skipLock) {
        this.skipLocked = skipLock;
    }

    @Override
    public void setOptions(String options) {
        this.options = options;
    }
}
