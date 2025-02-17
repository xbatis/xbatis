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

package db.sql.api.cmd.executor.method.orderByMethod;


import db.sql.api.Cmd;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Function;

public interface IOrderByMultiGetterFunMethod<SELF extends IOrderByMultiGetterFunMethod, TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> extends IBaseOrderByMethods {

    default SELF orderBy(GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f) {
        return this.orderBy(ascOrderByDirection(), getterFields, f);
    }

    default SELF orderBy(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), getterFields, f);
    }

    default SELF orderByDesc(GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f) {
        return this.orderBy(descOrderByDirection(), getterFields, f);
    }

    default SELF orderByDesc(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(descOrderByDirection(), getterFields, f);
    }

    SELF orderBy(IOrderByDirection orderByDirection, GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f);

    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, GetterField[] getterFields, Function<TABLE_FIELD[], Cmd> f) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, getterFields, f);
    }
}
