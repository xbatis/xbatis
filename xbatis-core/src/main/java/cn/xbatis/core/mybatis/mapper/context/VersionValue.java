/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package cn.xbatis.core.mybatis.mapper.context;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.basic.BasicValue;

public class VersionValue implements Cmd {

    private final BasicValue basicValue;

    private final Object newVersion;

    public VersionValue(BasicValue basicValue, Object newVersion) {
        this.basicValue = basicValue;
        this.newVersion = newVersion;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        return basicValue.sql(module, parent, context, sqlBuilder);
    }

    @Override
    public boolean contain(Cmd cmd) {
        return basicValue.contain(cmd);
    }

    public BasicValue getBasicValue() {
        return basicValue;
    }

    public Object getNewVersion() {
        return newVersion;
    }
}
