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

package cn.xbatis.core.sql.util;

import cn.xbatis.core.db.reflect.ForeignInfo;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.sql.executor.MpTable;
import db.sql.api.impl.cmd.struct.ConditionChain;

/**
 * 外键util
 */
public final class ForeignKeyUtil {

    private ForeignKeyUtil() {
    }


    /**
     * 构建外键on的Consumer
     *
     * @param mainTable      主表
     * @param secondTable    副表
     * @param conditionChain 条件类
     */
    public static void addForeignKeyCondition(MpTable mainTable, MpTable secondTable, ConditionChain conditionChain) {
        TableInfo mainTableInfo = mainTable.getTableInfo();
        TableInfo secondTableInfo = secondTable.getTableInfo();
        ForeignInfo foreignInfo;
        if ((foreignInfo = secondTableInfo.getForeignInfo(mainTableInfo.getType())) != null) {
            TableFieldInfo foreignFieldInfo = foreignInfo.getTableFieldInfo();
            conditionChain.eq(mainTable.$(mainTableInfo.getSingleIdFieldInfo(true).getColumnName()), secondTable.$(foreignFieldInfo.getColumnName()));
        } else if ((foreignInfo = mainTableInfo.getForeignInfo(secondTableInfo.getType())) != null) {
            TableFieldInfo foreignFieldInfo = foreignInfo.getTableFieldInfo();
            conditionChain.eq(secondTable.$(secondTableInfo.getSingleIdFieldInfo(true).getColumnName()), mainTable.$(foreignFieldInfo.getColumnName()));
        }
    }
}
