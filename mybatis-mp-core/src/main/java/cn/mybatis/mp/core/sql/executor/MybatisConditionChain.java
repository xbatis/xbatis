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

package cn.mybatis.mp.core.sql.executor;

import cn.mybatis.mp.core.sql.TableSplitUtil;
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
        super.appendCondition(connector, condition);
        this.handleTableSplit(condition);
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
        if (!table.getTableInfo().isSplitTable()) {
            return;
        }
        if (!table.getTableInfo().getTableName().equals(table.getName())) {
            //这里已经修改过了
            return;
        }
        TableSplitUtil.splitHandle(table, c.getValue());
    }
}
