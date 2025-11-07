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
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.ModelBatchInsertContext;
import cn.xbatis.core.mybatis.mapper.context.ModelInsertContext;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Insert;
import cn.xbatis.db.Model;

import java.util.*;
import java.util.stream.Collectors;

public final class SaveModelMethodUtil {

    public static <M extends Model> int save(BasicMapper basicMapper, M model, SaveStrategy<M> saveStrategy) {
        return save(basicMapper, model, saveStrategy, new HashMap<>());
    }

    public static <M extends Model> int save(BasicMapper basicMapper, M model, SaveStrategy<M> saveStrategy, Map<String, Object> defaultValueContext) {
        return basicMapper.$saveModel(new ModelInsertContext(new Insert(), model, saveStrategy, defaultValueContext));
    }

    public static <M extends Model> int saveList(BasicMapper basicMapper, Collection<M> list, SaveStrategy<M> saveStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        int cnt = 0;
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (M model : list) {
            cnt += save(basicMapper, model, saveStrategy, defaultValueContext);
            DefaultValueContextUtil.removeNonSameLevelData(defaultValueContext);
        }
        return cnt;
    }

    public static <M extends Model> int saveBatch(BasicMapper basicMapper, Collection<M> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        SaveBatchStrategy saveBatchStrategy = new SaveBatchStrategy();
        return saveBatch(basicMapper, new Insert(), list, saveBatchStrategy);
    }

    public static <M extends Model> int saveBatch(BasicMapper basicMapper, Collection<M> list, SaveBatchStrategy saveBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return saveBatch(basicMapper, new Insert(), list, saveBatchStrategy);
    }

    public static <M extends Model> int saveBatch(BasicMapper basicMapper, BaseInsert<?> insert, Collection<M> list, SaveBatchStrategy<M> saveBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return saveBatch(basicMapper, insert, list, saveBatchStrategy, new HashMap<>());
    }

    public static <M extends Model> int saveBatch(BasicMapper basicMapper, BaseInsert<?> insert, Collection<M> list, SaveBatchStrategy<M> saveBatchStrategy, Map<String, Object> defaultValueContext) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        M first = list.stream().findFirst().get();
        ModelInfo modelInfo = Models.get(first.getClass());

        if (modelInfo.getTableInfo().isSplitTable()) {
            final List<String> groups = new ArrayList<>();
            Map<String, List<M>> groupedMap = list.stream()
                    .collect(Collectors.groupingBy(m -> {
                        String splitTableName = TableSplitUtil.getSplitTableName(modelInfo.getTableInfo(), modelInfo.getSplitFieldInfo().getValue(m));
                        if (!groups.contains(splitTableName)) {
                            groups.add(splitTableName);
                        }
                        return splitTableName;
                    }));

            int count = 0;
            for (String key : groups) {
                if (insert != null && insert.getInsertFields() != null && insert.getInsertFields().getFields() != null) {
                    insert.getInsertFields().getFields().clear();
                    insert.getInsertValues().getValues().clear();
                    insert.getInsertTable().getTable().setName(key);
                }
                count += basicMapper.$save(new ModelBatchInsertContext<>(insert, modelInfo, groupedMap.get(key), saveBatchStrategy, defaultValueContext));
            }
            return count;
        }

        return basicMapper.$save(new ModelBatchInsertContext<>(insert, modelInfo, list, saveBatchStrategy, defaultValueContext));
    }
}
