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

package db.sql.api;

public interface Cmd {
    /**
     * 构建sql
     *
     * @param module     模块的组件 例如 select ，order by
     * @param parent     使用改组件的组件
     * @param context    sql构建上下文
     * @param sqlBuilder 构建SQL的StringBuilder
     * @return SQL
     */
    StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder);

    /**
     * 是否包含某个sql命令
     *
     * @param cmd
     * @return 是否包含
     */
    boolean contain(Cmd cmd);
}
