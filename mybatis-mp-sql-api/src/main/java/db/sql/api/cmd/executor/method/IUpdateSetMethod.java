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

package db.sql.api.cmd.executor.method;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Function;

public interface IUpdateSetMethod<SELF extends IUpdateSetMethod, TABLE_FIELD extends ITableField<TABLE_FIELD, ?>, V> {

    SELF set(TABLE_FIELD field, V value);

    <T> SELF set(Getter<T> field, V value);

    <T, T2> SELF set(Getter<T> target, Getter<T2> source);

    default <T, T2> SELF set(boolean when, Getter<T> target, Getter<T2> source) {
        if (!when) {
            return (SELF) this;
        }
        return set(target, source);
    }

    <T> SELF set(Getter<T> field, Function<TABLE_FIELD, Cmd> f);
}
