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

import cn.xbatis.core.sql.ObjectConditionLifeCycle;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.util.FieldUtil;
import cn.xbatis.db.Logic;
import cn.xbatis.db.annotations.Condition;
import cn.xbatis.db.annotations.ConditionTarget;
import cn.xbatis.db.annotations.OrderBy;
import cn.xbatis.db.annotations.OrderByTarget;
import db.sql.api.impl.cmd.struct.ConditionChain;

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
        Class<?> targetTable = orderByTarget.value();
        Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();
        for(Field field : fieldList){
            orderByList.add(this.parseOrderByAnnotation(field,targetTable,tableInfoMap));
        }
        this.orderByItems=orderByList;
    }

    private OrderByItem parseOrderByAnnotation(Field field, Class<?> targetTable, Map<Class<?>, TableInfo> tableInfoMap) {
        OrderBy condition= field.getAnnotation(OrderBy.class);
        TableInfo tableInfo;
        if (condition == null || condition.target() == Void.class) {
            tableInfo = tableInfoMap.computeIfAbsent(targetTable, k -> Tables.get(targetTable));
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

    public void appendOrderBy(BaseQuery<?,?> query, Object target) {
        if (target == null) {
            return;
        }
        this.orderByItems.stream().forEach(i -> i.appendOrderBy(query, target));
    }
}
