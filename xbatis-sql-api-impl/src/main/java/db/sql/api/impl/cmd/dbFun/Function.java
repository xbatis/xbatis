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

package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.Alias;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.impl.tookit.SqlUtil;

public interface Function<T> extends Cmd, Alias<T> {

    default <T2> T as(Getter<T2> aliasGetter) {
        return this.as(SqlUtil.getAsName(aliasGetter));
    }

    /**
     * 拼接别名
     *
     * @param module
     * @param user
     * @param context
     * @param sqlBuilder
     */
    default void appendAlias(Cmd module, Cmd user, SqlBuilderContext context, StringBuilder sqlBuilder) {
        //拼接 select 的别名
        if (module instanceof Select && user instanceof Select) {
            if (this.getAlias() != null) {
                sqlBuilder.append(SqlConst.AS(context.getDbType()));
                sqlBuilder.append(this.getAlias());
            }

        }
    }

    /**
     * 函数的sql
     *
     * @param module     所属模块
     * @param parent     引用对象 - 父节点
     * @param context    sql 上下文
     * @param sqlBuilder sql 拼接对象
     * @return 函数 sql
     */
    StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder);

    @Override
    default StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        this.functionSql(module, parent, context, sqlBuilder);
        this.appendAlias(module, parent, context, sqlBuilder);
        return sqlBuilder;
    }
}
