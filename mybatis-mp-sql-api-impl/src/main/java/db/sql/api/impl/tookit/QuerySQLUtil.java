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

package db.sql.api.impl.tookit;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.struct.ILimit;
import db.sql.api.impl.cmd.struct.Limit;
import db.sql.api.impl.paging.PagingProcessorFactory;
import db.sql.api.tookit.CmdUtils;

import java.util.ArrayList;
import java.util.List;

public class QuerySQLUtil {

    public static StringBuilder buildQuerySQL(SqlBuilderContext context, Cmd module, Cmd parent, IQuery query, StringBuilder parentSql, List<Cmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return parentSql;
        }
        List<Cmd> before = new ArrayList<>();
        List<Cmd> after = new ArrayList<>();
        boolean findLimit = false;
        Limit limit = null;
        for (Cmd cmd : cmdList) {
            if (cmd instanceof ILimit) {
                findLimit = true;
                limit = (Limit) cmd;
                continue;
            }
            if (findLimit) {
                after.add(cmd);
            } else {
                before.add(cmd);
            }
        }

        if (!findLimit) {
            //没有分页
            return CmdUtils.join(null, query, context, parentSql, before);
        }
//        IPagingProcessor pagingProcessor = PagingProcessorFactory.getProcessor(context.getDbType());
//        if(pagingProcessor instanceof SQLServerRowNumberOverPagingProcessor){
//            before = before.stream().filter(item->!(item instanceof ISelect) && !(item instanceof IOrderBy)).collect(Collectors.toList());
//        }
        //StringBuilder sql = CmdUtils.join(module, query, context, new StringBuilder(200), before);
        // parentSql.append(pagingProcessor.buildPagingSQL(context, parent, query, sql, limit));
        //parentSql.append(CmdUtils.join(module, query, context, new StringBuilder(), after));
        return PagingProcessorFactory.getProcessor(context.getDbType()).buildPagingSQL(context, module, parent, query, parentSql, before, after, limit);
    }
}
