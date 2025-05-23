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

import cn.xbatis.core.db.reflect.ModelInfo;
import cn.xbatis.core.db.reflect.Models;
import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveOrUpdateStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.util.WhereUtil;
import cn.xbatis.core.util.ModelInfoUtil;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.Model;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.struct.Where;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SaveOrUpdateModelMethodUtil {

    public static <M extends Model<T>, T> int saveOrUpdate(BasicMapper basicMapper, M model, SaveOrUpdateStrategy saveOrUpdateStrategy) {
        return saveOrUpdate(basicMapper, model, saveOrUpdateStrategy, new HashMap<>());
    }

    public static <M extends Model<T>, T> int saveOrUpdate(BasicMapper basicMapper, M model, SaveOrUpdateStrategy saveOrUpdateStrategy, Map<String, Object> defaultValueContext) {
        return saveOrUpdate(basicMapper, Models.get(model.getClass()), model, saveOrUpdateStrategy, defaultValueContext);
    }

    public static <M extends Model<T>, T> int saveOrUpdate(BasicMapper basicMapper, ModelInfo modelInfo, M model, SaveOrUpdateStrategy saveOrUpdateStrategy, Map<String, Object> defaultValueContext) {
        boolean checkById = true;
        if (saveOrUpdateStrategy.getOn() != null) {
            checkById = false;
        }

        Where checkWhere = WhereUtil.create(modelInfo.getTableInfo());
        if (checkById) {
            if (modelInfo.getIdFieldInfos().isEmpty()) {
                throw new RuntimeException(modelInfo.getType().getName() + " has no id");
            }

            Object id;
            try {
                id = modelInfo.getIdFieldInfos().get(0).getReadFieldInvoker().invoke(model, null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (Objects.isNull(id)) {
                SaveStrategy<M> saveStrategy = new SaveStrategy<>()
                        .allFieldSave(saveOrUpdateStrategy.isAllField())
                        .forceFields(saveOrUpdateStrategy.getForceFields());
                return SaveModelMethodUtil.save(basicMapper, model, saveStrategy, defaultValueContext);
            }
            //使用主键查询
            WhereUtil.appendIdWhereWithModel(checkWhere, modelInfo, model);
        } else {
            saveOrUpdateStrategy.getOn().accept(checkWhere);
        }

        Query<T> query = new Query<>(checkWhere);
        query.$().cacheTableInfo(modelInfo.getTableInfo());
        Table table = query.$(modelInfo.getEntityType());

        if (saveOrUpdateStrategy.isIgnoreLogicDeleteWhenCheck()) {
            LogicDeleteUtil.execute(false, () -> {
                query.from(table).returnType(modelInfo.getEntityType());
            });
        } else {
            query.from(table).returnType(modelInfo.getEntityType());
        }

        for (String c : modelInfo.getTableInfo().getIdColumnNames()) {
            query.select(table.$(c));
        }

        T obj = basicMapper.get(query);
        if (obj == null) {
            SaveStrategy<M> saveStrategy = new SaveStrategy<>()
                    .allFieldSave(saveOrUpdateStrategy.isAllField())
                    .forceFields(saveOrUpdateStrategy.getForceFields());
            return SaveModelMethodUtil.save(basicMapper, model, saveStrategy, defaultValueContext);
        } else {
            UpdateStrategy<M> updateStrategy = new UpdateStrategy<>();
            if (modelInfo.getIdFieldInfos().isEmpty()) {
                updateStrategy.on(query.$where());
            } else {
                modelInfo.getIdFieldInfos().stream().forEach(item -> {
                    ModelInfoUtil.setValue(item, model, TableInfoUtil.getEntityFieldValue(item.getTableFieldInfo(), obj));
                });
            }
            updateStrategy.allFieldUpdate(saveOrUpdateStrategy.isAllField());
            updateStrategy.forceFields(saveOrUpdateStrategy.getForceFields());
            return UpdateModelMethodUtil.update(basicMapper, model, updateStrategy, defaultValueContext);
        }
    }

    public static <M extends Model> int saveOrUpdate(BasicMapper basicMapper, Collection<M> list, SaveOrUpdateStrategy saveOrUpdateStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        M first = list.stream().findFirst().get();
        ModelInfo modelInfo = Models.get(first.getClass());
        int cnt = 0;
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (M model : list) {
            cnt += saveOrUpdate(basicMapper, modelInfo, model, saveOrUpdateStrategy, defaultValueContext);
            DefaultValueContextUtil.removeNonSameLevelData(defaultValueContext);
        }
        return cnt;
    }
}
