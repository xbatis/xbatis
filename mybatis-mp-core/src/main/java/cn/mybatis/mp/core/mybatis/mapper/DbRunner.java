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

package cn.mybatis.mp.core.mybatis.mapper;

import cn.mybatis.mp.core.mybatis.mapper.context.PreparedContext;
import cn.mybatis.mp.core.mybatis.provider.PreparedSQLProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface DbRunner {

    /**
     * 执行原生非查询类sql
     *
     * @param sql    例如 update xx set name=? where id=?
     * @param params 例如 abc ,1
     * @return 影响的数量
     */
    default int execute(String sql, Object... params) {
        return this.$execute(new PreparedContext(sql, params));
    }

    @UpdateProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL)
    int $execute(PreparedContext preparedContext);
}
