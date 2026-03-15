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

package cn.xbatis.core.mybatis.mapper.mappers.utils;

import cn.xbatis.core.db.reflect.ModelFieldInfo;
import cn.xbatis.core.db.reflect.ModelInfo;
import cn.xbatis.core.db.reflect.Models;
import cn.xbatis.core.exception.NoUpdateRowException;
import cn.xbatis.core.exception.OptimisticLockException;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.ModelUpdateContext;
import cn.xbatis.core.mybatis.mapper.context.ModelUpdateCreateUtil;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateBatchStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.executor.MpTableField;
import cn.xbatis.core.sql.executor.TableSplitUtil;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import cn.xbatis.core.util.ModelInfoUtil;
import cn.xbatis.db.Model;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.dbFun.Case;
import db.sql.api.impl.cmd.struct.ConditionChain;
import db.sql.api.tookit.LambdaUtil;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UpdateModelMethodUtil {

    public static <M extends Model> int update(BasicMapper basicMapper, M model, Consumer<UpdateStrategy<M>> updateStrategy) {
        UpdateStrategy<M> strategy = UpdateMethodUtil.createUpdateStrategy();
        updateStrategy.accept(strategy);
        return update(basicMapper, Models.get(model.getClass()), model, strategy);
    }

    public static <M extends Model> int update(BasicMapper basicMapper, M model, UpdateStrategy<M> updateStrategy) {
        return update(basicMapper, model, updateStrategy, new HashMap<>());
    }

    public static <M extends Model> int update(BasicMapper basicMapper, M model, UpdateStrategy<M> updateStrategy, Map<String, Object> defaultValueContext) {
        return update(basicMapper, Models.get(model.getClass()), model, updateStrategy, defaultValueContext);
    }

    public static <M extends Model> int update(BasicMapper basicMapper, ModelInfo modelInfo, M model, UpdateStrategy<M> updateStrategy) {
        return update(basicMapper, modelInfo, model, updateStrategy, new HashMap<>());
    }

    public static <M extends Model> int update(BasicMapper basicMapper, ModelInfo modelInfo, M model, UpdateStrategy<M> updateStrategy, Map<String, Object> defaultValueContext) {
        if (updateStrategy.getUpdateFields() != null && updateStrategy.getUpdateFields().isEmpty()) {
            if (updateStrategy.isThrowExWhenNoRowUpdate()) {
                throw new NoUpdateRowException(updateStrategy.getNoRowUpdateErrorMessage());
            }
            return 0;
        }

        boolean isUpdateById = true;

        if (modelInfo.getIdFieldInfos().isEmpty()) {
            isUpdateById = false;
        } else {
            for (ModelFieldInfo idFieldInfo : modelInfo.getIdFieldInfos()) {
                if (idFieldInfo.getValue(model) == null) {
                    isUpdateById = false;
                    break;
                }
            }
        }

        Object version = null;
        try {
            if (modelInfo.getTableInfo().getVersionFieldInfo() != null) {
                if (modelInfo.getVersionFieldInfo() == null) {
                    if (isUpdateById) {
                        throw new RuntimeException(MessageFormat.format("model class {0} , need a version field", modelInfo.getType().getName()));
                    }
                } else {
                    version = modelInfo.getVersionFieldInfo().getValue(model);
                    if (version == null && isUpdateById) {
                        throw new RuntimeException("Data has no version value");
                    }
                }
            }

            int cnt = basicMapper.$update(new ModelUpdateContext<>(modelInfo, model, updateStrategy, defaultValueContext));
            if (cnt == 0 && modelInfo.getVersionFieldInfo() != null && version != null) {
                throw new OptimisticLockException(model, "Row was updated or deleted by another transaction");
            }

            if (cnt == 0 && updateStrategy.isThrowExWhenNoRowUpdate()) {
                throw new NoUpdateRowException(updateStrategy.getNoRowUpdateErrorMessage());
            }
            return cnt;
        } catch (Throwable e) {
            if (modelInfo.getVersionFieldInfo() != null) {
                //恢复version初始值
                ModelInfoUtil.setValue(modelInfo.getVersionFieldInfo(), model, version);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <M extends Model> int updateList(BasicMapper basicMapper, Collection<M> list, UpdateStrategy<M> updateStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        ModelInfo modelInfo = Models.get(list.stream().findFirst().get().getClass());
        int cnt = 0;
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (M model : list) {
            cnt += update(basicMapper, modelInfo, model, updateStrategy, defaultValueContext);
            DefaultValueContextUtil.removeNonSameLevelData(defaultValueContext);
        }
        return cnt;
    }

    public static <M extends Model> int updateModelBatch(BasicMapper basicMapper, Collection<M> list, Getter<M>[] batchFields) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        ModelInfo modelInfo = Models.get(list.stream().findFirst().get().getClass());
        return updateModelBatch(basicMapper, modelInfo, list, UpdateBatchStrategy.create(strategy -> strategy.batchFields(batchFields)));
    }

    public static <M extends Model> int updateModelBatch(BasicMapper basicMapper, Collection<M> list, UpdateBatchStrategy<M> updateBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        ModelInfo modelInfo = Models.get(list.stream().findFirst().get().getClass());
        return updateModelBatch(basicMapper, modelInfo, list, updateBatchStrategy);
    }

    public static <M extends Model> int updateModelBatch(BasicMapper basicMapper, ModelInfo modelInfo, Collection<M> list, UpdateBatchStrategy<M> updateBatchStrategy) {
        if (modelInfo.getIdFieldInfos().isEmpty()) {
            throw new RuntimeException("The model " + modelInfo.getType() + " has no id field ,can't do batch update");
        }
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }

        List<ModelFieldInfo> tableIdFieldInfos;
        List<ModelFieldInfo> tableFieldInfos;

        Getter<M>[] batchFields = updateBatchStrategy.getBatchFields();
        if (batchFields == null || batchFields.length == 0) {
            tableIdFieldInfos = modelInfo.getIdFieldInfos();
            tableFieldInfos = modelInfo.getModelFieldInfos()
                    .stream()
                    .filter(i -> !i.getTableFieldInfo().isTableId())
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            tableFieldInfos = Arrays.stream(batchFields)
                    .map(i -> LambdaUtil.getName(i))
                    .map(i -> modelInfo.getFieldInfo(i))
                    .filter(i -> !i.getTableFieldInfo().isTableId())
                    .collect(Collectors.toList());

            tableIdFieldInfos = modelInfo.getIdFieldInfos();
        }

        if (tableIdFieldInfos.isEmpty()) {
            if (modelInfo.getIdFieldInfos() == null || modelInfo.getIdFieldInfos().isEmpty()) {
                throw new IllegalArgumentException(modelInfo.getType() + " has no id field");
            }
        }

        if (modelInfo.getTableInfo().isSplitTable()) {
            final List<String> groups = new ArrayList<>();
            Map<String, List<M>> groupedMap = list.stream()
                    .collect(Collectors.groupingBy(e -> {
                        String splitTableName = TableSplitUtil.getSplitTableName(modelInfo.getTableInfo(), modelInfo.getSplitFieldInfo().getValue(e));
                        if (!groups.contains(splitTableName)) {
                            groups.add(splitTableName);
                        }
                        return splitTableName;
                    }));

            int count = 0;
            for (String key : groups) {
                UpdateChain updateChain = UpdateChain.of(basicMapper, modelInfo.getTableInfo().getType());
                updateChain.update(modelInfo.getTableInfo().getType(), table -> table.setName(key));
                count += _updateBatch(updateChain, modelInfo, groupedMap.get(key), tableFieldInfos, tableIdFieldInfos, updateBatchStrategy);
            }
            return count;
        }
        UpdateChain updateChain = UpdateChain.of(basicMapper, modelInfo.getTableInfo().getType());
        return _updateBatch(updateChain, modelInfo, list, tableFieldInfos, tableIdFieldInfos, updateBatchStrategy);
    }

    private static <M extends Model> int _updateBatch(UpdateChain updateChain, ModelInfo modelInfo, Collection<M> list, Collection<ModelFieldInfo> updateModelFieldInfos, List<ModelFieldInfo> idFieldInfos, UpdateBatchStrategy<M> updateBatchStrategy) {
        MpTableField[] idTableFields = idFieldInfos.stream().map(i -> {
            return updateChain.$().field(modelInfo.getTableInfo().getType(), i.getTableFieldInfo().getField().getName());
        }).collect(Collectors.toList()).toArray(new MpTableField[0]);


        boolean allUpdate = updateBatchStrategy.getBatchFields() == null || updateBatchStrategy.getBatchFields().length == 0;
        Map<String, List<Serializable>> columnUpdateValues = new HashMap<>();
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (M model : list) {
            if (allUpdate && !updateBatchStrategy.isIgnoreDefaultValue()) {
                for (ModelFieldInfo modelFieldInfo : updateModelFieldInfos) {
                    ModelUpdateCreateUtil.initUpdateValue(modelFieldInfo, model, Collections.EMPTY_SET, defaultValueContext);
                }
            }

            List<Serializable> values;
            for (ModelFieldInfo idTableField : idFieldInfos) {
                values = columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName());
                Object idValue = idTableField.getValue(model);
                if (idValue == null) {
                    throw new IllegalArgumentException("the datas of batch update has some id no set");
                }

                if (values == null) {
                    values = new ArrayList<>();
                    columnUpdateValues.put(idTableField.getTableFieldInfo().getColumnName(), values);
                }
                values.add((Serializable) idValue);
            }


            for (ModelFieldInfo modelFieldInfo : updateModelFieldInfos) {
                values = columnUpdateValues.get(modelFieldInfo.getTableFieldInfo().getColumnName());
                if (values == null) {
                    values = new ArrayList<>();
                    columnUpdateValues.put(modelFieldInfo.getTableFieldInfo().getColumnName(), values);
                }

                if (!allUpdate && !updateBatchStrategy.isIgnoreDefaultValue()) {
                    ModelUpdateCreateUtil.initUpdateValue(modelFieldInfo, model, Collections.EMPTY_SET, defaultValueContext);
                }

                values.add((Serializable) modelFieldInfo.getValue(model));
            }
        }

        for (ModelFieldInfo modelFieldInfo : updateModelFieldInfos) {
            TableField tableField = updateChain.$().field(modelInfo.getTableInfo().getType(), modelFieldInfo.getTableFieldInfo().getField().getName());
            if (tableField.isId()) {
                continue;
            }
            Case sqlCase = Methods.case_();
            for (int i = 0; i < list.size(); i++) {
                Object value = columnUpdateValues.get(modelFieldInfo.getTableFieldInfo().getColumnName()).get(i);
                if (value == null) {
                    if (updateBatchStrategy.isIgnoreNull()) {
                        value = tableField;
                    } else {
                        value = Methods.NULL();
                    }
                }
                sqlCase.when(buildIdCaseWhen(updateChain, idTableFields, columnUpdateValues, i), Methods.cmd(value));
            }
            sqlCase.else_(tableField);
            updateChain.set(tableField, sqlCase);
        }


        if (idTableFields.length > 1) {
            updateChain.dbAdapt((update, selector) -> {
                selector.otherwise(dbType -> {
                    if (dbType == DbType.H2 || dbType == DbType.SQLITE || dbType == DbType.KING_BASE || dbType == DbType.GAUSS ||
                            dbType == DbType.PGSQL || dbType.getDbModel() == DbModel.PGSQL) {
                        //支持双列 in
                        StringBuilder inTpl = new StringBuilder("(");

                        for (int i = 0; i < idTableFields.length; i++) {
                            inTpl.append("{").append(i).append("},");
                        }
                        inTpl.deleteCharAt(inTpl.length() - 1).append(")");
                        List<Object> values = new ArrayList<>();
                        for (int j = 0; j < list.size(); j++) {
                            List<Object> multiValues = new ArrayList<>();
                            for (int i = 0; i < idTableFields.length; i++) {
                                MpTableField idTableField = idTableFields[i];
                                multiValues.add(columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName()).get(j));
                            }
                            values.add(Methods.tpl(inTpl.toString(), multiValues.toArray()));
                        }
                        update.and(Methods.in(Methods.tpl(inTpl.toString(), idTableFields), values));

                    } else {
                        for (MpTableField idTableField : idTableFields) {
                            updateChain.in(idTableField, columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName()));
                        }
                        updateChain.andNested(chain -> {
                            for (int i = 0; i < list.size(); i++) {
                                final int index = i;
                                chain.orNested(o -> {
                                    for (MpTableField idTableField : idTableFields) {
                                        Object value = columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName()).get(index);
                                        o.eq(idTableField, value);
                                    }
                                });
                            }
                        });
                    }
                });
            });
        } else {
            for (MpTableField idTableField : idTableFields) {
                updateChain.in(idTableField, columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName()));
            }
        }

        return updateChain
                .execute();
    }


    private static ICondition buildIdCaseWhen(UpdateChain updateChain, MpTableField[] idTableFields, Map<String, List<Serializable>> columnUpdateValues, int index) {
        ConditionChain whenChain = updateChain.conditionChain().getConditionFactory().newConditionChain(null);
        for (MpTableField idTableField : idTableFields) {
            whenChain.and(idTableField.eq(columnUpdateValues.get(idTableField.getTableFieldInfo().getColumnName()).get(index)));
        }
        return whenChain;
    }
}
