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
import db.sql.api.cmd.struct.IForUpdate;
import db.sql.api.impl.tookit.SqlConst;

public class ForUpdate implements IForUpdate<ForUpdate> {

    private boolean wait = true;

    private String options;

    private boolean skipLock = false;

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(SqlConst.FOR_UPDATE);
        if (!wait) {
            sqlBuilder.append(wait ? SqlConst.FOR_UPDATE : SqlConst.NO_WAIT);
        } else if (skipLock) {
            sqlBuilder.append(skipLock ? SqlConst.SKIP_LOCKED : SqlConst.FOR_UPDATE);
        }

        if (options != null) {
            sqlBuilder.append(SqlConst.BLANK).append(options);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return false;
    }

    @Override
    public void setWait(boolean wait) {
        this.wait = wait;
    }

    @Override
    public void setSkipLock(boolean skipLock) {
        this.skipLock = skipLock;
    }

    @Override
    public void setOptions(String options) {
        this.options = options;
    }
}
