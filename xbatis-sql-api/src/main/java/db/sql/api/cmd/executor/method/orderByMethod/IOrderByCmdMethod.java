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
import db.sql.api.cmd.basic.IOrderByDirection;

import java.util.List;
import java.util.function.Supplier;

public interface IOrderByCmdMethod<SELF extends IOrderByCmdMethod, COLUMN extends Cmd> extends IBaseOrderByMethods {

    default SELF orderBy(COLUMN column) {
        return this.orderBy(ascOrderByDirection(), column);
    }

    default SELF orderBy(boolean when, COLUMN column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(column);
    }

    default SELF orderBy(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return (SELF) this;
        }
        return this.orderBy(column);
    }

    default SELF orderBy(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(supplier);
    }

    @SuppressWarnings("unchecked")
    default SELF orderBy(COLUMN... columns) {
        return this.orderBy(ascOrderByDirection(), columns);
    }

    default SELF orderBy(boolean when, COLUMN... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(columns);
    }

    default SELF orderBy(List<COLUMN> columns) {
        return this.orderBy(ascOrderByDirection(), columns);
    }

    default SELF orderBy(boolean when, List<COLUMN> columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(columns);
    }

    default SELF orderByDesc(COLUMN column) {
        return this.orderBy(descOrderByDirection(), column);
    }

    default SELF orderByDesc(boolean when, COLUMN column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByDesc(column);
    }

    default SELF orderByDesc(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return (SELF) this;
        }
        return this.orderByDesc(column);
    }

    default SELF orderByDesc(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderByDesc(supplier);
    }

    @SuppressWarnings("unchecked")
    default SELF orderByDesc(COLUMN... columns) {
        return this.orderBy(descOrderByDirection(), columns);
    }

    @SuppressWarnings("unchecked")
    default SELF orderByDesc(boolean when, COLUMN... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(columns);
    }

    default SELF orderByDesc(List<COLUMN> columns) {
        return this.orderBy(descOrderByDirection(), columns);
    }

    default SELF orderByDesc(boolean when, List<COLUMN> columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(columns);
    }

    SELF orderBy(IOrderByDirection orderByDirection, COLUMN column);

    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, COLUMN column) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, column);
    }

    @SuppressWarnings("unchecked")
    default SELF orderBy(IOrderByDirection orderByDirection, COLUMN... columns) {
        for (COLUMN column : columns) {
            this.orderBy(orderByDirection, column);
        }
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, COLUMN... columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, columns);
    }

    default SELF orderBy(IOrderByDirection orderByDirection, List<COLUMN> columns) {
        for (COLUMN column : columns) {
            this.orderBy(orderByDirection, column);
        }
        return (SELF) this;
    }

    default SELF orderBy(boolean when, IOrderByDirection orderByDirection, List<COLUMN> columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.orderBy(orderByDirection, columns);
    }
}
