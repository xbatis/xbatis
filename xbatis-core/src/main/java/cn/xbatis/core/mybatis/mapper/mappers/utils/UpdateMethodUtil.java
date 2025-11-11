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

import cn.xbatis.core.db.reflect.OnListenerUtil;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.EntityUpdateContext;
import cn.xbatis.core.mybatis.mapper.context.EntityUpdateCreateUtil;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import cn.xbatis.core.util.OptimisticLockUtil;
import cn.xbatis.core.util.TableInfoUtil;
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

public final class UpdateMethodUtil {

    public static <T> UpdateStrategy<T> createUpdateStrategy() {
        return new UpdateStrategy<>();
    }

    public static <T> int update(BasicMapper basicMapper, T entity, UpdateStrategy<T> updateStrategy) {
        return update(basicMapper, Tables.get(entity.getClass()), entity, updateStrategy);
    }

    public static <T> int update(BasicMapper basicMapper, TableInfo tableInfo, T entity) {
        return update(basicMapper, tableInfo, entity, createUpdateStrategy());
    }

    public static <T> int update(BasicMapper basicMapper, TableInfo tableInfo, T entity, UpdateStrategy<T> updateStrategy) {
        return update(basicMapper, tableInfo, entity, updateStrategy, new HashMap<>());
    }

    public static <T> int update(BasicMapper basicMapper, TableInfo tableInfo, T entity, UpdateStrategy<T> updateStrategy, Map<String, Object> defaultValueContext) {
        Object version = null;
        try {
            if (tableInfo.getVersionFieldInfo() != null) {
                version = tableInfo.getVersionFieldInfo().getValue(entity);
            }
            return basicMapper.$update(new EntityUpdateContext(tableInfo, entity, updateStrategy, defaultValueContext));
        } catch (Throwable e) {
            if (tableInfo.getVersionFieldInfo() != null) {
                //恢复version初始值
                TableInfoUtil.setValue(tableInfo.getVersionFieldInfo(), entity, version);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> int update(BasicMapper basicMapper, TableInfo tableInfo, T entity, Consumer<UpdateStrategy<T>> updateStrategy) {
        UpdateStrategy strategy = createUpdateStrategy();
        updateStrategy.accept(strategy);
        return update(basicMapper, tableInfo, entity, strategy);
    }

    public static <T> int update(BasicMapper basicMapper, T entity, Consumer<UpdateStrategy<T>> updateStrategy) {
        return update(basicMapper, Tables.get(entity.getClass()), entity, updateStrategy);
    }

    public static <T> int updateList(BasicMapper basicMapper, Collection<T> list, Consumer<UpdateStrategy<T>> updateStrategy) {
        UpdateStrategy strategy = createUpdateStrategy();
        updateStrategy.accept(strategy);
        return updateList(basicMapper, list, strategy);
    }

    public static <T> int updateList(BasicMapper basicMapper, Collection<T> list, UpdateStrategy<T> updateStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        TableInfo tableInfo = Tables.get(list.stream().findFirst().get().getClass());
        return updateList(basicMapper, tableInfo, list, updateStrategy);
    }

    public static <T> int updateList(BasicMapper basicMapper, TableInfo tableInfo, Collection<T> list, Consumer<UpdateStrategy<T>> updateStrategy) {
        UpdateStrategy strategy = createUpdateStrategy();
        updateStrategy.accept(strategy);
        return updateList(basicMapper, tableInfo, list, strategy);
    }

    public static <T> int updateList(BasicMapper basicMapper, TableInfo tableInfo, Collection<T> list, UpdateStrategy<T> updateStrategy) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }

        int cnt = 0;
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (T entity : list) {
            cnt += update(basicMapper, tableInfo, entity, updateStrategy, defaultValueContext);
            DefaultValueContextUtil.removeNonSameLevelData(defaultValueContext);
        }
        return cnt;
    }

    public static <T> int updateBatch(BasicMapper basicMapper, Collection<T> list, Getter<T>[] batchFields) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        if (batchFields == null || batchFields.length == 0) {
            throw new IllegalArgumentException("batchFields must not be empty");
        }
        TableInfo tableInfo = Tables.get(list.stream().findFirst().get().getClass());
        return updateBatch(basicMapper, tableInfo, list, batchFields);
    }

    public static <T> int updateBatch(BasicMapper basicMapper, TableInfo tableInfo, Collection<T> list, Getter<T>[] batchFields) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }

        Collection<TableFieldInfo> tableFieldInfos;
        if (batchFields == null || batchFields.length == 0) {
            tableFieldInfos = tableInfo.getTableFieldInfos()
                    .stream()
                    .filter(i -> i.getTableFieldAnnotation().exists() && i.getTableFieldAnnotation().update() && !i.isLogicDelete() && !i.isVersion())
                    .collect(Collectors.toSet());
            ;
        } else {
            tableFieldInfos = Arrays.stream(batchFields)
                    .map(i -> LambdaUtil.getName(i))
                    .map(i -> tableInfo.getFieldInfo(i))
                    .filter(i -> !i.isTableId() && !i.isLogicDelete() && !i.isVersion())
                    .collect(Collectors.toSet());
            tableFieldInfos.addAll(tableInfo.getIdFieldInfos());
        }


        List<TableFieldInfo> idFieldInfos = tableInfo.getIdFieldInfos();
        if (idFieldInfos == null || idFieldInfos.isEmpty()) {
            throw new IllegalArgumentException(tableInfo.getType() + " has no id field");
        }

        if (tableInfo.isSplitTable()) {
            final List<String> groups = new ArrayList<>();
            Map<String, List<T>> groupedMap = list.stream()
                    .collect(Collectors.groupingBy(e -> {
                        String splitTableName = TableSplitUtil.getSplitTableName(tableInfo, tableInfo.getSplitFieldInfo().getValue(e));
                        if (!groups.contains(splitTableName)) {
                            groups.add(splitTableName);
                        }
                        return splitTableName;
                    }));

            int count = 0;
            for (String key : groups) {
                UpdateChain updateChain = UpdateChain.of(basicMapper, tableInfo.getType());
                updateChain.update(tableInfo.getType(), table -> table.setName(key));
                count += _updateBatch(updateChain, tableInfo, groupedMap.get(key), tableFieldInfos, idFieldInfos, batchFields == null || batchFields.length == 0);
            }
            return count;
        }
        UpdateChain updateChain = UpdateChain.of(basicMapper, tableInfo.getType());
        return _updateBatch(updateChain, tableInfo, list, tableFieldInfos, idFieldInfos, batchFields == null || batchFields.length == 0);
    }

    private static <T> int _updateBatch(UpdateChain updateChain, TableInfo tableInfo, Collection<T> list, Collection<TableFieldInfo> updateTableFieldInfos, List<TableFieldInfo> idFieldInfos, boolean allUpdate) {
        Map<String, List<Serializable>> columnUpdateValues = new HashMap<>();
        Map<String, Object> defaultValueContext = new HashMap<>();
        for (T entity : list) {
            if (allUpdate) {
                for (TableFieldInfo tableFieldInfo : updateTableFieldInfos) {
                    EntityUpdateCreateUtil.initUpdateValue(tableFieldInfo, entity, Collections.EMPTY_SET, defaultValueContext);
                }
            }

            for (TableFieldInfo tableFieldInfo : updateTableFieldInfos) {
                List<Serializable> values = columnUpdateValues.get(tableFieldInfo.getColumnName());
                if (values == null) {
                    values = new ArrayList<>();
                    columnUpdateValues.put(tableFieldInfo.getColumnName(), values);
                }

                if (!allUpdate) {
                    EntityUpdateCreateUtil.initUpdateValue(tableFieldInfo, entity, Collections.EMPTY_SET, defaultValueContext);
                }

                values.add((Serializable) tableFieldInfo.getValue(entity));
            }
        }
        if (allUpdate) {
            //非局部修改 触发onUpdate操作
            //更新动作通知
            for (T entity : list) {
                OnListenerUtil.notifyUpdate(entity);
            }
        }
        for (TableFieldInfo tableFieldInfo : updateTableFieldInfos) {
            TableField tableField = updateChain.$().field(tableInfo.getType(), tableFieldInfo.getField().getName());
            if (tableField.isId()) {
                continue;
            }
            Case sqlCase = Methods.case_();
            for (int i = 0; i < list.size(); i++) {
                Object value = columnUpdateValues.get(tableField.getName()).get(i);
                if (value == null) {
                    value = Methods.NULL();
                }
                sqlCase.when(buildIdCaseWhen(updateChain, tableInfo, idFieldInfos, columnUpdateValues, i), Methods.cmd(value));
            }
            sqlCase.else_(tableField);
            updateChain.set(tableField, sqlCase);
        }

        idFieldInfos.stream().forEach(tableFieldInfo -> {
            TableField tableField = updateChain.$().field(tableInfo.getType(), tableFieldInfo.getField().getName());
            updateChain.in(tableField, columnUpdateValues.get(tableField.getName()));
        });

        OptimisticLockUtil.versionPlus1(tableInfo, updateChain);

        return updateChain
                .execute();
    }

    private static Condition buildIdCaseWhen(UpdateChain updateChain, TableInfo tableInfo, List<TableFieldInfo> idFieldInfos, Map<String, List<Serializable>> columnUpdateValues, int index) {
        Condition condition = null;
        for (TableFieldInfo tableFieldInfo : idFieldInfos) {
            List<Serializable> values = columnUpdateValues.get(tableFieldInfo.getColumnName());
            TableField tableField = updateChain.$().field(tableInfo.getType(), tableFieldInfo.getField().getName());
            if (condition == null) {
                condition = tableField.eq(values.get(index));
            }
        }
        return condition;
    }
}
