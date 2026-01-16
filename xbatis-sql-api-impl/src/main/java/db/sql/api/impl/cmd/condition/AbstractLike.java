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

import db.sql.api.Cmd;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.basic.BasicValue;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlConst;

public class AbstractLike<T extends AbstractLike<T>> extends BasicCondition<T> {
    private final LikeMode mode;

    public AbstractLike(char[] operator, LikeMode mode, Cmd key, Cmd value) {
        super(operator, key, value);
        this.mode = mode;
    }

    public LikeMode getMode() {
        return mode;
    }

    @Override
    public StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder = getField().sql(module, this, context, sqlBuilder);
        sqlBuilder.append(getOperator());
        boolean before = false;
        boolean after = false;

        switch (this.mode) {
            case NONE: {
                break;
            }
            case DEFAULT: {
                before = true;
                after = true;
                break;
            }
            case RIGHT: {
                after = true;
                break;
            }
            default: {
                before = true;
            }
        }


        if (context.getDbType() == DbType.DB2 || context.getDbType() == DbType.SQLITE) {
            //由于 DB2 CONCAT 不支持 变量，所以只能换方式
            if (!before && !after) {
                sqlBuilder = getValue().sql(module, this, context, sqlBuilder);
            } else {
                sqlBuilder.append(SqlConst.BRACKET_LEFT);
                if (before) {
                    sqlBuilder.append(SqlConst.VAGUE_SYMBOL);
                    sqlBuilder.append(SqlConst.CONCAT_SPLIT_SYMBOL);
                }
                sqlBuilder = getValue().sql(module, this, context, sqlBuilder);
                if (after) {
                    sqlBuilder.append(SqlConst.CONCAT_SPLIT_SYMBOL).append(SqlConst.VAGUE_SYMBOL);
                }
                sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            }
            return sqlBuilder;
        }

        if (before && after && (context.getDbType().getDbModel() == DbModel.ORACLE || context.getDbType() == DbType.ORACLE
                || context.getDbType() == DbType.KING_BASE
        )) {
            //ORACLE Concat 不支持2个以上参数
            sqlBuilder.append(SqlConst.BRACKET_LEFT);
            sqlBuilder.append(SqlConst.VAGUE_SYMBOL).append(SqlConst.CONCAT_SPLIT_SYMBOL);
            sqlBuilder = getValue().sql(module, this, context, sqlBuilder);
            sqlBuilder.append(SqlConst.CONCAT_SPLIT_SYMBOL).append(SqlConst.VAGUE_SYMBOL);
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
            return sqlBuilder;
        }

        if (before || after) {
            sqlBuilder.append(SqlConst.CONCAT).append(SqlConst.BRACKET_LEFT);
        }


        if (before) {
            sqlBuilder.append(SqlConst.VAGUE_SYMBOL).append(SqlConst.DELIMITER);
        }
        sqlBuilder = getValue().sql(module, this, context, sqlBuilder);

        if (getValue().getClass() == BasicValue.class && (context.getDbType().getDbModel() == DbModel.PGSQL || context.getDbType() == DbType.PGSQL || context.getDbType() == DbType.GAUSS)) {
            BasicValue basicValue = (BasicValue) getValue();
            if (Objects.nonNull(basicValue)) {
                sqlBuilder.append(SqlConst.CAST_TEXT);
            }
        }

        if (after) {
            sqlBuilder.append(SqlConst.DELIMITER).append(SqlConst.VAGUE_SYMBOL);
        }
        if (before || after) {
            sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        }
        return sqlBuilder;
    }
}
