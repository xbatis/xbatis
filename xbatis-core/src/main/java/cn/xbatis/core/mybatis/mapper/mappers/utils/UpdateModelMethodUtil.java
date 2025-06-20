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
import cn.xbatis.core.mybatis.mapper.context.ModelUpdateContext;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.db.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
}
