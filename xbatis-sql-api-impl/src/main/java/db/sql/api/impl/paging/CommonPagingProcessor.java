/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

import db.sql.api.*;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.impl.cmd.struct.Limit;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.List;

public class CommonPagingProcessor implements IPagingProcessor {

    @Override
    public StringBuilder buildPagingSQL(SqlBuilderContext context, Cmd module, Cmd parent
            , IQuery query, StringBuilder parentSQL, List<Cmd> beforeCmds, List<Cmd> afterCmds, Limit limit) {
        if (parentSQL == null) {
            parentSQL = new StringBuilder(200);
        }
        parentSQL = CmdUtils.join(module, query, context, parentSQL, beforeCmds);
        parentSQL = this.sql(context, module, parent, query, parentSQL, limit);
        parentSQL.append(CmdUtils.join(module, query, context, new StringBuilder(), afterCmds));
        return parentSQL;
    }

    private StringBuilder sql(SqlBuilderContext sqlBuilderContext, Cmd module, Cmd parent, IQuery query, StringBuilder parentSQL, Limit limit) {
        IDbType dbType = sqlBuilderContext.getDbType();

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.SQL_SERVER || dbType == DbType.ORACLE) {
            sqlBuilderContext.addParam(limit.getOffset());
            sqlBuilderContext.addParam(limit.getLimit());
            return parentSQL.append(SqlConst.OFFSET).append(SqlConst.PLACEHOLDER).append(SqlConst.ROWS_FETCH_NEXT).append(SqlConst.PLACEHOLDER).append(SqlConst.ROWS_ONLY);
        }

        Object limitValue = limit.getLimit();
        Object offsetValue = limit.getOffset();
        if (sqlBuilderContext.getSqlMode() != SQLMode.PRINT) {
            limitValue = SqlConst.PLACEHOLDER;
            offsetValue = SqlConst.PLACEHOLDER;
            sqlBuilderContext.addParam(limit.getLimit());
            sqlBuilderContext.addParam(limit.getOffset());
        }
        parentSQL.append(SqlConst.LIMIT).append(limitValue).append(SqlConst.OFFSET).append(offsetValue);
        return parentSQL;
    }
}
