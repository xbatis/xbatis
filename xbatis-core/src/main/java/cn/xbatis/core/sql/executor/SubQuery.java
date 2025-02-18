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

package cn.xbatis.core.sql.executor;

import db.sql.api.impl.cmd.struct.Where;

/**
 * 子查询
 */
public class SubQuery extends BaseSubQuery<SubQuery> {

    public SubQuery() {
        this((String) null);
    }

    public SubQuery(String alias) {
        super(alias);
    }

    public SubQuery(Where where) {
        super(null, where);
    }

    public SubQuery(String alias, Where where) {
        super(alias, where);
    }

    public static SubQuery create() {
        return new SubQuery();
    }

    public static SubQuery create(Where where) {
        return new SubQuery(where);
    }

    public static SubQuery create(String alias) {
        return new SubQuery(alias);
    }

    public static SubQuery create(String alias, Where where) {
        return new SubQuery(alias, where);
    }
}
