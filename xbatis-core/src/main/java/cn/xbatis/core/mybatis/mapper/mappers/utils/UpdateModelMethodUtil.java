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
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.ModelUpdateContext;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import cn.xbatis.db.Model;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.Condition;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.dbFun.Case;
import db.sql.api.tookit.LambdaUtil;

import java.io.Serializable;
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
        return basicMapper.$update(new ModelUpdateContext<>(modelInfo, model, updateStrategy, defaultValueContext));
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

    public static <M extends Model> int updateBatchModel(BasicMapper basicMapper, Collection<M> list, Getter<M>[] batchFields) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }

        ModelInfo modelInfo = Models.get(list.stream().findFirst().get().getClass());

        Collection<ModelFieldInfo> modelFieldInfos;
        if (batchFields == null || batchFields.length == 0) {
            modelFieldInfos = modelInfo.getModelFieldInfos();
        } else {
            modelFieldInfos = Arrays.stream(batchFields)
                    .map(i -> LambdaUtil.getName(i))
                    .map(i -> modelInfo.getFieldInfo(i))
                    .filter(i -> !i.getTableFieldInfo().isTableId())
                    .collect(Collectors.toSet());
            modelFieldInfos.addAll(modelInfo.getIdFieldInfos());
        }

        UpdateChain updateChain = UpdateChain.of(basicMapper, modelInfo.getEntityType());
        Map<String, List<Serializable>> columnUpdateValues = new HashMap<>();
        for (M model : list) {
            for (ModelFieldInfo modelFieldInfo : modelFieldInfos) {
                List<Serializable> values = columnUpdateValues.get(modelFieldInfo.getTableFieldInfo().getColumnName());
                if (values == null) {
                    values = new ArrayList<>();
                    columnUpdateValues.put(modelFieldInfo.getTableFieldInfo().getColumnName(), values);
                }
                values.add((Serializable) modelFieldInfo.getValue(model));
            }
        }

        List<ModelFieldInfo> idFieldInfos = modelInfo.getIdFieldInfos();
        if (idFieldInfos == null || idFieldInfos.isEmpty()) {
            throw new IllegalArgumentException(modelInfo.getType() + " has no id field");
        }

        for (ModelFieldInfo modelFieldInfo : modelFieldInfos) {
            TableField tableField = updateChain.$().field(modelInfo.getEntityType(), modelFieldInfo.getTableFieldInfo().getField().getName());
            if (tableField.isId()) {
                continue;
            }
            Case sqlCase = Methods.case_();
            for (int i = 0; i < list.size(); i++) {
                Object value = columnUpdateValues.get(tableField.getName()).get(i);
                if (value == null) {
                    value = Methods.NULL();
                }
                sqlCase.when(buildIdCaseWhen(updateChain, modelInfo.getTableInfo(), idFieldInfos, columnUpdateValues, i), Methods.cmd(value));
            }
            updateChain.set(tableField, sqlCase);
        }

        idFieldInfos.stream().forEach(tableFieldInfo -> {
            TableField tableField = updateChain.$().field(modelInfo.getEntityType(), tableFieldInfo.getField().getName());
            updateChain.in(tableField, columnUpdateValues.get(tableField.getName()));
        });

        return updateChain
                .execute();
    }

    private static Condition buildIdCaseWhen(UpdateChain updateChain, TableInfo tableInfo, List<ModelFieldInfo> idFieldInfos, Map<String, List<Serializable>> columnUpdateValues, int index) {
        Condition condition = null;
        for (ModelFieldInfo modelFieldInfo : idFieldInfos) {
            List<Serializable> values = columnUpdateValues.get(modelFieldInfo.getTableFieldInfo().getColumnName());
            TableField tableField = updateChain.$().field(tableInfo.getType(), modelFieldInfo.getTableFieldInfo().getField().getName());
            if (condition == null) {
                condition = tableField.eq(values.get(index));
            }
        }
        return condition;
    }
}
