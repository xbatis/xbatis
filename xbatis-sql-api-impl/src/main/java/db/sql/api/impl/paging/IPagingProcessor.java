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

package db.sql.api.impl.paging;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.impl.cmd.struct.Limit;

import java.util.List;

public interface IPagingProcessor {

    /**
     * 构建分页sql
     *
     * @param sqlBuilderContext 构建上下文
     * @param module            所属模块
     * @param parent            父节点
     * @param query             查询类
     * @param beforeCmds        分页前面的cmd列表
     * @param afterCmds         分页后面的cmd列表
     * @param limit             limit 分页对象
     * @return 包含分页的SQL
     */
    StringBuilder buildPagingSQL(SqlBuilderContext sqlBuilderContext, Cmd module, Cmd parent
            , IQuery query, StringBuilder parentSQL, List<Cmd> beforeCmds, List<Cmd> afterCmds, Limit limit);
}
