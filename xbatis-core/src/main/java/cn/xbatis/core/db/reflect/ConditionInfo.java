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

import cn.xbatis.core.exception.NotTableFieldException;
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
        List<Object> conditionList = new ArrayList<>(fieldList.size());

        Class<?> targetTable;

        final Logic logic;
        int parentStorey = 1;
        if (clazz.isAnnotationPresent(ConditionTarget.class)) {
            ConditionTarget conditionTarget = clazz.getAnnotation(ConditionTarget.class);
            targetTable = conditionTarget.value();
            logic = conditionTarget.logic();
            parentStorey = conditionTarget.storey();
        } else {
            targetTable = clazz;
            logic = Logic.AND;
        }

        Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();
        for (Field field : fieldList) {
            cn.xbatis.db.annotations.Conditions conditions = field.getAnnotation(cn.xbatis.db.annotations.Conditions.class);
            if (conditions != null) {
                List<ConditionItem> subList = new ArrayList<>();
                for (Condition condition : conditions.value()) {
                    ConditionItem conditionItem = this.parseConditionAnnotation(clazz, parentStorey, field, condition, targetTable, tableInfoMap);
                    if (conditionItem != null) {
                        subList.add(conditionItem);
                    }
                }
                if (subList.isEmpty()) {
                    continue;
                }
                ConditionsItem conditionsItem = new ConditionsItem(new FieldInfo(clazz, field), conditions, subList);
                conditionList.add(conditionsItem);
            } else {
                Condition condition = field.getAnnotation(Condition.class);
                ConditionItem conditionItem = this.parseConditionAnnotation(clazz, parentStorey, field, condition, targetTable, tableInfoMap);
                if (conditionItem != null) {
                    conditionList.add(conditionItem);
                }
            }
        }

        Map<String, Object> conditionItemMap = conditionList.stream().collect(Collectors.toMap(i -> {
            if (i instanceof ConditionItem) {
                return ((ConditionItem) i).getFieldInfo().getField().getName();
            } else {
                return ((ConditionsItem) i).getFieldInfo().getField().getName();
            }
        }, i -> i));

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

        for (Object i : conditionList) {
            String fieldName;
            if (i instanceof ConditionItem) {
                fieldName = ((ConditionItem) i).getFieldInfo().getField().getName();
            } else {
                fieldName = ((ConditionsItem) i).getFieldInfo().getField().getName();
            }
            if (usedConditionFields.contains(fieldName)) {
                continue;
            }
            itemGroups.add(new ConditionItemGroup(true, logic, null, Collections.singletonList(i)));
        }

        conditionItemGroupMap.entrySet().stream().forEach(entry -> {
            itemGroups.add(entry.getValue());
        });

        this.conditionItemGroups = itemGroups;
    }

    private ConditionItem parseConditionAnnotation(Class<?> clazz, int parentStorey, Field field, Condition condition, Class<?> targetTable, Map<Class<?>, TableInfo> tableInfoMap) {
        if (condition != null && condition.value() == Condition.Type.IGNORE) {
            return null;
        }

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
            throw new NotTableFieldException(clazz, "", tableInfo.getType(), property);
        }
        return new ConditionItem(parentStorey, new FieldInfo(clazz, field), tableFieldInfo, condition);
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

        if (target instanceof ObjectConditionLifeCycle) {
            ObjectConditionLifeCycle objectConditionLifeCycle = (ObjectConditionLifeCycle) target;
            objectConditionLifeCycle.afterBuildCondition(conditionChain);
        }
    }
}
