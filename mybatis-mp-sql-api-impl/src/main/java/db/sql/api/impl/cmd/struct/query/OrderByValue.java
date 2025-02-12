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

package db.sql.api.impl.cmd.struct.query;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IOrderByDirection;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.condition.IsNull;
import db.sql.api.impl.cmd.dbFun.Case;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;

public class OrderByValue implements Cmd {

    private final Cmd key;

    private final IOrderByDirection orderByDirection;

    public OrderByValue(IOrderByDirection orderByDirection, Cmd key) {
        if (key instanceof IOrderByDirection) {
            throw new RuntimeException();
        }
        this.key = key;
        this.orderByDirection = orderByDirection;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (!IOrderByDirection.isSupportNullsOrder(context.getDbType())) {
            Case nullDirectionCmd = null;
            if (orderByDirection == OrderByDirection.DESC_NULLS_FIRST || orderByDirection == OrderByDirection.ASC_NULLS_FIRST) {
                nullDirectionCmd = Methods.case_().when(new IsNull(this.key), 1).else_(0);
            } else if (orderByDirection == OrderByDirection.DESC_NULLS_LAST || orderByDirection == OrderByDirection.ASC_NULLS_LAST) {
                nullDirectionCmd = Methods.case_().when(new IsNull(this.key), 0).else_(1);
            }
            if (Objects.nonNull(nullDirectionCmd)) {
                nullDirectionCmd.sql(module, parent, context, sqlBuilder);
                sqlBuilder.append(SqlConst.DELIMITER);
            }
        }

        key.sql(module, this, context, sqlBuilder);
        orderByDirection.sql(module, this.key, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key);
    }
}
