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
import cn.xbatis.core.util.FieldUtil;
import cn.xbatis.db.annotations.OrderBy;
import cn.xbatis.db.annotations.OrderByAsField;
import cn.xbatis.db.annotations.OrderByColumn;
import cn.xbatis.db.annotations.OrderByTarget;
import db.sql.api.impl.tookit.SqlUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderByInfo {

    private final List<OrderByItem> orderByItems;

    public OrderByInfo(Class<?> clazz) {
        List<Field> fieldList = FieldUtil.getFields(clazz);
        List<OrderByItem> orderByList = new ArrayList<>(fieldList.size());
        OrderByTarget orderByTarget = clazz.getAnnotation(OrderByTarget.class);

        Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();
        for (Field field : fieldList) {
            OrderByItem orderByItem = this.parseOrderByAnnotation(field, orderByTarget, tableInfoMap);
            if (orderByItem == null) {
                continue;
            }
            orderByList.add(orderByItem);
        }
        this.orderByItems = orderByList;
    }

    private OrderByItem parseOrderByAnnotation(Field field, OrderByTarget orderByTarget, Map<Class<?>, TableInfo> tableInfoMap) {

        if (field.isAnnotationPresent(OrderByColumn.class)) {
            OrderByColumn orderByColumn = field.getAnnotation(OrderByColumn.class);
            return new OrderByItem(field, orderByColumn.value());
        } else if (field.isAnnotationPresent(OrderByAsField.class)) {
            OrderByAsField orderByColumn = field.getAnnotation(OrderByAsField.class);
            if (orderByColumn.property().isEmpty()) {
                return new OrderByItem(field, SqlUtil.getAsName(orderByColumn.target(), field.getName()));
            }
            return new OrderByItem(field, SqlUtil.getAsName(orderByColumn.target(), orderByColumn.property()));
        }

        OrderBy condition = field.getAnnotation(OrderBy.class);
        if (orderByTarget.strict() && condition == null) {
            return null;
        }
        TableInfo tableInfo;
        if (condition == null || condition.target() == Void.class) {
            tableInfo = tableInfoMap.computeIfAbsent(orderByTarget.value(), k -> Tables.get(orderByTarget.value()));
        } else {
            tableInfo = tableInfoMap.computeIfAbsent(condition.target(), k -> Tables.get(condition.target()));
        }

        String property = field.getName();
        if (condition != null && !condition.property().isEmpty()) {
            property = condition.property();
        }
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(property);
        if (tableFieldInfo == null) {
            throw new RuntimeException("can not find entity property " + property + " in entity " + tableInfo.getType());
        }
        return new OrderByItem(field, tableFieldInfo, condition);
    }

    public void appendOrderBy(BaseQuery<?, ?> query, Object target) {
        if (target == null) {
            return;
        }
        this.orderByItems.stream().forEach(i -> i.appendOrderBy(query, target));
    }
}
