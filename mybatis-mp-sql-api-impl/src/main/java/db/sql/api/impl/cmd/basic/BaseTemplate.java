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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.text.MessageFormat;
import java.util.Objects;

public abstract class BaseTemplate<T extends BaseTemplate<T>> extends AbstractAlias<T> implements Cmd {

    protected final String template;

    protected final Cmd[] params;

    @SafeVarargs
    public BaseTemplate(String template, Object... params) {
        this.template = template;
        if (Objects.nonNull(params)) {
            Cmd[] cmds = new Cmd[params.length];
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                cmds[i] = param instanceof Cmd ? (Cmd) param : new BasicValue(param);
            }
            this.params = cmds;
        } else {
            this.params = null;
        }
    }

    @SafeVarargs
    public BaseTemplate(String template, Cmd... params) {
        this.template = template;
        this.params = params;
    }

    /**
     * 拼接别名
     *
     * @param module
     * @param user
     * @param context
     * @param sqlBuilder
     */
    private void appendAlias(Cmd module, Cmd user, SqlBuilderContext context, StringBuilder sqlBuilder) {
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
        sqlBuilder.append(SqlConst.BLANK);
        String str = this.template;
        if (Objects.nonNull(params) && params.length > 0) {
            Object[] paramsStr = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsStr[i] = params[i].sql(module, this, context, new StringBuilder());
            }
            str = MessageFormat.format(this.template, paramsStr);
        }
        sqlBuilder.append(SqlConst.BLANK).append(str);
        this.appendAlias(module, parent, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public final boolean contain(Cmd cmd) {
        if (Objects.isNull(params)) {
            return false;
        }
        return CmdUtils.contain(cmd, (Object[]) params);
    }
}
