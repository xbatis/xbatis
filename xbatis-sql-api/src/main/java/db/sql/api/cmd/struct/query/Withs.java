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

package db.sql.api.cmd.struct.query;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Withs implements Cmd {

    private List<IWith> withs;

    public void add(IWith with) {
        if (withs == null) {
            withs = new ArrayList<>(2);
        }
        withs.add(with);
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (withs == null || withs.isEmpty()) {
            return sqlBuilder;
        }
        sqlBuilder.append(" WITH ");
        CmdUtils.join(module, this, context, sqlBuilder, this.withs, ",".toCharArray());
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.withs);
    }

    public List<IWith> getUnions() {
        return Collections.unmodifiableList(this.withs);
    }
}
