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
import db.sql.api.impl.tookit.SQLOptimizeUtils;

public class XbatisSQLBuilder implements SQLBuilder {

    /**
     * 构建query sql
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return query的sql
     */
    @Override
    public StringBuilder buildQuerySQL(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions) {
        return SQLOptimizeUtils.getOptimizedSql(query, context, optimizeOptions);
    }

    /**
     * 构建count 查询sql
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return query count的sql
     */
    @Override
    public StringBuilder buildCountQuerySQL(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions) {
        return SQLOptimizeUtils.getOptimizedCountSql(query, context, optimizeOptions);
    }

    /**
     * 从query中构建count sql，一般用于分页时使用
     *
     * @param query           查询
     * @param context         上下文
     * @param optimizeOptions 优化配置
     * @return 从query中构建的count sql
     */
    @Override
    public StringBuilder buildCountSQLFromQuery(BaseQuery query, SqlBuilderContext context, OptimizeOptions optimizeOptions) {
        return SQLOptimizeUtils.getCountSqlFromQuery(query, context, optimizeOptions);
    }

    @Override
    public StringBuilder buildInsertSQL(BaseInsert insert, SqlBuilderContext context) {
        return insert.sql(null, null, context, new StringBuilder(SQLOptimizeUtils.getStringBuilderCapacity(insert.cmds())));
    }

    @Override
    public StringBuilder buildUpdateSQL(BaseUpdate update, SqlBuilderContext context) {
        return update.sql(null, null, context, new StringBuilder(SQLOptimizeUtils.getStringBuilderCapacity(update.cmds())));
    }

    @Override
    public StringBuilder buildDeleteSQL(BaseDelete delete, SqlBuilderContext context) {
        return delete.sql(null, null, context, new StringBuilder(SQLOptimizeUtils.getStringBuilderCapacity(delete.cmds())));
    }
}
