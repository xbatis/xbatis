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

package db.sql.api.impl.cmd.executor;

import db.sql.api.DbType;
import db.sql.api.cmd.executor.Runnable;

/**
 * 选择器 不同数据库执行不同的方法
 */
public interface Selector {

    /**
     * 当数据库类型为dbType 时
     *
     * @param dbType   数据库类型
     * @param runnable 执行器
     * @return 自己
     */
    Selector when(DbType dbType, Runnable runnable);

    /**
     * 当数据库类型在dbTypes 时
     *
     * @param dbTypes  数据库类型数组
     * @param runnable 执行器
     * @return 自己
     */
    default Selector when(DbType[] dbTypes, Runnable runnable) {
        for (DbType dbType : dbTypes) {
            when(dbType, runnable);
        }
        return this;
    }

    /**
     * 其他数据库类型时
     *
     * @param runnable 执行器
     * @return 自己
     */
    Selector otherwise(Runnable runnable);

    /**
     * 其他数据库类型时 忽略
     */
    Selector otherwise();

    /**
     * 执行
     *
     * @param dbType 数据库类型
     */
    void dbExecute(DbType dbType);
}
