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

package cn.xbatis.core.sql.executor;

import cn.xbatis.core.sql.TableSplitUtil;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.Condition;
import db.sql.api.impl.cmd.basic.Connector;
import db.sql.api.impl.cmd.struct.ConditionChain;

public class MybatisConditionChain extends ConditionChain {

    public MybatisConditionChain(ConditionFactory conditionFactory) {
        super(conditionFactory);
    }

    public MybatisConditionChain(ConditionFactory conditionFactory, ConditionChain parent) {
        super(conditionFactory, parent);
    }

    @Override
    protected void appendCondition(Connector connector, ICondition condition) {
        if (isCanAppendToConditionChain(condition)) {
            super.appendCondition(connector, condition);
        }
        this.handleTableSplit(condition);
    }

    /**
     * 是否需要追加到condition chain里边
     *
     * @param condition
     * @return
     */
    private boolean isCanAppendToConditionChain(ICondition condition) {
        if (!(condition instanceof Condition)) {
            return true;
        }
        Condition c = (Condition) condition;
        if (!(c.getField() instanceof MpTableField)) {
            return true;
        }
        MpTableField tableField = (MpTableField) c.getField();
        if (!tableField.getTableFieldInfo().getTableFieldAnnotation().exists()) {
            return false;
        }
        return true;
    }

    private void handleTableSplit(ICondition condition) {
        if (!(condition instanceof Condition)) {
            return;
        }
        Condition c = (Condition) condition;
        if (!(c.getField() instanceof MpTableField)) {
            return;
        }
        MpTableField tableField = (MpTableField) c.getField();
        if (!tableField.getTableFieldInfo().isTableSplitKey()) {
            return;
        }
        MpTable table = (MpTable) tableField.getTable();
        if (!TableSplitUtil.isNeedSplitHandle(table)) {
            return;
        }
        TableSplitUtil.splitHandle(table, c.getValue());
    }
}
