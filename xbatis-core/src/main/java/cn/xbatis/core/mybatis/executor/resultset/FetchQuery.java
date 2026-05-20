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

package cn.xbatis.core.mybatis.executor.resultset;

import cn.xbatis.core.sql.executor.Query;
import db.sql.api.impl.cmd.struct.Where;

public class FetchQuery<T> extends Query<T> {

    public FetchQuery() {
    }

    public FetchQuery(Where where) {
        super(where);
    }

    public static <T> FetchQuery<T> create() {
        return new FetchQuery();
    }

    public static <T> FetchQuery<T> create(Where where) {
        if (where == null) {
            return create();
        }
        return new FetchQuery(where);
    }
}
