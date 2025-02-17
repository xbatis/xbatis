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

package cn.xbatis.core.sql;

import cn.xbatis.core.sql.executor.BaseDelete;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.BaseUpdate;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.tookit.OptimizeOptions;

public interface SQLBuilder {

    /**
     * 构建query sql
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return sql
     */
    StringBuilder buildQuerySQl(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions);


    /**
     * 构建 count query sql
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return sql
     */
    StringBuilder buildCountQuerySQl(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions);

    /**
     * 从query 中 获取 count 查询 sql
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return sql
     */
    StringBuilder buildCountSQLFromQuery(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions);

    /**
     * @param insert  插入对象
     * @param context 上下文
     * @return sql
     */
    StringBuilder buildInsertSQL(BaseInsert insert, SqlBuilderContext context);

    /**
     * @param update  修改对象
     * @param context 上下文
     * @return sql
     */
    StringBuilder buildUpdateSQL(BaseUpdate update, SqlBuilderContext context);

    /**
     * @param delete  删除对象
     * @param context 上下文
     * @return sql
     */
    StringBuilder buildDeleteSQL(BaseDelete delete, SqlBuilderContext context);

}
