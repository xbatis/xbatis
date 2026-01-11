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

package db.sql.api.impl.cmd.condition;

import db.sql.api.*;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SqlConst;

public abstract class AbstractILike<T extends AbstractILike<T>> extends AbstractLike<T> {

    public AbstractILike(char[] operator, LikeMode mode, Cmd key, Cmd value) {
        super(operator, mode, key, value);
    }

    boolean notSupport(IDbType dbType) {
        return dbType.getDbModel() != DbModel.PGSQL && dbType != DbType.H2 && dbType != DbType.PGSQL && dbType != DbType.GAUSS && dbType != DbType.KING_BASE;
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (this.notSupport(context.getDbType())) {
            this.operator = this instanceof NotILike ? SqlConst.NOT_LIKE : SqlConst.LIKE;
        }

        if (context.getDbType().getDbModel() != DbModel.MYSQL && context.getDbType() != DbType.SQL_SERVER && context.getDbType() != DbType.MYSQL && context.getDbType() != DbType.MARIA_DB) {
            this.field = Methods.upper(this.field);
            this.value = Methods.upper(this.value);
        }
        return super.conditionSql(module, parent, context, sqlBuilder);
    }
}
