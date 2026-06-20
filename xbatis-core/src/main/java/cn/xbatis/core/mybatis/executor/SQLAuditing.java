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

package cn.xbatis.core.mybatis.executor;

import java.util.List;

/**
 * SQL 审计
 */
public interface SQLAuditing {

    /**
     * SQL 审计操作
     *
     * @param sql         sql
     * @param params      sql 参数
     * @param startTime   开始时间（毫秒）
     * @param endTime     结束时间（毫秒）
     * @param queryCount  查询结果总条数; -1 时 应该是查询
     * @param updateCount 新增/修改/删除影响的总条数；-1时 应该非查询
     */
    void auditOperation(String sql, List<Object> params, long startTime, long endTime, long queryCount, long updateCount);
}
