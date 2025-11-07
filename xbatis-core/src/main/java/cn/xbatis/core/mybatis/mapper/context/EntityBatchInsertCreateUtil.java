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
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Insert;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.tenant.TenantUtil;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.cmd.basic.Table;

import java.util.*;
import java.util.stream.Collectors;

public class EntityBatchInsertCreateUtil {

    private static Set<String> getAllSaveField(TableInfo tableInfo, DbType dbType, Object entity) {
        Set<String> saveFieldSet = new HashSet<>();
        for (TableFieldInfo tableFieldInfo : tableInfo.getTableFieldInfos()) {
            if (!tableFieldInfo.getTableFieldAnnotation().exists()) {
                continue;
            }
            if (!tableFieldInfo.getTableFieldAnnotation().insert()) {
                continue;
            }
            if (tableFieldInfo.isTableId()) {
                TableId tableId = TableInfoUtil.getTableIdAnnotation(tableFieldInfo.getField(), dbType);
                Objects.requireNonNull(tableId.value());
                if (tableId.value() == IdAutoType.AUTO) {
                    Object id;
                    try {
                        id = tableFieldInfo.getReadFieldInvoker().invoke(entity, null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (Objects.isNull(id)) {
                        continue;
                    }
                }
            }
            saveFieldSet.add(tableFieldInfo.getField().getName());
        }
        return saveFieldSet;
    }

    private static <T> void doBefore(TableInfo tableInfo, List<TableFieldInfo> saveFieldInfoSet, T insertData, DbType dbType, Map<String, Object> defaultValueContext) {
        //设置租户ID
        TenantUtil.setTenantId(tableInfo, insertData);
        for (TableFieldInfo tableFieldInfo : tableInfo.getInsertDoBeforeTableFieldInfos()) {
            if (!saveFieldInfoSet.contains(tableFieldInfo)) {
                continue;
            }
            EntityInsertCreateUtil.initInsertValue(tableInfo, tableFieldInfo, insertData, dbType, defaultValueContext);
        }
        //插入动作通知
        OnListenerUtil.notifyInsert(insertData);
    }


    public static <T> BaseInsert<?> create(BaseInsert<?> insert, TableInfo tableInfo, T[] insertData, SaveBatchStrategy<T> saveBatchStrategy, DbType dbType, boolean useBatchExecutor, Map<String, Object> defaultValueContext) {

        insert = insert == null ? new Insert() : insert;

        insert.$().cacheTableInfo(tableInfo);
        Table table = insert.$().table(tableInfo.getType());
        insert.insert(table);

        Set<String> saveFieldSet;
        if (saveBatchStrategy.getForceFields() == null || saveBatchStrategy.getForceFields().isEmpty()) {
            saveFieldSet = getAllSaveField(tableInfo, dbType, insertData[0]);
        } else {
            saveFieldSet = saveBatchStrategy.getForceFields();
        }

        List<TableFieldInfo> saveFieldInfoSet = saveFieldSet.stream().map(tableInfo::getFieldInfo).collect(Collectors.toList());

        //拼上主键
        if (!tableInfo.getIdFieldInfos().isEmpty()) {
            tableInfo.getIdFieldInfos().forEach(idFieldInfo -> {
                TableId tableId = TableInfoUtil.getTableIdAnnotation(idFieldInfo.getField(), dbType);
                if (tableId.value() == IdAutoType.GENERATOR || tableId.value() == IdAutoType.SQL) {
                    if (!saveFieldInfoSet.contains(idFieldInfo)) {
                        saveFieldInfoSet.add(idFieldInfo);
                    }
                }
            });
        }

        //拼上租户ID
        if (Objects.nonNull(tableInfo.getTenantIdFieldInfo())) {
            if (!saveFieldInfoSet.contains(tableInfo.getTenantIdFieldInfo())) {
                saveFieldInfoSet.add(tableInfo.getTenantIdFieldInfo());
            }
        }

        //拼上乐观锁
        if (Objects.nonNull(tableInfo.getVersionFieldInfo())) {
            if (!saveFieldInfoSet.contains(tableInfo.getVersionFieldInfo())) {
                saveFieldInfoSet.add(tableInfo.getVersionFieldInfo());
            }
        }

        //拼上逻辑删除
        if (Objects.nonNull(tableInfo.getLogicDeleteFieldInfo())) {
            if (!saveFieldInfoSet.contains(tableInfo.getLogicDeleteFieldInfo())) {
                saveFieldInfoSet.add(tableInfo.getLogicDeleteFieldInfo());
            }
        }

        //设置insert 列
        for (TableFieldInfo tableFieldInfo : saveFieldInfoSet) {
            insert.fields(insert.$().field(table, tableFieldInfo.getColumnName(), tableFieldInfo.isTableId()));
        }

        int fieldSize = saveFieldInfoSet.size();

        boolean containId = false;

        if (tableInfo.isSplitTable()) {
            TableSplitUtil.splitHandle((MpTable) table, tableInfo.getSplitFieldInfo().getValue(Arrays.stream(insertData).findFirst().get()));
        }

        for (T t : insertData) {
            doBefore(tableInfo, saveFieldInfoSet, t, dbType, defaultValueContext);
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < fieldSize; i++) {
                TableFieldInfo tableFieldInfo = saveFieldInfoSet.get(i);
                Object value = tableFieldInfo.getValue(t);

                if (tableFieldInfo.isTableId()) {
                    containId = true;
                }

                TableField tableField = tableFieldInfo.getTableFieldAnnotation();
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    values.add(CmdParamUtil.build(tableField, value));
                }
            }
            insert.values(values);
        }

        if (dbType == DbType.SQL_SERVER && insert.getInsertValues().getValues().size() > 0) {
            TableId tableId = TableIds.get(tableInfo.getType(), dbType);
            if (!useBatchExecutor && !containId && Objects.nonNull(tableId) && tableId.value() == IdAutoType.AUTO) {
                insert.getInsertFields().setOutput("OUTPUT INSERTED." + tableInfo.getIdFieldInfo().getColumnName());
            }
        }
        if (saveBatchStrategy.getConflictAction() != null) {
            insert.conflictKeys(saveBatchStrategy.getConflictKeys());
            insert.conflictKeys(saveBatchStrategy.getConflictColumns());
            insert.onConflict(saveBatchStrategy.getConflictAction());
        }
        return insert;
    }
}
