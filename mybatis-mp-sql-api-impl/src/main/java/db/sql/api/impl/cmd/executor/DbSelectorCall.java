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

package db.sql.api.impl.cmd.executor;

import db.sql.api.DbType;
import db.sql.api.impl.tookit.Objects;

import java.util.HashMap;
import java.util.Map;

public class DbSelectorCall<R> implements SelectorCall<R> {

    private final Map<DbType, DbTypeCallable> consumers = new HashMap<>();

    private DbTypeCallable<R> otherwise;

    @Override
    public DbSelectorCall<R> when(DbType dbType, DbTypeCallable<R> runnable) {
        consumers.put(dbType, runnable);
        return this;
    }

    @Override
    public DbSelectorCall<R> when(DbType[] dbTypes, DbTypeCallable<R> runnable) {
        for (DbType dbType : dbTypes) {
            consumers.put(dbType, runnable);
        }
        return this;
    }

    @Override
    public DbSelectorCall<R> otherwise(DbTypeCallable<R> runnable) {
        if (Objects.nonNull(this.otherwise)) {
            throw new RuntimeException("The method of 'otherwise' has already called");
        }
        this.otherwise = runnable;
        return this;
    }

    @Override
    public DbSelectorCall<R> otherwise() {
        return this.otherwise((dbType) -> {
            return null;
        });
    }

    private R execute(DbType dbType, DbTypeCallable<R> callable) {
        try {
            return callable.call(dbType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public R dbExecute(DbType dbType) {
        DbTypeCallable<R> runnable = consumers.get(dbType);
        if (Objects.nonNull(runnable)) {
            return this.execute(dbType, runnable);
        }
        if (Objects.nonNull(this.otherwise)) {
            return this.execute(dbType, this.otherwise);
        }
        throw new RuntimeException("Not adapted to DbType " + dbType);
    }
}
