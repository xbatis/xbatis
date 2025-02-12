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
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.basic.BasicValue;
import db.sql.api.impl.tookit.SqlConst;

import java.math.BigDecimal;

public class ST_Point implements Cmd {

    private BasicValue x;

    private BasicValue y;

    private int srid;

    public ST_Point(double x, double y, int srid) {
        this.x = new BasicValue(x);
        this.y = new BasicValue(y);
        this.srid = srid;
    }

    public ST_Point(double x, double y) {
        this(x, y, 4326);
    }

    public ST_Point(BigDecimal x, BigDecimal y, int srid) {
        this.x = new BasicValue(x);
        this.y = new BasicValue(y);
        this.srid = srid;
    }

    public ST_Point(BigDecimal x, BigDecimal y) {
        this(x, y, 4326);
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (srid != 0) {
            if (context.getDbType() == DbType.MYSQL || context.getDbType() == DbType.MARIA_DB) {
                sqlBuilder.append("ST_SRID(");
            } else {
                sqlBuilder = sqlBuilder.append("ST_SetSRID(");
            }
        }

        if (context.getDbType() == DbType.MYSQL || context.getDbType() == DbType.MARIA_DB) {
            sqlBuilder = sqlBuilder.append("Point(");
        } else {
            sqlBuilder = sqlBuilder.append("ST_MakePoint(");
        }

        sqlBuilder = this.x.sql(module, parent, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(SqlConst.DELIMITER);
        sqlBuilder = this.y.sql(module, parent, context, sqlBuilder);
        sqlBuilder = sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        sqlBuilder = sqlBuilder.append(SqlConst.DELIMITER);

        if (srid != 0) {
            sqlBuilder = sqlBuilder.append(srid);
            sqlBuilder = sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return false;
    }
}
