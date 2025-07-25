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

import cn.xbatis.db.Logic;
import db.sql.api.impl.cmd.struct.ConditionChain;
import lombok.Data;

import java.util.List;

@Data
public class ConditionItemGroup {

    private final boolean root;

    private final Logic rootLogic;

    private final Logic subLogic;

    //里面可能是ConditionItem 或这 ConditionsItem
    private List<Object> conditionItems;

    public ConditionItemGroup(boolean root, Logic rootLogic, Logic subLogic, List<Object> conditionItems) {
        this.root = root;
        this.rootLogic = rootLogic;
        this.subLogic = subLogic;
        this.conditionItems = conditionItems;
    }

    private static void appendCondition(ConditionChain conditionChain, Object target, Logic logic, List<?> conditionItems) {
        conditionItems.stream().forEach(i -> {
            if (i instanceof ConditionItem) {
                ((ConditionItem) i).appendCondition(conditionChain, target);
                if (logic == Logic.AND) {
                    conditionChain.and();
                } else {
                    conditionChain.or();
                }
            } else {
                ConditionsItem conditionsItem = (ConditionsItem) i;
                if (logic == Logic.AND) {
                    conditionChain.andNested(c -> {
                        appendCondition(c, target, conditionsItem.getAnnotation().logic(), conditionsItem.getConditionItemList());
                    });
                } else {
                    conditionChain.orNested(c -> {
                        appendCondition(c, target, conditionsItem.getAnnotation().logic(), conditionsItem.getConditionItemList());
                    });
                }
            }
        });
    }

    public void appendCondition(ConditionChain conditionChain, Object target) {
        if (root) {
            if (rootLogic == Logic.AND) {
                conditionChain.and();
            } else {
                conditionChain.or();
            }
            appendCondition(conditionChain, target, rootLogic, this.conditionItems);
        } else {
            if (rootLogic == Logic.AND) {
                conditionChain.andNested(c -> {
                    appendCondition(c, target, subLogic, this.conditionItems);
                });
            } else {
                conditionChain.orNested(c -> {
                    appendCondition(c, target, subLogic, this.conditionItems);
                });
            }
        }
    }
}
