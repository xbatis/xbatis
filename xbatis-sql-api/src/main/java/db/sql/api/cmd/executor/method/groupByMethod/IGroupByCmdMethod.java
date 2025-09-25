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

package db.sql.api.cmd.executor.method.groupByMethod;


import db.sql.api.Cmd;

import java.util.List;
import java.util.function.Supplier;

public interface IGroupByCmdMethod<SELF extends IGroupByCmdMethod, COLUMN extends Cmd> {

    SELF groupBy(COLUMN column);

    default SELF groupBy(boolean when, COLUMN column) {
        if (!when) {
            return (SELF) this;
        }
        return this.groupBy(column);
    }

    default SELF groupBy(Supplier<COLUMN> supplier) {
        COLUMN column = supplier.get();
        if (column == null) {
            return (SELF) this;
        }
        return this.groupBy(column);
    }

    default SELF groupBy(boolean when, Supplier<COLUMN> supplier) {
        if (!when) {
            return (SELF) this;
        }
        return this.groupBy(supplier);
    }

    @SuppressWarnings("unchecked")
    default SELF groupBy(COLUMN... columns) {
        for (COLUMN column : columns) {
            this.groupBy(column);
        }
        return (SELF) this;
    }


    default SELF groupBy(List<COLUMN> columns) {
        for (COLUMN column : columns) {
            this.groupBy(column);
        }
        return (SELF) this;
    }

    default SELF groupBy(boolean when, List<COLUMN> columns) {
        if (!when) {
            return (SELF) this;
        }
        return this.groupBy(columns);
    }
}
