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

package cn.mybatis.mp.core.mybatis.mapper.mappers.utils;

import cn.mybatis.mp.core.db.reflect.ModelInfo;
import cn.mybatis.mp.core.db.reflect.Models;
import cn.mybatis.mp.core.mybatis.mapper.BasicMapper;
import cn.mybatis.mp.core.mybatis.mapper.context.ModelBatchInsertContext;
import cn.mybatis.mp.core.mybatis.mapper.context.ModelInsertContext;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.Insert;
import cn.mybatis.mp.db.Model;

import java.util.Collection;
import java.util.Objects;

public final class SaveModelMethodUtil {

    public static <M extends Model> int save(BasicMapper basicMapper, M model, SaveStrategy<M> saveStrategy) {
        return basicMapper.$saveModel(new ModelInsertContext(new Insert(), model, saveStrategy));
    }

    public static <M extends Model> int saveList(BasicMapper basicMapper, Collection<M> list, SaveStrategy<M> saveStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        int cnt = 0;
        for (M model : list) {
            cnt += save(basicMapper, model, saveStrategy);
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
        M first = list.stream().findFirst().get();
        ModelInfo modelInfo = Models.get(first.getClass());
        return basicMapper.$save(new ModelBatchInsertContext<>(insert, modelInfo, list, saveBatchStrategy));
    }
}
