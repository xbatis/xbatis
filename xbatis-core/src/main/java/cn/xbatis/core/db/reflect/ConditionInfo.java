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
import cn.xbatis.core.util.FieldUtil;
import cn.xbatis.db.Logic;
import cn.xbatis.db.annotations.Condition;
import cn.xbatis.db.annotations.ConditionGroup;
import cn.xbatis.db.annotations.ConditionTarget;
import db.sql.api.impl.cmd.struct.ConditionChain;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ConditionInfo {

    private final List<ConditionItemGroup> conditionItemGroups;

    public ConditionInfo(Class<?> clazz) {
        List<Field> fieldList = FieldUtil.getFields(clazz);
        List<ConditionItem> conditions = new ArrayList<>(fieldList.size());

        Class<?> targetTable;

        final Logic logic;
        if (clazz.isAnnotationPresent(ConditionTarget.class)) {
            ConditionTarget conditionTarget = clazz.getAnnotation(ConditionTarget.class);
            targetTable = conditionTarget.value();
            logic = conditionTarget.logic();
        } else {
            targetTable = clazz;
            logic = Logic.AND;
        }

        Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();
        for (Field field : fieldList) {
            Condition condition = field.getAnnotation(Condition.class);
            if (condition != null && condition.value() == Condition.Type.IGNORE) {
                continue;
            }

            TableInfo tableInfo;
            if (condition == null) {
                tableInfo = tableInfoMap.computeIfAbsent(targetTable, k -> Tables.get(targetTable));
            } else if (condition.target() == Void.class) {
                tableInfo = tableInfoMap.computeIfAbsent(targetTable, k -> Tables.get(targetTable));
            } else {
                tableInfo = tableInfoMap.computeIfAbsent(targetTable, k -> Tables.get(condition.target()));
            }

            String property = field.getName();
            if (condition != null && !condition.property().isEmpty()) {
                property = condition.property();
            }
            TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(property);
            if (tableFieldInfo == null) {
                throw new RuntimeException("can not find entity property " + property + " in entity class " + tableInfo.getType());
            }
            conditions.add(new ConditionItem(field, tableFieldInfo, condition));
        }

        Map<String, ConditionItem> conditionItemMap = conditions.stream().collect(Collectors.toMap(i -> i.getField().getName(), i -> i));

        ConditionGroup[] conditionGroups = clazz.getAnnotationsByType(ConditionGroup.class);
        Map<ConditionGroup, ConditionItemGroup> conditionItemGroupMap = new HashMap<>();
        Set<String> usedConditionFields = new HashSet<>();
        for (ConditionGroup key : conditionGroups) {
            ConditionItemGroup group = conditionItemGroupMap.computeIfAbsent(key, i -> {
                return new ConditionItemGroup(false, logic, key.logic(), new ArrayList<>());
            });

            for (String field : key.value()) {
                if (!conditionItemMap.containsKey(field)) {
                    throw new RuntimeException("class " + clazz + " have no field: " + field);
                }
                group.getConditionItems().add(conditionItemMap.get(field));
                usedConditionFields.add(field);
            }
        }

        List<ConditionItemGroup> itemGroups = new ArrayList<>();

        for (ConditionItem i : conditions) {
            if (usedConditionFields.contains(i.getField().getName())) {
                continue;
            }
            itemGroups.add(new ConditionItemGroup(true, logic, null, i));
        }

        conditionItemGroupMap.entrySet().stream().forEach(entry -> {
            itemGroups.add(entry.getValue());
        });

        this.conditionItemGroups = itemGroups;
    }

    public void appendCondition(ConditionChain conditionChain, Object target) {
        if (target == null) {
            return;
        }

        if (target instanceof ObjectConditionLifeCycle) {
            ObjectConditionLifeCycle objectConditionLifeCycle = (ObjectConditionLifeCycle) target;
            objectConditionLifeCycle.beforeBuildCondition();
        }
        this.conditionItemGroups.stream().forEach(i -> i.appendCondition(conditionChain, target));
    }
}
