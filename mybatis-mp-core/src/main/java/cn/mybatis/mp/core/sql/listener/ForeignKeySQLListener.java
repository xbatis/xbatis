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

package cn.mybatis.mp.core.sql.listener;

import cn.mybatis.mp.core.sql.executor.MpTable;
import cn.mybatis.mp.core.sql.util.ForeignKeyUtil;
import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.cmd.struct.IOn;
import db.sql.api.impl.cmd.struct.ConditionChain;

/**
 * JOIN时自动添加on条件
 */
public class ForeignKeySQLListener implements SQLListener {

    @Override
    public void onJoin(Object source, JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, IOn<?, ?, ?, ?, ?, ?, ?> on) {
        if (!(mainTable instanceof MpTable) || !(secondTable instanceof MpTable)) {
            return;
        }
        if (on.conditionChain().hasContent()) {
            return;
        }
        ForeignKeyUtil.addForeignKeyCondition((MpTable) mainTable, (MpTable) secondTable, (ConditionChain) on.conditionChain());
    }
}
