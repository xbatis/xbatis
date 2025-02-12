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

package db.sql.api.impl.paging;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.Alias;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.struct.query.IOrderBy;
import db.sql.api.cmd.struct.query.ISelect;
import db.sql.api.cmd.struct.query.Withs;
import db.sql.api.impl.cmd.condition.In;
import db.sql.api.impl.cmd.struct.Limit;
import db.sql.api.impl.cmd.struct.update.UpdateSet;
import db.sql.api.tookit.CmdUtils;

import java.util.List;

public class SQLServerRowNumberOverPagingProcessor implements IPagingProcessor {

    @Override
    public StringBuilder buildPagingSQL(SqlBuilderContext context, Cmd module, Cmd parent
            , IQuery query, StringBuilder parentSQL, List<Cmd> beforeCmds, List<Cmd> afterCmds, Limit limit) {
        if (parentSQL == null) {
            parentSQL = new StringBuilder();
        }
        String alias = null;
        if (query instanceof Alias) {
            alias = ((Alias) query).getAlias();
        }
        if (alias == null || alias.isEmpty()) {
            alias = "NT";
        }

        StringBuilder orderBy;
        if (query.getOrderBy() != null) {
            orderBy = query.getOrderBy().sql(module, parent, context, new StringBuilder());
        } else {
            orderBy = new StringBuilder("ORDER BY CURRENT_TIMESTAMP");
        }

        StringBuilder sql = new StringBuilder(200);

        sql.append("SELECT TOP ").append(limit.getLimit()).append(" ");

        boolean handlerSelect = false;
        if (parent != null && (parent instanceof In || parent instanceof UpdateSet)) {
            //假如是在in条件里
            List<Cmd> selectFields = query.getSelect().getSelectField();
            if (selectFields.size() == 1) {
                Cmd cmd = selectFields.get(0);
                if (cmd instanceof IDatasetField) {
                    IDatasetField a = (IDatasetField) cmd;
                    if (a.getAlias() != null) {
                        sql.append(a.getAlias());
                    } else {
                        sql.append(a.getName(context.getDbType()));
                    }
                    handlerSelect = true;
                } else if (cmd instanceof Alias) {
                    Alias a = (Alias) cmd;
                    if (a.getAlias() != null) {
                        //假如是在in条件里
                        sql.append(a.getAlias());
                        handlerSelect = true;
                    }
                }
            }
        }

        if (!handlerSelect) {
            sql.append("*");
        }

        sql.append(" FROM (");

        String rnName = RowNumNameUtil.getRowName(context);
        for (Cmd cmd : beforeCmds) {
            if (cmd instanceof Withs) {
                cmd.sql(module, parent, context, parentSQL);
                continue;
            }

            if (cmd instanceof IOrderBy) {
                continue;
            }

            cmd.sql(module, parent, context, sql);
            if (cmd instanceof ISelect) {
                sql.append(",ROW_NUMBER() OVER(").append(orderBy).append(") ").append(rnName);
            }
        }
        sql.append(CmdUtils.join(module, query, context, new StringBuilder(), afterCmds));
        sql.append(") ").append(alias);
        sql.append(" WHERE ").append(rnName).append(" > ").append(limit.getOffset());

        return parentSQL.append(sql);
    }
}
