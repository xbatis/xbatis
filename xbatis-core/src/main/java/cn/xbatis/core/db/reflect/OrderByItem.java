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


import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.db.annotations.OrderBy;
import db.sql.api.Cmd;
import db.sql.api.impl.cmd.Methods;

import java.lang.reflect.Field;

/**
 * 被注解的字段必须为 Integer Boolean 类型
 * 1 true 代表升序 0 false 代表 倒序
 */
public class OrderByItem {

    private final Field field;

    private final Object orderByInfo;

    private final int storey;
    private final Integer ZERO = 0;

    public OrderByItem(Field field, Object orderByInfo, OrderBy annotation) {
        field.setAccessible(true);
        this.field = field;
        this.orderByInfo = orderByInfo;
        this.storey = annotation == null ? 1 : annotation.storey();
    }

    public OrderByItem(Field field, Object orderByInfo) {
        field.setAccessible(true);
        this.field = field;
        this.orderByInfo = orderByInfo;
        this.storey = 0;
    }

    public void appendOrderBy(BaseQuery<?, ?> query, Object target) {
        Object value;
        try {
            value = this.field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (value == null) {
            return;
        }
        Cmd column;
        if (orderByInfo instanceof TableFieldInfo) {
            FieldInfo fieldInfo = ((TableFieldInfo) this.orderByInfo).getFieldInfo();
            column = query.$().field(fieldInfo.getClazz(), fieldInfo.getField().getName(), this.storey);
        } else {
            column = Methods.column((String) orderByInfo);
        }

        if (Boolean.FALSE.equals(value) || ZERO.equals(value)) {
            query.orderByDesc(column);
        } else {
            query.orderBy(column);
        }
    }
}
