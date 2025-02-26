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

package cn.xbatis.core.mybatis.executor.resultset;

import cn.xbatis.core.db.reflect.*;
import cn.xbatis.core.mybatis.executor.BasicMapperThreadLocalUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.util.*;
import cn.xbatis.db.annotations.ResultEntity;
import db.sql.api.Cmd;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.Column;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.cmd.struct.query.Select;
import db.sql.api.impl.tookit.OptimizeOptions;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MybatisDefaultResultSetHandler extends DefaultResultSetHandler {

    private final Map<FetchInfo, Map<Object, List<Object>>> singleFetchCache = new HashMap<>();
    private final Map<Method, Map> createdEventContextMap = new HashMap<>();
    private BasicMapper basicMapper;
    private Map<FetchInfo, List<FetchObject>> needFetchValuesMap;
    //Fetch 信息
    private Map<Class, List<FetchInfo>> fetchInfosMap;
    private Map<String, Consumer<Where>> fetchFilters;
    private Map<String, Boolean> fetchEnables;
    private Consumer onRowEvent;
    private Class<?> returnType;
    private Map<Class, List<PutEnumValueInfo>> putEnumValueInfoMap;
    private Map<Class, List<PutValueInfo>> putValueInfoMap;
    private Map<String, Object> putValueSessionCache;
    private Map<Class, List<CreatedEventInfo>> createdEventInfos;


    public MybatisDefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        if (mappedStatement.getResultMaps().size() == 1) {
            Class<?> returnType = mappedStatement.getResultMaps().get(0).getType();
            if (returnType.isAnnotationPresent(ResultEntity.class)) {
                ResultInfo resultInfo = ResultInfos.get(returnType);
                this.fetchInfosMap = resultInfo.getFetchInfoMap();
                if (Objects.nonNull(this.fetchInfosMap) && !this.fetchInfosMap.isEmpty()) {
                    this.needFetchValuesMap = new HashMap<>();
                    this.basicMapper = BasicMapperThreadLocalUtil.get();
                }

                if (boundSql.getParameterObject() instanceof SQLCmdQueryContext) {
                    BaseQuery<?, ?> baseQuery = ((SQLCmdQueryContext) boundSql.getParameterObject()).getExecution();
                    this.fetchFilters = baseQuery.getFetchFilters();
                    this.fetchEnables = baseQuery.getFetchEnables();
                    this.putEnumValueInfoMap = resultInfo.getPutEnumValueInfoMap();
                    this.putValueInfoMap = resultInfo.getPutValueInfoMap();
                    this.createdEventInfos = resultInfo.getCreatedEventInfos();
                }
            }

            if (boundSql.getParameterObject() instanceof SQLCmdQueryContext) {
                BaseQuery<?, ?> baseQuery = ((SQLCmdQueryContext) boundSql.getParameterObject()).getExecution();
                this.onRowEvent = baseQuery.getOnRowEvent();
                this.returnType = baseQuery.getReturnType();
            }
        }
    }

    private void onRowEvent(Object rowValue) {
        if (Objects.isNull(onRowEvent)) {
            return;
        }
        if (Objects.isNull(rowValue)) {
            return;
        }
        if (!rowValue.getClass().isAssignableFrom(returnType)) {
            return;
        }
        onRowEvent.accept(rowValue);
    }

    private void putEnumValue(Object rowValue, ResultSet resultSet) {
        if (Objects.isNull(putEnumValueInfoMap)) {
            return;
        }
        if (Objects.isNull(rowValue)) {
            return;
        }

        List<PutEnumValueInfo> putEnumValueInfos = putEnumValueInfoMap.get(rowValue.getClass());
        if (Objects.isNull(putEnumValueInfos) || putEnumValueInfos.isEmpty()) {
            return;
        }
        putEnumValueInfos.forEach(item -> {
            Object codeValue;
            try {
                TypeHandler<?> typeHandler = item.getValueTypeHandler();
                if (Objects.isNull(typeHandler)) {
                    typeHandler = mappedStatement.getConfiguration().getTypeHandlerRegistry().getTypeHandler(item.getValueType());
                }
                if (Objects.nonNull(typeHandler)) {
                    codeValue = typeHandler.getResult(resultSet, item.getValueColumn());
                } else {
                    codeValue = resultSet.getObject(item.getValueColumn());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Object targetValue = PutEnumValueUtil.getEnumValue(codeValue, item);
            if (Objects.isNull(targetValue)) {
                return;
            }
            try {
                item.getWriteFieldInvoker().invoke(rowValue,
                        new Object[]{targetValue});
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Map<String, Object> getPutValueSessionCache() {
        if (putValueSessionCache == null) {
            putValueSessionCache = new HashMap<>();
        }
        return putValueSessionCache;
    }

    private void putValue(Object rowValue, ResultSet resultSet) {
        if (Objects.isNull(putValueInfoMap)) {
            return;
        }
        if (Objects.isNull(rowValue)) {
            return;
        }

        List<PutValueInfo> putValueInfos = putValueInfoMap.get(rowValue.getClass());
        if (Objects.isNull(putValueInfos) || putValueInfos.isEmpty()) {
            return;
        }
        putValueInfos.forEach(item -> {
            Object[] values = new Object[item.getValuesColumn().length];
            for (int i = 0; i < item.getValuesColumn().length; i++) {
                try {
                    TypeHandler<?> typeHandler = item.getValuesTypeHandler()[i];
                    if (Objects.isNull(typeHandler)) {
                        typeHandler = mappedStatement.getConfiguration().getTypeHandlerRegistry().getTypeHandler(item.getValueTypes()[i]);
                    }
                    if (Objects.nonNull(typeHandler)) {
                        values[i] = typeHandler.getResult(resultSet, item.getValuesColumn()[i]);
                    } else {
                        values[i] = resultSet.getObject(item.getValuesColumn()[i]);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            Object targetValue = PutValueUtil.getPutValue(values, item, getPutValueSessionCache());
            if (Objects.isNull(targetValue)) {
                return;
            }
            try {
                item.getWriteFieldInvoker().invoke(rowValue, new Object[]{targetValue});
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void onCreatedEvent(Object rowValue) {
        if (Objects.isNull(this.createdEventInfos) || this.createdEventInfos.isEmpty()) {
            return;
        }
        if (Objects.isNull(rowValue)) {
            return;
        }
        List<CreatedEventInfo> list = createdEventInfos.get(rowValue.getClass());
        if (Objects.isNull(list) || list.isEmpty()) {
            return;
        }
        Map context = null;
        for (CreatedEventInfo item : createdEventInfos.get(rowValue.getClass())) {
            if (item.isHasContextParam() && Objects.isNull(context)) {
                context = createdEventContextMap.computeIfAbsent(item.getMethod(), k -> new HashMap<>());
            } else {
                context = null;
            }
            CreatedEventUtil.onCreated(rowValue, context, item);
        }
    }

    @Override
    protected Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        Object rowValue = super.getRowValue(rsw, resultMap, columnPrefix);
        rowValue = this.loadFetchValue(resultMap.getType(), rowValue, rsw.getResultSet());
        this.putEnumValue(rowValue, rsw.getResultSet());
        this.putValue(rowValue, rsw.getResultSet());
        this.onRowEvent(rowValue);
        this.onCreatedEvent(rowValue);
        return rowValue;
    }

    @Override
    protected Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, CacheKey combinedKey, String columnPrefix, Object partialObject) throws SQLException {
        Object rowValue = super.getRowValue(rsw, resultMap, combinedKey, columnPrefix, partialObject);
        rowValue = this.loadFetchValue(resultMap.getType(), rowValue, rsw.getResultSet());
        this.putEnumValue(rowValue, rsw.getResultSet());
        this.putValue(rowValue, rsw.getResultSet());
        this.onRowEvent(rowValue);
        this.onCreatedEvent(rowValue);
        return rowValue;
    }

    @Override
    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        super.handleRowValues(rsw, resultMap, resultHandler, rowBounds, parentMapping);
        this.handleFetch();
    }

    public Object loadFetchValue(Class<?> resultType, Object rowValue, ResultSet resultSet) {
        if (Objects.isNull(fetchInfosMap) || fetchInfosMap.isEmpty()) {
            return rowValue;
        }

        List<FetchInfo> fetchInfos = fetchInfosMap.get(resultType);
        if (Objects.isNull(fetchInfos) || fetchInfos.isEmpty()) {
            return rowValue;
        }

        for (FetchInfo fetchInfo : fetchInfos) {
            String fetchKey = fetchInfo.getFieldInfo().getClazz().getName() + "." + fetchInfo.getField().getName();
            Boolean fetchEnable = Objects.isNull(fetchEnables) || !fetchEnables.containsKey(fetchKey) || fetchEnables.get(fetchKey);
            fetchEnable = fetchEnable == null ? true : fetchEnable;
            if (!fetchEnable) {
                if (fetchInfo.getFieldInfo().getTypeClass().isAssignableFrom(Collections.class)) {
                    try {
                        fetchInfo.getWriteFieldInvoker().invoke(rowValue, new Object[]{new ArrayList()});
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return rowValue;
            }

            Object onValue;
            try {
                if (Objects.nonNull(fetchInfo.getValueTypeHandler())) {
                    onValue = fetchInfo.getValueTypeHandler().getResult(resultSet, fetchInfo.getValueColumn());
                } else {
                    onValue = resultSet.getObject(fetchInfo.getValueColumn());
                    if (!(onValue instanceof Number)) {
                        onValue = resultSet.getString(fetchInfo.getValueColumn());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (Objects.isNull(onValue)) {
                continue;
            }

            if (Objects.isNull(rowValue)) {
                rowValue = configuration.getObjectFactory().create(resultType);
            }

            if (!fetchInfo.getFetch().forceUseIn() &&
                    (fetchInfo.getFetch().limit() > 0 || (Objects.nonNull(fetchInfo.getTargetSelectColumn()) && fetchInfo.getTargetSelectColumn().contains("(")))) {
                this.singleConditionFetch(rowValue, fetchInfo, onValue);
            } else {
                MapUtil.computeIfAbsent(needFetchValuesMap, fetchInfo, key -> new ArrayList<>()).add(new FetchObject(onValue, onValue.toString(), rowValue));
            }
        }

        return rowValue;
    }

    public void handleFetch() {
        if (Objects.isNull(this.needFetchValuesMap) || this.needFetchValuesMap.isEmpty()) {
            return;
        }
        for (Map.Entry<FetchInfo, List<FetchObject>> entry : needFetchValuesMap.entrySet()) {
            FetchInfo fetchInfo = entry.getKey();
            List<FetchObject> fetchObjects = entry.getValue();
            List<Object> list = this.fetchData(fetchInfo, fetchObjects.stream().map(item -> item.getSourceKey()).distinct().collect(Collectors.toList()), false);
            this.fillFetchData(fetchInfo, fetchObjects, list);
        }
    }

    private List<Object> fetchData(FetchInfo fetchInfo, Query<Object> query, List<Serializable> queryValueList) {
        if (queryValueList.isEmpty()) {
            return Collections.emptyList();
        }
        if (Objects.nonNull(query.$where().getConditionChain())) {
            query.$where().getConditionChain().getConditionBlocks().clear();
        }

        query.from(fetchInfo.getFetch().target(), table -> {
            if (queryValueList.size() == 1) {
                if (fetchInfo.getFetch().limit() > 0) {
                    query.limit(fetchInfo.getFetch().limit());
                }
                query.eq(table.$(fetchInfo.getTargetMatchColumn()), queryValueList.get(0));
            } else {
                query.in(table.$(fetchInfo.getTargetMatchColumn()), queryValueList);
            }
        });

        String fetchKey = fetchInfo.getFieldInfo().getClazz().getName() + "." + fetchInfo.getField().getName();
        boolean hasFetchFilter = !Objects.isNull(fetchFilters) && fetchFilters.containsKey(fetchKey);
        query.setFetchEnables(fetchEnables);
        query.setFetchFilters(fetchFilters);
        if (hasFetchFilter) {
            fetchFilters.get(fetchKey).accept(query.$where());
        }
        query.optimizeOptions(OptimizeOptions::disableAll);
        return basicMapper.list(query);
    }

    public void fillFetchData(FetchInfo fetchInfo, List<FetchObject> values, List<Object> fetchData) {
        if (Objects.isNull(fetchData) || fetchData.isEmpty()) {
            //需要Fetch数据是空的 则直接设置null
            values.stream().forEach(i -> setValue(i.getValue(), null, fetchInfo));
            return;
        }

        Map<String, List<Object>> map = new HashMap<>();
        fetchData.forEach(item -> {
            Object eqValue;
            try {
                if (Objects.nonNull(fetchInfo.getEqGetFieldInvoker())) {
                    eqValue = fetchInfo.getEqGetFieldInvoker().invoke(item, null);
                } else {
                    if (fetchInfo.isUseResultFetchKeyValue()) {
                        eqValue = ((FetchKeyValue) item).getKey();
                    } else {
                        eqValue = item;
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(eqValue)) {
                List<Object> conditionValues = MapUtil.computeIfAbsent(map, eqValue.toString(), key -> new ArrayList<>());
                if (!conditionValues.contains(item)) {
                    conditionValues.add(item);
                }
            }
        });

        values.forEach(item -> {
            List<Object> matchValues = map.get(item.getMatchKey());
            this.setValue(item.getValue(), matchValues, fetchInfo);
        });
    }

    protected List<Object> fetchData(FetchInfo fetchInfo, List conditionList, boolean single) {
        if (conditionList.isEmpty()) {
            return Collections.emptyList();
        }
        int batchSize = 100;
        List queryValueList = new ArrayList<>(100);
        Query query = Query.create().returnType(fetchInfo.getReturnType());

        if (Objects.isNull(fetchInfo.getTargetSelectColumn()) || StringPool.EMPTY.equals(fetchInfo.getTargetSelectColumn())) {
            query.select(fetchInfo.getReturnType());
            Select select = query.getSelect();
            Set<String> selectColumns = new HashSet<>();
            if (select != null && select.getSelectField() != null) {
                List<Cmd> selectFields = select.getSelectField();
                selectFields.stream().forEach(item -> {
                    if (item instanceof TableField) {
                        selectColumns.add(((TableField) item).getName());
                    }
                });
            }

            if (!selectColumns.contains(fetchInfo.getTargetMatchColumn())) {
                query.select(new Column(fetchInfo.getTargetMatchColumn()));
            }
        } else {
            if (!single && fetchInfo.isUseResultFetchKeyValue()) {
                query.setReturnType(FetchKeyValue.class);
                query.select(new Column(fetchInfo.getTargetMatchColumn()).as(FetchKeyValue::getKey));
                query.select(new Column(fetchInfo.getTargetSelectColumn()).as(FetchKeyValue::getValue));
            } else if (!fetchInfo.getTargetSelectColumn().contains("(")) {
                query.select(new Column(fetchInfo.getTargetSelectColumn()));
                query.select(new Column(fetchInfo.getTargetMatchColumn()));
            } else {
                query.select(new Column(fetchInfo.getTargetSelectColumn()));
            }
        }
        if (Objects.nonNull(fetchInfo.getOrderBy()) && !StringPool.EMPTY.equals(fetchInfo.getOrderBy())) {
            query.orderBy(OrderByDirection.NONE, fetchInfo.getOrderBy());
        }

        if (Objects.nonNull(fetchInfo.getGroupBy()) && !StringPool.EMPTY.equals(fetchInfo.getGroupBy())) {
            query.groupBy(fetchInfo.getGroupBy());
        }

        if (Objects.nonNull(fetchInfo.getOtherConditions()) && !StringPool.EMPTY.equals(fetchInfo.getOtherConditions())) {
            query.and(q -> Methods.cTpl(fetchInfo.getOtherConditions()));
        }

        List<Object> resultList = new ArrayList<>(conditionList.size());
        int size = conditionList.size();
        for (int i = 0; i < size; i++) {
            queryValueList.add(conditionList.get(i));
            if (i != 0 && i % batchSize == 0) {
                resultList.addAll(fetchData(fetchInfo, query, (List<Serializable>) queryValueList));
            }
        }
        if (size == 1 || (size - 1) % batchSize != 0) {
            List<Object> list = fetchData(fetchInfo, query, (List<Serializable>) queryValueList);
            if (resultList.isEmpty()) {
                return list;
            }
            resultList.addAll(list);
        }
        return resultList;
    }

    public void singleConditionFetch(Object rowValue, FetchInfo fetchInfo, Object onValue) {
        List<Object> list;
        if (Objects.nonNull(onValue)) {
            list = singleFetchCache.computeIfAbsent(fetchInfo, key -> new HashMap<>()).computeIfAbsent(onValue, key2 -> this.fetchData(fetchInfo, Collections.singletonList(onValue), true));
        } else {
            list = new ArrayList<>();
        }
        this.setValue(rowValue, list, fetchInfo);
    }

    protected void setValue(Object rowValue, List<?> matchValues, FetchInfo fetchInfo) {
        if (fetchInfo.isMultiple()) {
            matchValues = Objects.isNull(matchValues) ? new ArrayList<>() : matchValues;
            if (matchValues.isEmpty()) {
                fetchInfo.setValue(rowValue, matchValues);
                return;
            }
            if (fetchInfo.isUseResultFetchKeyValue() && matchValues.get(0) instanceof FetchKeyValue) {
                matchValues = ((List<FetchKeyValue>) matchValues)
                        .stream().map(m -> TypeConvertUtil.convert(m.getValue(), fetchInfo.getFieldInfo().getFinalClass()))
                        .collect(Collectors.toList());
            }
            fetchInfo.setValue(rowValue, matchValues);
        } else {
            if (Objects.isNull(matchValues) || matchValues.isEmpty()) {
                fetchInfo.setValue(rowValue, null);
                return;
            } else if (matchValues.size() > 1 && !fetchInfo.getFetch().multiValueErrorIgnore()) {
                throw new TooManyResultsException("fetch action found more than 1 record");
            }

            Object value;
            if (fetchInfo.isUseResultFetchKeyValue() && matchValues.get(0) instanceof FetchKeyValue) {
                value = ((List<FetchKeyValue>) matchValues)
                        .stream().map(m -> TypeConvertUtil.convert(m.getValue(), fetchInfo.getFieldInfo().getFinalClass()))
                        .findFirst().get();
            } else {
                value = matchValues.get(0);
            }
            fetchInfo.setValue(rowValue, value);
        }
    }
}
