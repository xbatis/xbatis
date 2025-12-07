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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.Condition;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.struct.ConditionChain;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@Data
public class ConditionItem {

    private final FieldInfo fieldInfo;

    private final TableFieldInfo tableFieldInfo;

    private final Condition.Type type;

    private final int storey;

    private final LikeMode likeMode;

    private final Condition annotation;

    private final Object defaultValue;

    public ConditionItem(FieldInfo fieldInfo, TableFieldInfo tableFieldInfo, Condition annotation) {
        fieldInfo.getField().setAccessible(true);
        this.fieldInfo = fieldInfo;
        this.tableFieldInfo = tableFieldInfo;
        this.annotation = annotation;
        if (annotation == null) {
            this.type = Condition.Type.EQ;
            this.storey = 1;
            this.likeMode = null;
        } else {
            this.type = annotation.value();
            this.storey = annotation.storey();
            this.likeMode = LikeMode.valueOf(annotation.likeMode().name());
        }

        if (annotation != null) {
            if (annotation.defaultValue().isEmpty()) {
                this.defaultValue = null;
            } else if (!annotation.defaultValue().contains("{")) {
                this.defaultValue = TypeConvertUtil.convert(annotation.defaultValue(), fieldInfo.getTypeClass());
            } else {
                this.defaultValue = null;
            }
        } else {
            this.defaultValue = null;
        }
    }

    private Date toEndDayTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Object toEndDayTime(Object param) {
        if (param == null) {
            return null;
        }
        if (param instanceof LocalDate) {
            return ((LocalDate) param).atStartOfDay().plusDays(1).minusNanos(1);
        } else if (param instanceof Date) {
            return toEndDayTime((Date) param);
        } else if (param instanceof Long) {
            return toEndDayTime(new Date((Long) param));
        }
        if (param instanceof LocalDateTime) {
            return ((LocalDateTime) param).toLocalDate().atStartOfDay().plusDays(1).minusNanos(1);
        } else if (param instanceof String) {
            String p = (String) param;
            if (p.length() == 10) {
                return p + " 23:59:59";
            }
        }
        return param;
    }

    private Object getDefaultValue(Object target) {
        if (this.defaultValue != null) {
            return this.defaultValue;
        } else if (annotation != null && annotation.defaultValue().contains("{")) {
            return XbatisGlobalConfig.getDefaultValue(target.getClass(), fieldInfo.getTypeClass(), annotation.defaultValue());
        } else {
            return null;
        }
    }

    public void appendCondition(ConditionChain conditionChain, Object target) {
        Object value;
        try {
            value = this.fieldInfo.getField().get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (value == null) {
            value = getDefaultValue(target);
        } else if (value instanceof String) {
            String v = ((String) value).trim();
            if (v.isEmpty()) {
                value = getDefaultValue(target);
            } else {
                value = v;
            }
        } else if (value instanceof Collection && ((Collection) value).isEmpty()) {
            value = getDefaultValue(target);
        } else if (value instanceof Object[] && ((Object[]) value).length < 1) {
            value = getDefaultValue(target);
        }

        if (value == null) {
            return;
        }

        if (this.annotation != null && this.annotation.cast()) {
            value = TypeConvertUtil.convert(value, tableFieldInfo.getFieldInfo().getTypeClass());
        }

        if (annotation.toEndDayTime()) {
            value = toEndDayTime(value);
        }

        CmdFactory cmdFactory = conditionChain.getConditionFactory().getCmdFactory();
        FieldInfo fieldInfo = this.tableFieldInfo.getFieldInfo();
        TableField tableField = cmdFactory.field(fieldInfo.getClazz(), fieldInfo.getField().getName(), this.storey);
        switch (this.type) {
            case EQ: {
                conditionChain.eq(tableField, value);
                break;
            }
            case NE: {
                conditionChain.ne(tableField, value);
                break;
            }
            case IN: {
                if (value instanceof Collection) {
                    conditionChain.in(tableField, (Collection) value);
                } else {
                    conditionChain.in(tableField, (Object[]) value);
                }
                break;
            }
            case LT: {
                conditionChain.lt(tableField, value);
                break;
            }

            case LTE: {
                conditionChain.lte(tableField, value);
                break;
            }

            case GT: {
                conditionChain.gt(tableField, value);
                break;
            }

            case GTE: {
                conditionChain.gte(tableField, value);
                break;
            }

            case LIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Like value must be String");
                }
                conditionChain.like(this.likeMode, tableField, value);
                break;
            }

            case NOT_LIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Not like value must be String");
                }
                conditionChain.notLike(this.likeMode, tableField, value);
                break;
            }

            case ILIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Like value must be String");
                }
                conditionChain.iLike(this.likeMode, tableField, value);
                break;
            }

            case NOT_ILIKE: {
                if (!(value instanceof String)) {
                    throw new RuntimeException("Not like value must be String");
                }
                conditionChain.notILike(this.likeMode, tableField, value);
                break;
            }

            case BETWEEN: {
                Object[] array;
                if (value instanceof Object[]) {
                    array = (Object[]) value;
                } else if (value instanceof Collection) {
                    Collection list = (Collection) value;
                    array = list.toArray();
                } else {
                    throw new RuntimeException("Not support type : " + value.getClass());
                }

                conditionChain.between(tableField, array[0], annotation != null && annotation.toEndDayTime() ? toEndDayTime(array[1]) : array[1]);
                break;
            }

            case NULL: {
                if (isTrueOr1(value)) {
                    conditionChain.isNull(tableField);
                } else {
                    conditionChain.isNotNull(tableField);
                }
                break;
            }

            case NOT_NULL: {
                if (isTrueOr1(value)) {
                    conditionChain.isNotNull(tableField);
                } else {
                    conditionChain.isNull(tableField);
                }
                break;
            }

            case BLANK: {
                if (isTrueOr1(value)) {
                    conditionChain.empty(tableField);
                } else {
                    conditionChain.notEmpty(tableField);
                }
                break;
            }

            case NOT_BLANK: {
                if (isTrueOr1(value)) {
                    conditionChain.notEmpty(tableField);
                } else {
                    conditionChain.empty(tableField);
                }
                break;
            }
        }
    }

    private boolean isTrueOr1(Object value) {
        String str = value.toString();
        return str.equalsIgnoreCase("true") || str.equals("1") ? true : false;
    }
}
