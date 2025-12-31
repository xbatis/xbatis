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

package cn.xbatis.core.sql.listener;

import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.sql.executor.BaseUpdate;
import cn.xbatis.core.sql.executor.MpTable;
import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.executor.IDelete;
import db.sql.api.cmd.executor.IExecutor;
import db.sql.api.cmd.executor.IInsert;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.cmd.struct.IOn;
import db.sql.api.impl.cmd.executor.AbstractDelete;
import db.sql.api.impl.cmd.executor.AbstractQuery;
import db.sql.api.impl.cmd.executor.AbstractUpdate;
import db.sql.api.impl.cmd.struct.On;
import db.sql.api.impl.cmd.struct.Where;

public class LogicDeleteSQLListener implements SQLListener {

    private void addConditionToWhere(Object source, IDataset<?, ?> dataset) {
        if (!(dataset instanceof MpTable) || !(source instanceof IExecutor)) {
            return;
        }
        if (source instanceof IInsert || source instanceof IDelete) {
            return;
        }
        Where where;
        if (source instanceof AbstractDelete) {
            AbstractDelete delete = (AbstractDelete) source;
            where = delete.$where();
        } else if (source instanceof AbstractUpdate) {
            BaseUpdate update = (BaseUpdate) source;
            where = update.$where();
        } else if (source instanceof AbstractQuery) {
            AbstractQuery query = (AbstractQuery) source;
            where = query.$where();
        } else {
            throw new RuntimeException("not support type:" + source.getClass());
        }
        LogicDeleteUtil.addLogicDeleteCondition((MpTable) dataset, where);
    }

    private void addConditionToOn(IDataset<?, ?> dataset, IOn on) {
        if (!(dataset instanceof MpTable) || !(on instanceof On)) {
            return;
        }
        LogicDeleteUtil.addLogicDeleteCondition((MpTable) dataset, (On) on);
    }

    @Override
    public void onFrom(Object source, IDataset<?, ?> dataset) {
        this.addConditionToWhere(source, dataset);
    }

    @Override
    public void onUpdate(Object source, IDataset<?, ?> dataset) {
        this.addConditionToWhere(source, dataset);
    }

    @Override
    public void onJoin(Object source, JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, IOn<?, ?, ?, ?, ?, ?, ?> on) {
        this.addConditionToOn(secondTable, on);
    }
}