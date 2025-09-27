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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.text.MessageFormat;
import java.util.Objects;

public abstract class BaseTemplate<T extends BaseTemplate<T>> extends AbstractAlias<T> implements Cmd {

    protected final String template;

    protected final Cmd[] params;

    protected final boolean wrapping;

    public BaseTemplate(String template, Object... params) {
        this(false, template, params);
    }

    public BaseTemplate(boolean wrapping, String template, Object... params) {
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
        this.wrapping = wrapping;
    }

    public BaseTemplate(boolean wrapping, String template, Cmd... params) {
        this.template = template;
        this.wrapping = wrapping;
        this.params = params;
    }

    /**
     * 对模板特殊字符 进行封装：例如 ',format会报错，自动包装成 ''
     *
     * @param template
     * @return
     */
    protected String wrapTemplate(String template) {
        StringBuilder tsb = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            boolean hasPre = i > 0;
            boolean hasNext = i < template.length() - 1;
            System.out.println(i);
            char c = template.charAt(i);
            if (c != '\'') {
                tsb.append(c);
                continue;
            }

            boolean doAppend = false;
            if (hasPre && hasNext) {

                if (template.charAt(i - 1) != '\'' && template.charAt(i + 1) != '\'') {
                    doAppend = true;
                }
            } else if (hasPre) {
                if (template.charAt(i - 1) != '\'') {
                    doAppend = true;
                }
            } else if (hasNext) {
                if (template.charAt(i + 1) != '\'') {
                    doAppend = true;
                }
            }

            if (doAppend) {
                tsb.append('\'');
            }
            tsb.append(c);
        }
        return tsb.toString();
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
            if (wrapping) {
                str = wrapTemplate(this.template);
            }
            str = MessageFormat.format(str, paramsStr);
        } else if (wrapping) {
            str = wrapTemplate(this.template);
            str = MessageFormat.format(str, null);
        }
        sqlBuilder.append(SqlConst.BLANK).append(str);
        this.appendAlias(module, parent, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public final boolean contain(Cmd cmd) {
        boolean contain = false;

        if (Objects.nonNull(params)) {
            contain = CmdUtils.contain(cmd, params);
        }

        if (!contain && cmd instanceof IDataset) {
            //支持字符串列；需要含有“别名.xx"的情况
            IDataset dataset = (IDataset) cmd;
            if (dataset.getAlias() != null && !dataset.getAlias().isEmpty()) {
                return this.template.contains(dataset.getAlias() + ".");
            }
        }

        return contain;
    }
}
