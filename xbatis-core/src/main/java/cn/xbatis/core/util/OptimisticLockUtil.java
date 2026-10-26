/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.context.CmdParamUtil;
import cn.xbatis.core.mybatis.mapper.context.VersionValue;
import cn.xbatis.core.sql.executor.BaseUpdate;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.ConditionBlock;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.condition.Eq;
import db.sql.api.impl.cmd.struct.ConditionChain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class OptimisticLockUtil {

    /**
     * update set version = version + 1
     *
     * @param tableInfo
     * @param update
     * @return 版本后 +1 是否成功
     */
    public static boolean versionPlus1(TableInfo tableInfo, BaseUpdate<?> update) {
        if (tableInfo.getVersionFieldInfo() == null) {
            return false;
        }

        //不管有没有乐观锁值，都加上set version +1 或者 设置新的时间
        Table table = update.$().table(tableInfo.getType());
        db.sql.api.impl.cmd.basic.TableField versionField = update.$().field(table, tableInfo.getVersionFieldInfo().getColumnName());

        Object versionOldValue = null;
        if (tableInfo.getVersionFieldInfo().getFieldInfo().getTypeClass() == LocalDateTime.class) {
            versionOldValue = getNewVersionValueFormUpdate(tableInfo, update);
            if (versionOldValue == null) {
                versionOldValue = LocalDateTime.now();
            }
        } else if (tableInfo.getVersionFieldInfo().getFieldInfo().getTypeClass() == Date.class) {
            versionOldValue = getNewVersionValueFormUpdate(tableInfo, update);
            if (versionOldValue == null) {
                versionOldValue = new Date(System.currentTimeMillis());
            }
        }

        if (versionOldValue == null) {
            versionOldValue = Methods.tpl("{0} + 1", versionField);
        }

        update.set(versionField, CmdParamUtil.build(tableInfo.getVersionFieldInfo(), versionOldValue));
        return true;
    }

    private static Object getNewVersionValueFormUpdate(TableInfo tableInfo, BaseUpdate<?> update) {
        ConditionChain extConditionChain = update.$where().getExtConditionChain();
        if (extConditionChain != null && extConditionChain.hasContent()) {
            for (ConditionBlock conditionBlock : extConditionChain.getConditionBlocks()) {
                if (conditionBlock.getCondition() instanceof Eq) {
                    Eq eq = (Eq) conditionBlock.getCondition();
                    if (eq.getValue() instanceof VersionValue) {
                        return ((VersionValue) eq.getValue()).getNewVersion();
                    }
                }
            }
        }
        return null;
    }


    /**
     * 添加 version条件
     *
     * @param where
     * @param tableInfo
     * @param entity
     * @param <T>
     */
    public static <T> void appendVersionWhere(db.sql.api.impl.cmd.struct.Where where, TableInfo tableInfo, T entity) {
        TableFieldInfo versionFieldInfo = tableInfo.getVersionFieldInfo();
        if (Objects.isNull(versionFieldInfo)) {
            return;
        }

        Object version = versionFieldInfo.getValue(entity);
        if (Objects.isNull(version)) {
            return;
        }

        CmdFactory $ = where.getConditionFactory().getCmdFactory();
        where.eq($.field(entity.getClass(), versionFieldInfo.getField().getName(), 1), CmdParamUtil.build(versionFieldInfo, version));
    }
}
