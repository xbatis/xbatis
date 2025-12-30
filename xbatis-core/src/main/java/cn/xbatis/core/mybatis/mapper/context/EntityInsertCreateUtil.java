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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.db.reflect.OnListenerUtil;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableIds;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.incrementer.Generator;
import cn.xbatis.core.incrementer.GeneratorFactory;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Insert;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.sql.executor.MpTableField;
import cn.xbatis.core.tenant.TenantUtil;
import cn.xbatis.core.util.DefaultValueUtil;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityInsertCreateUtil {

    public static void initInsertValue(TableInfo tableInfo, TableFieldInfo tableFieldInfo, Object insertData, DbType dbType, Map<String, Object> defaultValueContext) {
        if (tableFieldInfo.isTableId()) {
            Object value = tableFieldInfo.getValue(insertData);
            if (value != null && IdUtil.isIdExists(insertData, tableFieldInfo)) {
                return;
            }
            if (!IdUtil.isIdValueExists(value)) {
                TableId tableId = TableIds.get(insertData.getClass(), dbType);
                if (tableId.value() == IdAutoType.GENERATOR) {
                    Generator generator = GeneratorFactory.getIdentifierGenerator(tableId.generatorName());
                    Object id = generator.nextId(tableInfo.getType());
                    IdUtil.setId(insertData, tableFieldInfo, id);
                }
            }
            return;
        }

        if (tableFieldInfo.isLogicDelete()) {
            Object value = tableFieldInfo.getValue(insertData);
            if (value != null) {
                return;
            }

            //逻辑删除字段
            //设置删除初始值
            value = tableFieldInfo.getLogicDeleteInitValue();
            if (value != null) {
                //逻辑删除初始值回写
                TableInfoUtil.setValue(tableFieldInfo, insertData, value);
            } else if (!StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().defaultValue())) {
                //读取回填 @TableField里的默认值
                DefaultValueUtil.getAndSetDefaultValue(insertData, tableFieldInfo, defaultValueContext);
            }
            return;
        }

        if (!StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().defaultValue())) {
            Object value = tableFieldInfo.getValue(insertData);
            if (value != null && !tableFieldInfo.getTableFieldAnnotation().defaultValueFillAlways()) {
                return;
            }

            //读取回填 默认值
            DefaultValueUtil.getAndSetDefaultValue(insertData, tableFieldInfo, defaultValueContext);
            return;
        }

        if (tableFieldInfo.isVersion()) {
            Object value = tableFieldInfo.getValue(insertData);
            if (value != null) {
                return;
            }
            //设置 乐观锁设置 默认值
            value = VersionUtil.getInitValue(tableFieldInfo.getFieldInfo().getTypeClass());
            //乐观锁回写
            TableInfoUtil.setValue(tableFieldInfo, insertData, value);
            return;
        }

        throw new RuntimeException("未处理");
    }

    private static <T> void doBefore(TableInfo tableInfo, T insertData, SaveStrategy<T> saveStrategy, DbType dbType, Map<String, Object> defaultValueContext) {
        //设置租户ID
        TenantUtil.setTenantId(tableInfo, insertData);

        for (TableFieldInfo tableFieldInfo : tableInfo.getInsertDoBeforeTableFieldInfos()) {
            initInsertValue(tableInfo, tableFieldInfo, insertData, dbType, defaultValueContext);
        }

        //插入动作通知
        OnListenerUtil.notifyInsert(insertData);
    }

    public static <T> BaseInsert<?> create(BaseInsert<?> insert, TableInfo tableInfo, T insertData, SaveStrategy<T> saveStrategy, DbType dbType, Map<String, Object> defaultValueContext) {
        doBefore(tableInfo, insertData, saveStrategy, dbType, defaultValueContext);
        insert = insert == null ? new Insert() : insert;

        insert.$().cacheTableInfo(tableInfo);
        MpTable table = (MpTable) insert.$().table(tableInfo.getType());
        insert.insert(table);

        if (tableInfo.isSplitTable()) {
            TableSplitUtil.splitHandle(table, tableInfo.getSplitFieldInfo().getValue(insertData));
        }

        List<Object> values = new ArrayList<>();
        for (int i = 0; i < tableInfo.getFieldSize(); i++) {
            TableFieldInfo tableFieldInfo = tableInfo.getTableFieldInfos().get(i);
            if (!tableFieldInfo.getTableFieldAnnotation().exists()) {
                continue;
            }
            if (!tableFieldInfo.getTableFieldAnnotation().insert()) {
                continue;
            }

            boolean isNeedInsert = false;
            Object value = tableFieldInfo.getValue(insertData);
            if (Objects.nonNull(value)) {
                isNeedInsert = true;
            }

            // 看是否是强制字段
            if (!isNeedInsert && (saveStrategy.isAllFieldSave() || (Objects.nonNull(saveStrategy.getForceFields()) && saveStrategy.getForceFields().contains(tableFieldInfo.getField().getName())))) {
                isNeedInsert = true;
                if (tableFieldInfo.isTableId() && value == null) {
                    isNeedInsert = false;
                }
            }

            if (isNeedInsert) {
                insert.fields(new MpTableField(table, tableFieldInfo));
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    values.add(CmdParamUtil.build(tableFieldInfo, value));
                }
            }
        }

        insert.values(values);

        if (saveStrategy.getConflictAction() != null) {
            insert.conflictKeys(saveStrategy.getConflictKeys());
            insert.conflictKeys(saveStrategy.getConflictColumns());
            insert.onConflict(saveStrategy.getConflictAction());
        }
        return insert;
    }
}
