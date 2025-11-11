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

package cn.xbatis.core.util;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.sql.executor.BaseUpdate;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.Table;

public class OptimisticLockUtil {

    /**
     * update set version = version + 1
     * @param tableInfo
     * @param update
     * @return
     */
    public static boolean versionPlus1(TableInfo tableInfo, BaseUpdate<?> update) {
        //不管有没有乐观锁，都加上set version +1
        if(tableInfo.getVersionFieldInfo() == null){
            return false;
        }

        Table table = update.$().table(tableInfo.getType());
        db.sql.api.impl.cmd.basic.TableField versionField = update.$().field(table, tableInfo.getVersionFieldInfo().getColumnName());
        update.set(versionField,Methods.tpl("{0} + 1",versionField));
        return true;
    }
}
