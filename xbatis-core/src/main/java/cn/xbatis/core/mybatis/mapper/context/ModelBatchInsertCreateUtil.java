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

import cn.xbatis.core.db.reflect.*;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Insert;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.tenant.TenantUtil;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.Model;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.NULL;

import java.util.*;
import java.util.stream.Collectors;

public class ModelBatchInsertCreateUtil {

    private static Set<String> getAllSaveField(ModelInfo modelInfo, DbType dbType, Model model) {
        Set<String> saveFieldSet = new HashSet<>();
        for (ModelFieldInfo modelFieldInfo : modelInfo.getModelFieldInfos()) {
            if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                TableId tableId = TableInfoUtil.getTableIdAnnotation(modelFieldInfo.getTableFieldInfo().getField(), dbType);
                Objects.requireNonNull(tableId.value());
                if (tableId.value() == IdAutoType.AUTO) {
                    Object id;
                    try {
                        id = modelFieldInfo.getReadFieldInvoker().invoke(model, null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (Objects.isNull(id)) {
                        continue;
                    }
                }
            }
            saveFieldSet.add(modelFieldInfo.getField().getName());
        }
        return saveFieldSet;
    }


    private static <M extends Model<T>, T> void doBefore(ModelInfo modelInfo, List<ModelFieldInfo> saveFieldInfoSet, M insertData, SaveBatchStrategy<T> saveBatchStrategy, DbType dbType, Map<String, Object> defaultValueContext) {
        //设置租户ID
        TenantUtil.setTenantId(insertData);

        for (ModelFieldInfo modelFieldInfo : modelInfo.getInsertDoBeforeModelFieldInfos()) {
            if (!saveFieldInfoSet.contains(modelFieldInfo)) {
                continue;
            }
            ModelInsertCreateUtil.initInsertValue(modelInfo, modelFieldInfo, insertData, dbType, defaultValueContext);
        }

        //插入动作通知
        OnListenerUtil.notifyInsert(insertData);
    }


    public static <T extends Model> BaseInsert<?> create(BaseInsert<?> insert, ModelInfo modelInfo, T[] insertData, SaveBatchStrategy<T> saveBatchStrategy, DbType dbType, boolean useBatchExecutor, Map<String, Object> defaultValueContext) {

        insert = insert == null ? new Insert() : insert;

        TableInfo tableInfo = modelInfo.getTableInfo();

        insert.$().cacheTableInfo(tableInfo);
        MpTable table = (MpTable) insert.$().table(tableInfo.getType());
        insert.insert(table);

        Set<String> saveFieldSet;
        if (saveBatchStrategy.getForceFields() == null || saveBatchStrategy.getForceFields().isEmpty()) {
            saveFieldSet = getAllSaveField(modelInfo, dbType, insertData[0]);
        } else {
            saveFieldSet = saveBatchStrategy.getForceFields();
        }

        List<ModelFieldInfo> saveFieldInfoSet = saveFieldSet.stream().map(modelInfo::getFieldInfo).collect(Collectors.toList());

        //拼上主键
        if (!modelInfo.getIdFieldInfos().isEmpty()) {
            modelInfo.getIdFieldInfos().forEach(idFieldInfo -> {
                TableId tableId = TableInfoUtil.getTableIdAnnotation(idFieldInfo.getTableFieldInfo().getField(), dbType);
                if (tableId.value() == IdAutoType.GENERATOR) {
                    if (!saveFieldInfoSet.contains(idFieldInfo)) {
                        saveFieldInfoSet.add(idFieldInfo);
                    }
                }
            });

        }

        //拼上租户ID
        if (Objects.nonNull(modelInfo.getTenantIdFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getTenantIdFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getTenantIdFieldInfo());
            }
        }

        //拼上乐观锁
        if (Objects.nonNull(modelInfo.getVersionFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getVersionFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getVersionFieldInfo());
            }
        }

        //拼上逻辑删除
        if (Objects.nonNull(modelInfo.getLogicDeleteFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getLogicDeleteFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getLogicDeleteFieldInfo());
            }
        }

        //设置insert 列
        for (ModelFieldInfo modelFieldInfo : saveFieldInfoSet) {
            insert.fields(insert.$().field(table, modelFieldInfo.getTableFieldInfo().getColumnName(), modelFieldInfo.getTableFieldInfo().isTableId()));
        }

        int fieldSize = saveFieldInfoSet.size();
        boolean containId = false;

        if (TableSplitUtil.isNeedSplitHandle(table)) {
            TableSplitUtil.splitHandle(table, modelInfo.getSplitFieldInfo().getValue(Arrays.stream(insertData).findFirst().get()));
        }

        for (Model t : insertData) {
            List<Object> values = new ArrayList<>();
            doBefore(modelInfo, saveFieldInfoSet, t, saveBatchStrategy, dbType, defaultValueContext);
            for (int i = 0; i < fieldSize; i++) {
                ModelFieldInfo modelFieldInfo = saveFieldInfoSet.get(i);
                Object value = modelFieldInfo.getValue(t);

                if (modelFieldInfo.getTableFieldInfo().isTableId() && Objects.nonNull(value)) {
                    containId = true;
                }

                TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    values.add(CmdParamUtil.build(tableField, value));
                }
            }
            insert.values(values);
        }

        if (dbType == DbType.SQL_SERVER && insert.getInsertValues().getValues().size() > 0) {
            TableId tableId = TableIds.get(modelInfo.getEntityType(), dbType);
            if (!useBatchExecutor && !containId && Objects.nonNull(tableId) && tableId.value() == IdAutoType.AUTO) {
                TableFieldInfo idFieldInfo = modelInfo.getTableInfo().getSingleIdFieldInfo(false);
                if (idFieldInfo != null) {
                    insert.getInsertFields().setOutput("OUTPUT INSERTED." + idFieldInfo.getColumnName());
                }
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
