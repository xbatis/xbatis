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

package db.sql.api.cmd.executor.method;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.*;

import java.util.function.Function;

public interface IReturningMethod<SELF extends IReturningMethod,
        TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd> {

    SELF returning(COLUMN cmd);

    default SELF returningAll() {
        this.returning((COLUMN) SQLCmdAll.INSTANCE);
        return (SELF) this;
    }

    default SELF returningAll(IDataset<?, ?> dataset) {
        this.returning((COLUMN) new SQLCmdAll(dataset));
        return (SELF) this;
    }


    default SELF returning(Class entity) {
        return this.returning(entity, 1);
    }

    SELF returning(Class entity, int storey);

    default SELF returning(Class... entities) {
        return this.returning(1, entities);
    }

    default SELF returning(int storey, Class... entities) {
        for (Class entity : entities) {
            this.returning(entity, storey);
        }
        return (SELF) this;
    }

    default <T> SELF returningIgnore(Getter<T> column) {
        return this.returningIgnore(column, 1);
    }

    <T> SELF returningIgnore(Getter<T> column, int storey);

    @SuppressWarnings("unchecked")
    default <T> SELF returningIgnore(Getter<T>... columns) {
        return this.returningIgnore(1, columns);
    }

    @SuppressWarnings("unchecked")
    default <T> SELF returningIgnore(int storey, Getter<T>... columns) {
        for (Getter column : columns) {
            this.returningIgnore(column, storey);
        }
        return (SELF) this;
    }

    default  <T> SELF returning(Getter<T>... columns){
        return returning(1,columns);
    }

    <T> SELF returning(int storey, Getter<T>... columns);

    default <T> SELF returning(Getter<T> column, Function<TABLE_FIELD,COLUMN> f){
        return returning(column,1,f);
    }

    <T> SELF returning(Getter<T> column, int storey, Function<TABLE_FIELD,COLUMN> f);
}
