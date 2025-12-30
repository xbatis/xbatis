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
import cn.xbatis.core.util.ModelInfoUtil;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.Model;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.basic.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelInsertCreateUtil {

    public static <M extends Model<T>, T> void initInsertValue(ModelInfo modelInfo, ModelFieldInfo modelFieldInfo, M insertData, DbType dbType, Map<String, Object> defaultValueContext) {
        if (modelFieldInfo.getTableFieldInfo().isTableId()) {
            Object value = modelFieldInfo.getValue(insertData);
            if (value != null && IdUtil.isIdExists(insertData, modelFieldInfo)) {
                return;
            }
            if (!IdUtil.isIdValueExists(value)) {
                TableId tableId = TableIds.get(modelInfo.getEntityType(), dbType);
                if (tableId.value() == IdAutoType.GENERATOR) {
                    Generator generator = GeneratorFactory.getIdentifierGenerator(tableId.generatorName());
                    Object id = generator.nextId(modelInfo.getTableInfo().getType());
                    IdUtil.setId(insertData, modelFieldInfo, id);
                }
            }
            return;
        }

        if (modelFieldInfo.getTableFieldInfo().isLogicDelete()) {
            Object value = modelFieldInfo.getValue(insertData);
            if (value != null) {
                return;
            }
            //逻辑删除字段
            //设置删除初始值
            value = modelFieldInfo.getTableFieldInfo().getLogicDeleteInitValue();
            if (value != null) {
                //逻辑删除初始值回写
                ModelInfoUtil.setValue(modelFieldInfo, insertData, value);
            } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                //读取回填 @TableField里的默认值
                DefaultValueUtil.getAndSetDefaultValue(insertData, modelFieldInfo, defaultValueContext);
            }
            return;
        }

        if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
            Object value = modelFieldInfo.getValue(insertData);
            if (value != null && !modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValueFillAlways()) {
                return;
            }

            //读取回填 默认值
            DefaultValueUtil.getAndSetDefaultValue(insertData, modelFieldInfo, defaultValueContext);
            return;
        }
        if (modelFieldInfo.getTableFieldInfo().isVersion()) {
            Object value = modelFieldInfo.getValue(insertData);
            if (value != null) {
                return;
            }
            //设置 乐观锁设置 默认值
            value = VersionUtil.getInitValue(modelFieldInfo.getFieldInfo().getTypeClass());
            //乐观锁回写
            ModelInfoUtil.setValue(modelFieldInfo, insertData, value);
            return;
        }

        throw new RuntimeException("未处理");
    }


    private static <M extends Model<T>, T> void doBefore(ModelInfo modelInfo, M insertData, SaveStrategy<T> saveStrategy, DbType dbType, Map<String, Object> defaultValueContext) {
        //设置租户ID
        TenantUtil.setTenantId(insertData);

        for (ModelFieldInfo modelFieldInfo : modelInfo.getInsertDoBeforeModelFieldInfos()) {
            initInsertValue(modelInfo, modelFieldInfo, insertData, dbType, defaultValueContext);
        }

        //插入动作通知
        OnListenerUtil.notifyInsert(insertData);
    }

    public static <M extends Model<T>, T> BaseInsert<?> create(BaseInsert<?> insert, ModelInfo modelInfo, M insertData, SaveStrategy<T> saveStrategy, DbType dbType, Map<String, Object> defaultValueContext) {
        doBefore(modelInfo, insertData, saveStrategy, dbType, defaultValueContext);

        insert = insert == null ? new Insert() : insert;
        TableInfo tableInfo = modelInfo.getTableInfo();

        insert.$().cacheTableInfo(tableInfo);
        MpTable table = (MpTable) insert.$().table(tableInfo.getType());
        insert.insert(table);

        if (tableInfo.isSplitTable()) {
            TableSplitUtil.splitHandle(table, modelInfo.getSplitFieldInfo().getValue(insertData));
        }

        List<Object> values = new ArrayList<>();

        for (int i = 0; i < modelInfo.getFieldSize(); i++) {
            ModelFieldInfo modelFieldInfo = modelInfo.getModelFieldInfos().get(i);
            boolean isNeedInsert = false;
            Object value = modelFieldInfo.getValue(insertData);

            if (Objects.nonNull(value)) {
                isNeedInsert = true;
            }

            // 看是否是强制字段
            if (!isNeedInsert && (saveStrategy.isAllFieldSave() || (Objects.nonNull(saveStrategy.getForceFields()) && saveStrategy.getForceFields().contains(modelFieldInfo.getField().getName())))) {
                isNeedInsert = true;
                if (modelFieldInfo.getTableFieldInfo().isTableId() && value == null) {
                    isNeedInsert = false;
                }
            }

            if (isNeedInsert) {
                insert.fields(new MpTableField(table, modelFieldInfo.getTableFieldInfo()));
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    values.add(CmdParamUtil.build(modelFieldInfo, value));
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
