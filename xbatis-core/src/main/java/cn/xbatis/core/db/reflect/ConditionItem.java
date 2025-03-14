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

package cn.xbatis.core.db.reflect;

import cn.xbatis.db.annotations.Condition;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.struct.Where;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Collection;

@Data
public class ConditionItem {

    private final Field field;

    private final TableFieldInfo tableFieldInfo;

    private final Condition.Type type;

    private final int storey;

    private final Condition.LikeMode likeMode;

    public ConditionItem(Field field, TableFieldInfo tableFieldInfo, Condition annotation) {
        field.setAccessible(true);
        this.field = field;
        this.tableFieldInfo = tableFieldInfo;
        if (annotation == null) {
            this.type = Condition.Type.EQ;
            this.storey = 1;
            this.likeMode = null;
        } else {
            this.type = annotation.value();
            this.storey = annotation.storey();
            this.likeMode = annotation.likeMode();
        }
    }

    public void appendCondition(Where where, Object target) {
        Object value;
        try {
            value = this.field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (value == null) {
            return;
        }
        if (value instanceof String && ((String) value).isEmpty()) {
            return;
        }
        if (value instanceof Collection && ((Collection) value).isEmpty()) {
            return;
        }

        if (value instanceof Object[] && ((Object[]) value).length < 1) {
            return;
        }
        CmdFactory cmdFactory = where.getConditionFactory().getCmdFactory();
        FieldInfo fieldInfo = this.tableFieldInfo.getFieldInfo();
        TableField tableField = cmdFactory.field(fieldInfo.getClazz(), fieldInfo.getField().getName(), this.storey);
        switch (this.type) {
            case EQ: {
                where.eq(tableField, value);
                break;
            }
            case NE: {
                where.ne(tableField, value);
                break;
            }
            case IN: {
                if (value instanceof Collection) {
                    where.in(tableField, (Collection) value);
                } else {
                    where.in(tableField, (Object[]) value);
                }
                break;
            }
            case LT: {
                where.lt(tableField, value);
                break;
            }

            case LTE: {
                where.lte(tableField, value);
                break;
            }

            case GT: {
                where.gt(tableField, value);
                break;
            }

            case GTE: {
                where.gte(tableField, value);
                break;
            }

            case LIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Like value must be String");
                }
                where.like(LikeMode.valueOf(this.likeMode.name()), tableField, (String) value);
                break;
            }

            case NOT_LIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Not like value must be String");
                }
                where.notLike(LikeMode.valueOf(this.likeMode.name()), tableField, (String) value);
                break;
            }
        }
    }
}
