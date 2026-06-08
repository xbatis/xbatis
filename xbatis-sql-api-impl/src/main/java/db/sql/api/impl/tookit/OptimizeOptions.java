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

package db.sql.api.impl.tookit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
     * 禁止某个表优化Join
     */
    private Map<Class, Set<Integer>> disableOptimizeJoinMap;

    /**
     * 设置是否优化OrderBy
     *
     * @param optimizeOrderBy
     * @return 自己
     */
    public OptimizeOptions optimizeOrderBy(boolean optimizeOrderBy) {
        this.optimizeOrderBy = optimizeOrderBy;
        return this;
    }

    /**
     * 设置是否优化Join
     *
     * @param optimizeJoin
     * @return 自己
     */
    public OptimizeOptions optimizeJoin(boolean optimizeJoin) {
        this.optimizeJoin = optimizeJoin;
        return this;
    }

    /**
     * 设置禁止某个表优化Join
     *
     * @param entity 实体类
     * @return 自己
     */
    public OptimizeOptions disableOptimizeJoin(Class entity) {
        return this.disableOptimizeJoin(entity, 1);
    }

    /**
     * 设置禁止某个表优化Join
     *
     * @param entity
     * @param storey
     * @return 自己
     */
    public OptimizeOptions disableOptimizeJoin(Class entity, int storey) {
        if (disableOptimizeJoinMap == null) {
            disableOptimizeJoinMap = new HashMap<>();
        }
        disableOptimizeJoinMap.computeIfAbsent(entity, k -> new HashSet<>()).add(storey);
        return this;
    }

    /**
     * 关闭所有优化项
     *
     * @return 自己
     */
    public OptimizeOptions disableAll() {
        this.optimizeJoin = false;
        this.optimizeOrderBy = false;
        return this;
    }

    /**
     * 是否所有优化项关闭
     *
     * @return 是否全部禁用
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

    /**
     * 获取禁止优化Join的表
     *
     * @return 禁止优化Join的map
     */
    public Map<Class, Set<Integer>> getDisableOptimizeJoinMap() {
        return disableOptimizeJoinMap;
    }
}
