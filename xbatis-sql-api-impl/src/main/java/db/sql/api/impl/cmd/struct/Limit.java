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
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.ILimit;

public class Limit implements ILimit<Limit> {

    private int offset;

    private int limit;

    public Limit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (limit < 0) {
            return sqlBuilder;
        }
        if (context.getDbType().getDbModel() == DbModel.ORACLE || context.getDbType() == DbType.ORACLE || context.getDbType() == DbType.SQL_SERVER) {
            return sqlBuilder.append(" OFFSET ").append(this.offset).append(" ROWS FETCH NEXT ").append(this.limit).append(" ROWS ONLY");
        }
        return sqlBuilder.append(" LIMIT ").append(this.limit).append(" OFFSET ").append(this.offset);
    }

    public Limit set(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return false;
    }
}
