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
import cn.xbatis.core.mybatis.configuration.XbatisContextUtil;
import cn.xbatis.core.mybatis.executor.BasicMapperThreadLocalUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdCountFromQueryContext;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.util.*;
import cn.xbatis.db.annotations.ResultEntity;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.struct.Where;
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
    private final String FETCH_MATCH_COLUMN = "m$v";
    private final List<Object> rowValues = new ArrayList<>();
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
    private Map<String, Object> defaultValueContext = new HashMap<>();
    private Boolean hasFetchMatchColumn;

    public MybatisDefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        if (mappedStatement.getResultMaps().size() == 1) {
            Class<?> returnType = mappedStatement.getResultMaps().get(0).getType();
            Object parameterObject = boundSql.getParameterObject();
            BaseQuery<?, ?> baseQuery = XbatisContextUtil.getExecution(parameterObject);
            if (isNeedFetch(parameterObject, returnType)) {
                ResultInfo resultInfo = ResultInfos.get(returnType);
                this.fetchInfosMap = resultInfo.getFetchInfoMap();
                if (Objects.nonNull(this.fetchInfosMap) && !this.fetchInfosMap.isEmpty()) {
                    this.needFetchValuesMap = new HashMap<>();
                    this.basicMapper = BasicMapperThreadLocalUtil.get();
                }

                if (baseQuery != null) {
                    this.fetchFilters = baseQuery.getFetchFilters();
                    this.fetchEnables = baseQuery.getFetchEnables();
                    this.putEnumValueInfoMap = resultInfo.getPutEnumValueInfoMap();
                    this.putValueInfoMap = resultInfo.getPutValueInfoMap();
                    this.createdEventInfos = resultInfo.getCreatedEventInfos();
                }
            }

            if (baseQuery != null) {
                this.returnType = baseQuery.getReturnType();
                if (!(parameterObject instanceof SQLCmdCountFromQueryContext)) {
                    this.onRowEvent = baseQuery.getOnRowEvent();
                }
            }
        }
    }

    private static boolean isNeedFetch(Object parameterObject, Class<?> returnType) {
        if (parameterObject instanceof SQLCmdCountFromQueryContext) {
            return false;
        }
        if (!returnType.isAnnotationPresent(ResultEntity.class)) {
            return false;
        }

        if (parameterObject instanceof Map && XbatisContextUtil.getExecution(parameterObject) != null) {
            return true;
        }
        return parameterObject instanceof SQLCmdQueryContext;
    }

    private void clearObjects() {
        singleFetchCache.clear();
        createdEventContextMap.clear();
        defaultValueContext.clear();
        if (putValueSessionCache != null) {
            putValueSessionCache.clear();
        }
        rowValues.clear();

        singleFetchCache.entrySet().stream().forEach(i -> {
            if (i.getValue() == null) {
                return;
            }
            i.getValue().entrySet().stream().forEach(i2 -> {
                if (i2.getValue() != null) {
                    i2.getValue().clear();
                }
            });
            i.getValue().clear();
        });
        if (needFetchValuesMap != null) {
            needFetchValuesMap.entrySet().stream().forEach(i -> {
                if (i.getValue() != null) {
                    i.getValue().clear();
                }
            });
            needFetchValuesMap.clear();
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
        List<FetchInfo> fetchInfos = getFetchInfo(returnType);
        boolean hasFetch = false;
        if (!Objects.isNull(fetchInfos) && !fetchInfos.isEmpty()) {
            hasFetch = true;
            rowValue = this.loadFetchValue(resultMap.getType(), fetchInfos, rowValue, rsw.getResultSet());
        }

        this.putEnumValue(rowValue, rsw.getResultSet());
        this.putValue(rowValue, rsw.getResultSet());

        if (!hasFetch) {
            // 有Fetch 需要延后执行
            this.onCreatedEvent(rowValue);
            this.onRowEvent(rowValue);
        } else {
            rowValues.add(rowValue);
        }

        if (rowValue != null) {
            if (hasFetchMatchColumn == null) {
                List<String> columns = rsw.getColumnNames();
                hasFetchMatchColumn = columns.contains(FETCH_MATCH_COLUMN);
                if (!hasFetchMatchColumn) {
                    hasFetchMatchColumn = columns.contains(FETCH_MATCH_COLUMN.toUpperCase());
                }
            }
            if (hasFetchMatchColumn) {
                return new FetchTargetValue(rsw.getResultSet().getString(FETCH_MATCH_COLUMN), rowValue);
            }
        }

        return rowValue;
    }

    @Override
    protected Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, CacheKey combinedKey, String columnPrefix, Object partialObject) throws SQLException {
        Object rowValue = super.getRowValue(rsw, resultMap, combinedKey, columnPrefix, partialObject);

        List<FetchInfo> fetchInfos = getFetchInfo(resultMap.getType());
        boolean hasFetch = false;
        if (!Objects.isNull(fetchInfos) && !fetchInfos.isEmpty()) {
            hasFetch = true;
            rowValue = this.loadFetchValue(resultMap.getType(), fetchInfos, rowValue, rsw.getResultSet());
        }


        this.putEnumValue(rowValue, rsw.getResultSet());
        this.putValue(rowValue, rsw.getResultSet());

        if (!hasFetch) {
            this.onCreatedEvent(rowValue);
            this.onRowEvent(rowValue);
        } else {
            rowValues.add(rowValue);
        }
        return rowValue;
    }

    @Override
    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        super.handleRowValues(rsw, resultMap, resultHandler, rowBounds, parentMapping);
        this.handleFetch();
        for (Object obj : rowValues) {
            this.onCreatedEvent(obj);
            this.onRowEvent(obj);
        }

        this.clearObjects();
    }

    private List<FetchInfo> getFetchInfo(Class<?> resultType) {
        if (Objects.isNull(fetchInfosMap) || fetchInfosMap.isEmpty()) {
            return null;
        }
        return fetchInfosMap.get(resultType);
    }

    public Object loadFetchValue(Class<?> resultType, List<FetchInfo> fetchInfos, Object rowValue, ResultSet resultSet) {
        for (FetchInfo fetchInfo : fetchInfos) {
            String fetchKey = fetchInfo.getFieldInfo().getClazz().getName() + "." + fetchInfo.getFieldInfo().getField().getName();
            Boolean fetchEnable = Objects.isNull(fetchEnables) || !fetchEnables.containsKey(fetchKey) || fetchEnables.get(fetchKey);
            fetchEnable = fetchEnable == null || fetchEnable;
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

            if (fetchInfo.isSingleFetch()) {
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
            List<?> list = this.fetchData(fetchInfo, fetchObjects.stream().map(item -> item.getSourceKey()).distinct().collect(Collectors.toList()), false);
            this.fillFetchData(fetchInfo, fetchObjects, list);
        }
    }

    private List<Object> fetchData(FetchInfo fetchInfo, Query<?> query, List<Serializable> queryValueList) {
        if (queryValueList.isEmpty()) {
            return new ArrayList();
        }
        if (Objects.nonNull(query.$where().getConditionChain())) {
            query.$where().getConditionChain().getConditionBlocks().clear();
        }

        if (queryValueList.size() == 1) {
            if (fetchInfo.getFetch().limit() > 0 && !fetchInfo.getFetch().memoryLimit()) {
                query.limit(fetchInfo.getFetch().limit());
            }
            if (fetchInfo.getMiddleTableInfo() != null) {
                //有中间表
                query.eq(query.$(fetchInfo.getFetch().middle()).$(fetchInfo.getMiddleSourceTableFieldInfo().getColumnName()), queryValueList.get(0));
            } else {
                query.eq(query.$(fetchInfo.getFetch().target()).$(fetchInfo.getTargetTableFieldInfo().getColumnName()), queryValueList.get(0));
            }
        } else {
            if (fetchInfo.getMiddleTableInfo() != null) {
                //有中间表
                query.in(query.$(fetchInfo.getFetch().middle()).$(fetchInfo.getMiddleSourceTableFieldInfo().getColumnName()), queryValueList);
            } else {
                query.in(query.$(fetchInfo.getFetch().target()).$(fetchInfo.getTargetTableFieldInfo().getColumnName()), queryValueList);
            }
        }

        String fetchKey = fetchInfo.getFieldInfo().getClazz().getName() + "." + fetchInfo.getFieldInfo().getField().getName();
        boolean hasFetchFilter = !Objects.isNull(fetchFilters) && fetchFilters.containsKey(fetchKey);
        query.setFetchEnables(fetchEnables);
        query.setFetchFilters(fetchFilters);
        if (hasFetchFilter) {
            fetchFilters.get(fetchKey).accept(query.$where());
        }
        query.optimizeOptions(OptimizeOptions::disableAll);
        //增加额外条件
        if (Objects.nonNull(fetchInfo.getOtherConditions()) && !StringPool.EMPTY.equals(fetchInfo.getOtherConditions())) {
            query.and(q -> Methods.cTpl(fetchInfo.getOtherConditions()));
        }
        return (List<Object>) basicMapper.list(query);
    }

    public void fillFetchData(FetchInfo fetchInfo, List<FetchObject> values, List<?> fetchData) {
        if (Objects.isNull(fetchData) || fetchData.isEmpty()) {
            //需要Fetch数据是空的 则直接设置null
            values.stream().forEach(i -> setValue(i.getValue(), null, fetchInfo));
            return;
        }

        Map<String, List<Object>> map = new HashMap<>();
        fetchData.forEach(item -> {
            Object eqValue;
            try {
                if (fetchInfo.isSourceTargetMatchFieldInReturnType()) {
                    eqValue = fetchInfo.getSourceTargetMatchFieldGetter().invoke(item, null);
                } else {
                    if (fetchInfo.getMiddleTableInfo() != null || !fetchInfo.isSourceTargetMatchFieldInReturnType()) {
                        eqValue = ((FetchTargetValue) item).getMatchFieldValue();
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
            return new ArrayList();
        }
        int batchSize = 100;
        List queryValueList = new ArrayList<>(batchSize);
        Query<?> query = Query.create().returnType(fetchInfo.getReturnType());

        //如果有中间表
        if (fetchInfo.getMiddleTableInfo() != null) {
            query.from(fetchInfo.getFetch().middle());
            query.innerJoin(fetchInfo.getFetch().middle(), fetchInfo.getFetch().target(), on -> {
                on.getJoin().getMainTable().as("middle");
                on.getJoin().getSecondTable().as("target");
                on.eq(query.$(fetchInfo.getFetch().middle(), fetchInfo.getFetch().middleTargetProperty()), query.$(fetchInfo.getFetch().target(), fetchInfo.getFetch().targetProperty()));
            });
        } else {
            query.from(fetchInfo.getTargetTableInfo().getType());
        }

        if (fetchInfo.getTargetSelect() == null) {
            query.select(fetchInfo.getReturnType());
        } else {
            query.select(fetchInfo.getTargetSelect());
        }

        if (!single) {
            if (fetchInfo.getMiddleTableInfo() != null) {
                query.select(query.$(fetchInfo.getFetch().middle(), fetchInfo.getFetch().middleSourceProperty()).as(FETCH_MATCH_COLUMN));
            } else if (!fetchInfo.isSourceTargetMatchFieldInReturnType()) {
                query.select(query.$(fetchInfo.getFetch().target(), fetchInfo.getFetch().targetProperty()).as(FETCH_MATCH_COLUMN));
            }
        }

        if (fetchInfo.isGroup()) {
            if (fetchInfo.getMiddleTableInfo() != null) {
                query.groupBy(query.$(fetchInfo.getFetch().middle(), fetchInfo.getFetch().middleSourceProperty()));
            } else {
                query.groupBy(query.$(fetchInfo.getFetch().target(), fetchInfo.getFetch().targetProperty()));
            }
        }

        if (Objects.nonNull(fetchInfo.getTargetOrderBy()) && !StringPool.EMPTY.equals(fetchInfo.getTargetOrderBy())) {
            query.orderBy(OrderByDirection.NONE, fetchInfo.getTargetOrderBy());
        }

        if (conditionList.size() < batchSize) {
            //无需 分批次查
            return fetchData(fetchInfo, query, (List<Serializable>) conditionList);
        }

        List<Object> resultList = new ArrayList<>(conditionList.size());
        int size = conditionList.size();
        for (int i = 0; i < size; i++) {
            queryValueList.add(conditionList.get(i));
            if ((i + 1) % batchSize == 0) {
                //达到单次查询
                resultList.addAll(fetchData(fetchInfo, query, (List<Serializable>) queryValueList));
                queryValueList.clear();
            }
        }

        if (queryValueList.isEmpty()) {
            return resultList;
        }

        //还有没查询的 继续查询
        List<Object> list = fetchData(fetchInfo, query, (List<Serializable>) queryValueList);
        queryValueList.clear();
        if (!resultList.isEmpty()) {
            resultList.addAll(list);
        }

        return resultList;
    }

    public void singleConditionFetch(Object rowValue, FetchInfo fetchInfo, Object onValue) {
        List<Object> list;
        if (Objects.nonNull(onValue)) {
            list = singleFetchCache.computeIfAbsent(fetchInfo, key -> new HashMap<>()).computeIfAbsent(onValue, key2 -> {
                return this.fetchData(fetchInfo, Collections.singletonList(onValue), true);
            });
        } else {
            list = new ArrayList<>();
        }
        this.setValue(rowValue, list, fetchInfo);
    }

    protected void setValue(Object rowValue, List<?> matchValues, FetchInfo fetchInfo) {
        if (fetchInfo.isMultiple()) {
            matchValues = Objects.isNull(matchValues) ? new ArrayList<>() : matchValues;
            if (matchValues.isEmpty()) {
                fetchInfo.setValue(rowValue, matchValues, defaultValueContext);
                return;
            }
            if (matchValues.get(0) instanceof FetchTargetValue) {
                matchValues = ((List<FetchTargetValue>) matchValues)
                        .stream().map(m -> TypeConvertUtil.convert(m.getTarget(), fetchInfo.getFieldInfo().getFinalClass()))
                        .collect(Collectors.toList());
            }
            if (fetchInfo.getFetch().limit() > 0 && fetchInfo.getFetch().memoryLimit() && matchValues.size() > fetchInfo.getFetch().limit()) {
                matchValues = matchValues.stream().limit(fetchInfo.getFetch().limit()).collect(Collectors.toList());
            }
            fetchInfo.setValue(rowValue, matchValues, defaultValueContext);
        } else {
            if (Objects.isNull(matchValues) || matchValues.isEmpty()) {
                fetchInfo.setValue(rowValue, null, defaultValueContext);
                return;
            } else if (matchValues.size() > 1 && !fetchInfo.getFetch().multiValueErrorIgnore()) {
                throw new TooManyResultsException("fetch action found more than 1 record");
            }

            Object value;
            if (matchValues.get(0) instanceof FetchTargetValue) {
                value = ((List<FetchTargetValue>) matchValues)
                        .stream().map(m -> TypeConvertUtil.convert(m.getTarget(), fetchInfo.getFieldInfo().getFinalClass()))
                        .findFirst().get();
            } else {
                value = matchValues.get(0);
            }
            fetchInfo.setValue(rowValue, value, defaultValueContext);
        }
    }
}
