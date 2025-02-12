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

package cn.mybatis.mp.core.sql.executor;

import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public class Query<T> extends BaseQuery<Query<T>, T> {

    public Query() {
        super();
    }

    public Query(Where where) {
        super(where);
    }

    public static <T> Query<T> create() {
        return new Query();
    }

    public static <T> Query<T> create(Where where) {
        if (where == null) {
            return create();
        }
        return new Query(where);
    }

    public <R> Query<R> returnType(Class<R> returnType) {
        return (Query<R>) super.setReturnType(returnType);
    }

    public <R> Query<R> returnType(Class<R> returnType, Consumer<R> consumer) {
        return (Query<R>) super.setReturnType(returnType, consumer);
    }
}
