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

package db.sql.api.cmd.executor.method.orderByMethod;


import db.sql.api.Cmd;

import java.util.List;
import java.util.function.Supplier;

public interface IOrderByCmdAscMethod<SELF extends IOrderByCmdAscMethod, COLUMN extends Cmd> extends IOrderByCmdMethod<SELF, COLUMN> {

    default SELF orderByAsc(COLUMN column) {
        return this.orderBy(ascOrderByDirection(), column);
    }

    default SELF orderByAsc(boolean when, COLUMN column) {
        return this.orderBy(when, ascOrderByDirection(), column);
    }

    default SELF orderByAsc(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), column);
    }

    default SELF orderByAsc(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByAsc(supplier);
    }

    @SuppressWarnings("unchecked")
    default SELF orderByAsc(COLUMN... columns) {
        return this.orderBy(ascOrderByDirection(), columns);
    }

    @SuppressWarnings("unchecked")
    default SELF orderByAsc(boolean when, COLUMN... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columns);
    }

    default SELF orderByAsc(List<COLUMN> columns) {
        return this.orderBy(ascOrderByDirection(), columns);
    }

    default SELF orderByAsc(boolean when, List<COLUMN> columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(ascOrderByDirection(), columns);
    }
}
