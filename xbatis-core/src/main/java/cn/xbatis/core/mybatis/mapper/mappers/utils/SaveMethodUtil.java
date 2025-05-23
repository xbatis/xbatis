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

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.EntityBatchInsertContext;
import cn.xbatis.core.mybatis.mapper.context.EntityInsertContext;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.core.sql.executor.Insert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SaveMethodUtil {

    public static <T> int save(BasicMapper basicMapper, TableInfo tableInfo, T entity) {
        return save(basicMapper, tableInfo, entity, new SaveStrategy<>());
    }

    public static <T> int save(BasicMapper basicMapper, TableInfo tableInfo, T entity, SaveStrategy<T> strategy) {
        return basicMapper.$saveEntity(new EntityInsertContext(new Insert(), tableInfo, entity, strategy, new HashMap<>()));
    }

    public static <T> int save(BasicMapper basicMapper, TableInfo tableInfo, T entity, SaveStrategy<T> strategy, Map<String, Object> defaultValueContext) {
        return basicMapper.$saveEntity(new EntityInsertContext(new Insert(), tableInfo, entity, strategy, defaultValueContext));
    }

    public static <T> int saveList(BasicMapper basicMapper, TableInfo tableInfo, Collection<T> list, SaveStrategy strategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        int cnt = 0;
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (T entity : list) {
            cnt += save(basicMapper, tableInfo, entity, strategy, defaultValueContext);
            DefaultValueContextUtil.removeNonSameLevelData(defaultValueContext);
        }
        return cnt;
    }

    public static <E> int saveBatch(BasicMapper basicMapper, Collection<E> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        TableInfo tableInfo = Tables.get(list.stream().findFirst().get().getClass());
        SaveBatchStrategy saveBatchStrategy = new SaveBatchStrategy();
        return saveBatch(basicMapper, tableInfo, list, saveBatchStrategy);
    }

    public static <E> int saveBatch(BasicMapper basicMapper, Collection<E> list, SaveBatchStrategy saveBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        TableInfo tableInfo = Tables.get(list.stream().findFirst().get().getClass());
        return saveBatch(basicMapper, tableInfo, list, saveBatchStrategy);
    }

    public static <E> int saveBatch(BasicMapper basicMapper, TableInfo tableInfo, Collection<E> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        SaveBatchStrategy saveBatchStrategy = new SaveBatchStrategy();
        return saveBatch(basicMapper, tableInfo, list, saveBatchStrategy);
    }

    public static <T> int saveBatch(BasicMapper basicMapper, TableInfo tableInfo, Collection<T> list, SaveBatchStrategy<T> saveBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return saveBatch(basicMapper, new Insert(), tableInfo, list, saveBatchStrategy);
    }

    public static <E> int saveBatch(BasicMapper basicMapper, BaseInsert<?> insert, TableInfo tableInfo, Collection<E> list, SaveBatchStrategy<E> saveBatchStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return basicMapper.$save(new EntityBatchInsertContext(insert, tableInfo, list, saveBatchStrategy, new HashMap<>()));
    }

    public static <E> int saveBatch(BasicMapper basicMapper, BaseInsert<?> insert, TableInfo tableInfo, Collection<E> list, SaveBatchStrategy<E> saveBatchStrategy, Map<String, Object> defaultValueContext) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        return basicMapper.$save(new EntityBatchInsertContext(insert, tableInfo, list, saveBatchStrategy, defaultValueContext));
    }
}
