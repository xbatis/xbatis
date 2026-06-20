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

package com.xbatis.core.test;

import cn.xbatis.core.mybatis.executor.SQLAuditing;

import java.util.List;

public class DefaultTestSQLAuditing implements SQLAuditing {

    @Override
    public void auditOperation(String sql, List<Object> params, long startTime, long endTime, long queryCount, long updateCount) {
            System.out.println("SQLAuditing sql: " + sql);
            System.out.println("SQLAuditing params: " + params);
            System.out.println("SQLAuditing costTime:" + (endTime - startTime));
            System.out.println("SQLAuditing queryCount: " + queryCount);
            System.out.println("SQLAuditing updateCount: " + updateCount);
    }
}
