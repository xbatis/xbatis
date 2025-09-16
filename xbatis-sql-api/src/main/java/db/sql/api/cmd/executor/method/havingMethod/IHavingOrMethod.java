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

package db.sql.api.cmd.executor.method.havingMethod;

import db.sql.api.Getter;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Function;

public interface IHavingOrMethod<SELF extends IHavingOrMethod, TABLE extends ITable<TABLE, TABLE_FIELD>, TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> {

    SELF havingOr(ICondition condition);

    default SELF havingOr(boolean when, ICondition condition) {
        if (!when) {
            return (SELF) this;
        }
        return this.havingOr(condition);
    }

    //---

    default <T> SELF havingOr(Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.havingOr(true, column, f);
    }

    default <T> SELF havingOr(boolean when, Getter<T> column, Function<TABLE_FIELD, ICondition> f) {
        return this.havingOr(when, column, 1, f);
    }

    default <T> SELF havingOr(Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f) {
        return this.havingOr(true, column, storey, f);
    }

    <T> SELF havingOr(boolean when, Getter<T> column, int storey, Function<TABLE_FIELD, ICondition> f);

    default SELF havingOr(GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f) {
        return this.havingOr(true, getterFields, f);
    }

    SELF havingOr(boolean when, GetterField[] getterFields, Function<TABLE_FIELD[], ICondition> f);

}
