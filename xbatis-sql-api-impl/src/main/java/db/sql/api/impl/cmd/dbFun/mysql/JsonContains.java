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

package db.sql.api.impl.cmd.dbFun.mysql;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.dbFun.BasicFunction;
import db.sql.api.impl.tookit.SqlConst;

import java.io.Serializable;

public class JsonContains extends BasicFunction<JsonContains> implements ICondition {

    private final Serializable containValue;

    private final String path;

    public JsonContains(Cmd key, Serializable containValue) {
        this(key, containValue, null);
    }


    public JsonContains(Cmd key, Serializable containValue, String path) {
        super(SqlConst.JSON_CONTAINS, key);
        this.containValue = containValue;
        this.path = path;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(operator).append(SqlConst.BRACKET_LEFT);
        this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER);
        if (containValue instanceof Number) {
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
            sqlBuilder.append(containValue);
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
        } else {
            new JsonQuote(containValue).sql(module, this, context, sqlBuilder);
        }

        if (path != null) {
            sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
            sqlBuilder.append(path);
            sqlBuilder.append(SqlConst.SINGLE_QUOT);
        }
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}
