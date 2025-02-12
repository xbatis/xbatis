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

package db.sql.api.impl.cmd.postgis;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.dbFun.BasicFunction;
import db.sql.api.impl.tookit.SqlConst;

public class ST_DWithin extends BasicFunction<ST_DWithin> implements ICondition {

    private final double distanceMeters;

    private final Boolean useSpheroid;

    private final Cmd g2;

    public ST_DWithin(Cmd g1, Cmd g2, double distanceMeters, Boolean useSpheroid) {
        super(SqlConst.ST_DWithin, g1);
        this.g2 = g2;
        this.distanceMeters = distanceMeters;
        this.useSpheroid = useSpheroid;
    }

    public ST_DWithin(Cmd g1, Cmd g2, double distanceMeters) {
        this(g1, g2, distanceMeters, null);
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder = sqlBuilder.append(operator);
        sqlBuilder = sqlBuilder.append(SqlConst.BRACKET_LEFT);
        sqlBuilder = key.sql(module, this, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(SqlConst.DELIMITER);
        sqlBuilder = g2.sql(module, this, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(SqlConst.DELIMITER);
        sqlBuilder = sqlBuilder.append(distanceMeters);
        if (useSpheroid != null) {
            sqlBuilder = sqlBuilder.append(SqlConst.DELIMITER);
            sqlBuilder = sqlBuilder.append(useSpheroid);
        }
        sqlBuilder = sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }
}
