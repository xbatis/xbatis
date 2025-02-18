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

package db.sql.api.impl.tookit;

public class OptimizeOptions {

    /**
     * 是否优化OrderBy
     */
    private boolean optimizeOrderBy = true;

    /**
     * 是否优化Join
     */
    private boolean optimizeJoin = true;

    /**
     * 设置是否优化OrderBy
     *
     * @param optimizeOrderBy
     * @return OptimizeOptions
     */
    public OptimizeOptions optimizeOrderBy(boolean optimizeOrderBy) {
        this.optimizeOrderBy = optimizeOrderBy;
        return this;
    }

    /**
     * 设置是否优化Join
     *
     * @param optimizeJoin
     * @return OptimizeOptions
     */
    public OptimizeOptions optimizeJoin(boolean optimizeJoin) {
        this.optimizeJoin = optimizeJoin;
        return this;
    }

    /**
     * 关闭所有优化项
     *
     * @return
     */
    public OptimizeOptions disableAll() {
        this.optimizeJoin = false;
        this.optimizeOrderBy = false;
        return this;
    }

    /**
     * 是否所有优化项关闭
     *
     * @return
     */
    public boolean isAllDisable() {
        return !optimizeOrderBy && !optimizeJoin;
    }

    /**
     * 是否优化Join
     *
     * @return 是否优化
     */
    public boolean isOptimizeJoin() {
        return optimizeJoin;
    }

    /**
     * 是否优化OrderBy
     *
     * @return 是否优化
     */
    public boolean isOptimizeOrderBy() {
        return optimizeOrderBy;
    }
}
