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

import cn.xbatis.core.sql.executor.BaseUpdate;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import cn.xbatis.core.util.OptimisticLockUtil;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.executor.IExecutor;
import db.sql.api.cmd.listener.SQLListener;
/**
 * 乐观锁SQL监听器
 */
public class OptimisticLockSQLListener implements SQLListener {

    @Override
    public void onUpdate(Object source, IDataset<?, ?> dataset) {
        if (!(dataset instanceof MpTable) || !(source instanceof BaseUpdate)) {
            return;
        }
        MpTable  mpTable = (MpTable) dataset;

        if(source instanceof UpdateChain){
            UpdateChain  update = (UpdateChain) source;
            if(update.isOptimisticLock()){
                OptimisticLockUtil.versionPlus1(mpTable.getTableInfo(),update);
            }
        }else {
            BaseUpdate<?>  update = (BaseUpdate<?>) source;
            OptimisticLockUtil.versionPlus1(mpTable.getTableInfo(),update);
        }

    }
}
