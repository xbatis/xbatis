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
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.basic.AbstractAlias;
import db.sql.api.impl.cmd.basic.Condition;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;

public abstract class BaseCondition<T extends BaseCondition<T, COLUMN, V>, COLUMN extends Cmd, V> extends AbstractAlias<T> implements Condition<COLUMN, V> {

    protected char[] operator;

    public BaseCondition(char[] operator) {
        this.operator = operator;
    }

    public char[] getOperator() {
        return operator;
    }

    abstract StringBuilder conditionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder);

    /**
     * 拼接别名
     *
     * @param module
     * @param user
     * @param context
     * @param sqlBuilder
     */
    void appendAlias(Cmd module, Cmd user, SqlBuilderContext context, StringBuilder sqlBuilder) {
        //拼接 select 的别名
        if (module instanceof Select && user instanceof Select) {
            if (this.getAlias() != null) {
                sqlBuilder.append(SqlConst.AS(context.getDbType()));
                sqlBuilder.append(this.getAlias());
            }

        }
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder = this.conditionSql(module, parent, context, sqlBuilder);
        this.appendAlias(module, parent, context, sqlBuilder);
        return sqlBuilder;
    }
}
