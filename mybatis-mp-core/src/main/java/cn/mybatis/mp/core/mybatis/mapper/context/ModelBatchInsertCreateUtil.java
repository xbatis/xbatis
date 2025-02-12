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

package cn.mybatis.mp.core.mybatis.mapper.context;

import cn.mybatis.mp.core.db.reflect.ModelFieldInfo;
import cn.mybatis.mp.core.db.reflect.ModelInfo;
import cn.mybatis.mp.core.db.reflect.TableIds;
import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.incrementer.IdentifierGenerator;
import cn.mybatis.mp.core.incrementer.IdentifierGeneratorFactory;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.Insert;
import cn.mybatis.mp.core.tenant.TenantUtil;
import cn.mybatis.mp.core.util.*;
import cn.mybatis.mp.db.IdAutoType;
import cn.mybatis.mp.db.Model;
import cn.mybatis.mp.db.annotations.TableField;
import cn.mybatis.mp.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.cmd.basic.Table;

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


    public static <T extends Model> BaseInsert<?> create(BaseInsert<?> insert, ModelInfo modelInfo, T[] insertData, SaveBatchStrategy<T> saveBatchStrategy, DbType dbType, boolean useBatchExecutor) {

        insert = insert == null ? new Insert() : insert;

        TableInfo tableInfo = modelInfo.getTableInfo();

        insert.$().cacheTableInfo(tableInfo);
        Table table = insert.$().table(tableInfo.getType());
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
        for (Model t : insertData) {
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < fieldSize; i++) {
                ModelFieldInfo modelFieldInfo = saveFieldInfoSet.get(i);
                Object value = modelFieldInfo.getValue(t);
                boolean hasValue = (!modelFieldInfo.getTableFieldInfo().isTableId() && Objects.nonNull(value))
                        || (modelFieldInfo.getTableFieldInfo().isTableId() && IdUtil.isIdValueExists(value));
                if (!hasValue) {
                    if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                        for (ModelFieldInfo idFieldInfo : modelInfo.getIdFieldInfos()) {
                            TableId tableId = TableInfoUtil.getTableIdAnnotation(idFieldInfo.getTableFieldInfo().getField(), dbType);
                            if (tableId.value() == IdAutoType.GENERATOR) {
                                IdentifierGenerator identifierGenerator = IdentifierGeneratorFactory.getIdentifierGenerator(tableId.generatorName());
                                Object id = identifierGenerator.nextId(modelInfo.getTableInfo().getType());
                                if (IdUtil.setId(t, modelFieldInfo, id)) {
                                    value = id;
                                }
                            } else {
                                throw new RuntimeException(modelFieldInfo.getField().getName() + " has no value");
                            }
                        }
                    } else if (modelFieldInfo.getTableFieldInfo().isTenantId()) {
                        value = TenantUtil.setTenantId(t);
                    } else if (modelFieldInfo.getTableFieldInfo().isLogicDelete()) {
                        //逻辑删除字段
                        //设置删除初始值
                        value = modelFieldInfo.getTableFieldInfo().getLogicDeleteInitValue();
                        if (value != null) {
                            //逻辑删除初始值回写
                            ModelInfoUtil.setValue(modelFieldInfo, t, value);
                        } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                            //读取回填 @TableField里的默认值
                            value = DefaultValueUtil.getAndSetDefaultValue(t, modelFieldInfo);
                        }
                    } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                        //读取回填 默认值
                        value = DefaultValueUtil.getAndSetDefaultValue(t, modelFieldInfo);
                    } else if (modelFieldInfo.getTableFieldInfo().isVersion()) {
                        //乐观锁设置 默认值1
                        value = TypeConvertUtil.convert(Integer.valueOf(1), modelFieldInfo.getField().getType());
                        //乐观锁回写
                        ModelInfoUtil.setValue(modelFieldInfo, t, value);
                    }
                }

                if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                    containId = true;
                }

                TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    MybatisParameter mybatisParameter = new MybatisParameter(value, tableField.typeHandler(), tableField.jdbcType());
                    values.add(Methods.cmd(mybatisParameter));
                }
            }
            insert.values(values);
        }

        if (dbType == DbType.SQL_SERVER && insert.getInsertValues().getValues().size() > 0) {
            TableId tableId = TableIds.get(modelInfo.getEntityType(), dbType);
            if (!useBatchExecutor && !containId && Objects.nonNull(tableId) && tableId.value() == IdAutoType.AUTO) {
                insert.getInsertFields().setOutput("OUTPUT INSERTED." + modelInfo.getTableInfo().getIdFieldInfo().getColumnName());
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
