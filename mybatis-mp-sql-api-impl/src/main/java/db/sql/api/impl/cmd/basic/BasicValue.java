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

package db.sql.api.impl.cmd.basic;


import db.sql.api.Cmd;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.condition.Like;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;

public class BasicValue extends AbstractField<BasicValue> {

    private final Object value;

    public BasicValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        Object originValue = this.value;

        if (value instanceof ValueWrap) {
            originValue = ((ValueWrap) value).getOriginValue();
        }

        if (Objects.nonNull(originValue) && originValue instanceof String && parent instanceof Like && ((Like) parent).getMode() != LikeMode.NONE) {
            originValue = originValue.toString().replaceAll("%", "\\\\%").replaceAll("_", "\\\\_%");
        }
        if (context.getSqlMode() == SQLMode.PRINT) {
            if (originValue instanceof Number) {
                sqlBuilder.append(originValue);
            } else {
                sqlBuilder.append(SqlConst.SINGLE_QUOT).append(originValue).append(SqlConst.SINGLE_QUOT);
            }
        } else {
            sqlBuilder.append(context.addParam(value));
        }

        if (parent instanceof Select) {
            if (Objects.nonNull(this.getAlias())) {
                sqlBuilder.append(SqlConst.AS(context.getDbType())).append(this.getAlias());
            }
        }

        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.value);
    }
}
