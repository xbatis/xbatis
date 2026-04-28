/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.cache.FetchCache;
import cn.xbatis.core.db.reflect.*;
import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdCountFromQueryContext;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.mybatis.mapper.context.XbatisContextUtil;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.util.WhereUtil;
import cn.xbatis.core.util.*;
import cn.xbatis.db.FetchLogicDeleteStrategy;
import cn.xbatis.db.FetchPropertyType;
import cn.xbatis.db.annotations.ResultEntity;
import db.sql.api.IDbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.OptimizeOptions;
import db.sql.api.impl.tookit.SQLPrinter;
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

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MybatisDefaultResultSetHandler extends DefaultResultSetHandler {
    private final String FETCH_MATCH_COLUMN = "m$v";
    private final Map<Method, Map> createdEventContextMap = new HashMap<>();
    private final List<Object> rowValues = new ArrayList<>();


    private final Map<FetchInfo, Map<Object, List<Object>>> singleFetchCache = new HashMap<>();
    private BasicMapper basicMapper;
    private Map<Class, List<FetchInfo>> filteredFetchInfosMap;
    //Fetch 信息
    private Map<Class, Map<String, List<FetchInfo>>> waitFetchGroupInfoMap;
    private Map<String, Set<Object>> waitFetchOnValusMap;
    private Map<String, List<FetchPut>> waitFetchPutMap;
    private Map<String, Map<Object, List<FetchPut>>> waitFetchPutValueMap;

    private Map<String, Consumer<Where>> fetchFilters;
    private Map<String, Boolean> fetchEnables;

    private BaseQuery<?, ?> baseQuery;

    private Consumer onRowEvent;
    private Class<?> returnType;
    private Map<Class, List<PutEnumValueInfo>> putEnumValueInfoMap;
    private Map<Class, List<PutValueInfo>> putValueInfoMap;
    private Map<String, Object> putValueSessionCache;
    private Map<Class, List<CreatedEventInfo>> createdEventInfos;
    private Map<String, Object> defaultValueContext = new HashMap<>();
    private Boolean hasFetchMatchColumn;
    private IDbType dbType;

    public MybatisDefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);

        if (mappedStatement.getResultMaps().size() == 1) {
            Class<?> returnType = mappedStatement.getResultMaps().get(0).getType();
            Object parameterObject = boundSql.getParameterObject();
            baseQuery = XbatisContextUtil.getQueryExecution(parameterObject);
            this.dbType = XbatisContextUtil.getDbType(parameterObject);
            if (isNeedFetch(parameterObject, returnType)) {
                ResultInfo resultInfo = ResultInfos.get(returnType);
                if (baseQuery != null) {
                    this.fetchFilters = baseQuery.getFetchFilters();
                    this.fetchEnables = baseQuery.getFetchEnables();
                    this.putEnumValueInfoMap = resultInfo.getPutEnumValueInfoMap();
                    this.putValueInfoMap = resultInfo.getPutValueInfoMap();
                    this.createdEventInfos = resultInfo.getCreatedEventInfos();

                    if (Objects.nonNull(resultInfo.getFetchInfoMap()) && !resultInfo.getFetchInfoMap().isEmpty()) {
                        this.waitFetchOnValusMap = new HashMap<>();
                        this.waitFetchPutMap = new HashMap<>();
                        this.waitFetchPutValueMap = new HashMap<>();

                        this.basicMapper = XbatisContextUtil.getBasicMapper(parameterObject);

                        this.waitFetchGroupInfoMap = new HashMap<>();
                        if (this.fetchEnables == null || this.fetchEnables.isEmpty()) {
                            for (Map.Entry<Class, List<FetchInfo>> entry : resultInfo.getFetchInfoMap().entrySet()) {
                                waitFetchGroupInfoMap.put(entry.getKey(), entry.getValue().stream().collect(Collectors.groupingBy(FetchInfo::getFetchGroup)));
                            }
                        } else {
                            this.filteredFetchInfosMap = new HashMap<>();
                            resultInfo.getFetchInfoMap().forEach((key, value) -> {
                                if (value == null || value.isEmpty()) {
                                    return;
                                }
                                value.stream().forEach(item -> {
                                    Boolean enable;
                                    if (this.fetchEnables == null) {
                                        enable = Boolean.FALSE;
                                    } else {
                                        enable = this.fetchEnables.get(item.getFetchKey());
                                    }
                                    if ((enable == null || enable) && !item.getFetch().mergeGroup().isEmpty()) {
                                        enable = this.fetchEnables.get(item.getFetch().mergeGroup());
                                    }
                                    if (enable != null && !enable) {
                                        List<FetchInfo> fetchInfos = filteredFetchInfosMap.get(key);
                                        if (fetchInfos == null) {
                                            fetchInfos = new ArrayList<>();
                                            filteredFetchInfosMap.put(key, fetchInfos);
                                        }
                                        fetchInfos.add(item);
                                    } else {
                                        Map<String, List<FetchInfo>> map = waitFetchGroupInfoMap.computeIfAbsent(key, k -> new HashMap<>());
                                        map.computeIfAbsent(item.getFetchGroup(), k -> new ArrayList<>()).add(item);
                                    }
                                });
                            });
                        }
                    }
                }
            }

            if (baseQuery != null) {
                this.returnType = baseQuery.getReturnType();
                if (!XbatisContextUtil.isCmdCountFromQueryContext(parameterObject)) {
                    this.onRowEvent = baseQuery.getOnRowEvent();
                }
            }
        }
    }

    private void clearObjects() {
        rowValues.clear();

        if (waitFetchOnValusMap != null) {
            waitFetchOnValusMap.entrySet().stream().forEach(i -> {
                if (i.getValue() != null) {
                    i.getValue().clear();
                }
            });
            waitFetchOnValusMap.clear();
        }

        if (waitFetchPutMap != null) {
            waitFetchPutMap.entrySet().stream().forEach(i -> {
                if (i.getValue() != null) {
                    i.getValue().clear();
                }
            });
            waitFetchPutMap.clear();
        }
        if (waitFetchPutValueMap != null) {
            waitFetchPutValueMap.entrySet().stream().forEach(i -> {
                for (Map.Entry<Object, List<FetchPut>> entry : i.getValue().entrySet()) {
                    entry.getValue().clear();
                }
                i.getValue().clear();
            });
            waitFetchPutValueMap.clear();
        }
    }

    private static boolean isNeedFetch(Object parameterObject, Class<?> returnType) {
        if (parameterObject instanceof SQLCmdCountFromQueryContext) {
            return false;
        }

        if (!returnType.isAnnotationPresent(ResultEntity.class)) {
            return false;
        }

        if (parameterObject instanceof SQLCmdQueryContext) {
            return true;
        }

        if (parameterObject instanceof Map) {
            if (XbatisContextUtil.isCmdCountFromQueryContext(parameterObject)) {
                return false;
            }
            if (XbatisContextUtil.getQueryExecution(parameterObject) != null) {
                return true;
            }
        }
        return false;
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
        Map<String, List<FetchInfo>> fetchMap = getFetchGroupInfo(returnType);
        boolean hasFetch = false;
        if (!Objects.isNull(fetchMap) && !fetchMap.isEmpty()) {
            hasFetch = true;
            List<FetchInfo> filteredFetchInfos = this.filteredFetchInfosMap == null ? null : this.filteredFetchInfosMap.get(returnType);
            rowValue = this.loadFetchValue(resultMap.getType(), fetchMap, filteredFetchInfos, rowValue, rsw.getResultSet());
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
                    hasFetchMatchColumn = columns.contains(getUpperCase(FETCH_MATCH_COLUMN));
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

        Map<String, List<FetchInfo>> fetchInfos = getFetchGroupInfo(resultMap.getType());
        boolean hasFetch = false;
        if (!Objects.isNull(fetchInfos) && !fetchInfos.isEmpty()) {
            hasFetch = true;
            List<FetchInfo> filteredFetchInfos = this.filteredFetchInfosMap == null ? null : this.filteredFetchInfosMap.get(returnType);
            rowValue = this.loadFetchValue(resultMap.getType(), fetchInfos, filteredFetchInfos, rowValue, rsw.getResultSet());
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

    private Map<String, List<FetchInfo>> getFetchGroupInfo(Class<?> resultType) {
        if (Objects.isNull(waitFetchGroupInfoMap) || waitFetchGroupInfoMap.isEmpty()) {
            return null;
        }
        return waitFetchGroupInfoMap.get(resultType);
    }

    private TypeHandler<?> getFetchOnValueTypeHandler(FetchInfo fetchInfo) {
        TypeHandler<?> typeHandler = fetchInfo.getTargetTableFieldInfo().getTypeHandler();
        if (typeHandler == null) {
            typeHandler = mappedStatement.getConfiguration().getTypeHandlerRegistry().getTypeHandler(fetchInfo.getTargetTableFieldInfo().getFieldInfo().getTypeClass());
            fetchInfo.setOnValueTypeHandler(typeHandler);
        }
        return typeHandler;
    }

    /**
     * 获取Fetch的on的值
     *
     * @param fetchInfo
     * @param resultSet
     * @return
     */
    public Object getFetchOnValue(FetchInfo fetchInfo, ResultSet resultSet) {
        Object onValue;
        try {
            TypeHandler<?> typeHandler = getFetchOnValueTypeHandler(fetchInfo);
            if (fetchInfo.getFetch().propertyType() == FetchPropertyType.SIMPLE) {
                onValue = typeHandler.getResult(resultSet, fetchInfo.getValueColumn());
            } else {
                onValue = resultSet.getString(fetchInfo.getValueColumn());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return onValue;
    }

    /**
     * 构建缓存key
     *
     * @param onValue
     * @param mainFetchInfo        主FetchInfo
     * @param mergeGroupFetchInfos 分组FetchInfos
     * @return 缓存key
     */
    public String buildFetchCacheKey(Object onValue, FetchInfo mainFetchInfo, List<FetchInfo> mergeGroupFetchInfos) {
        if (mainFetchInfo.getFetch().cacheName().isEmpty()) {
            return null;
        }
        String cacheKey = null;
        if (fetchFilters != null) {
            Where where = WhereUtil.create();
            this.appendWhereConditions(where, mainFetchInfo, mergeGroupFetchInfos);
            if (where.hasContent()) {
                cacheKey = onValue + "-" + SQLPrinter.sql(dbType, where);
            }
        }
        if (cacheKey == null && onValue != null) {
            cacheKey = onValue.toString();
        }
        return cacheKey;
    }

    /**
     * 拼接where
     *
     * @param mainFetchInfo        主FetchInfo
     * @param mergeGroupFetchInfos 分组FetchInfos
     *
     */
    public void appendWhereConditions(Where where, FetchInfo mainFetchInfo, List<FetchInfo> mergeGroupFetchInfos) {
        if (fetchFilters != null) {
            Consumer<Where> fetchFilter;
            if (mergeGroupFetchInfos != null) {
                for (FetchInfo fetchInfo : mergeGroupFetchInfos) {
                    fetchFilter = this.fetchFilters.get(fetchInfo.getFetchKey());
                    if (fetchFilter != null) {
                        fetchFilter.accept(where);
                    }
                }
            } else {
                fetchFilter = this.fetchFilters.get(mainFetchInfo.getFetchKey());
                if (fetchFilter != null) {
                    fetchFilter.accept(where);
                }
            }
            if (!mainFetchInfo.getFetch().mergeGroup().isEmpty()) {
                fetchFilter = this.fetchFilters.get(mainFetchInfo.getFetch().mergeGroup());
                if (fetchFilter != null) {
                    fetchFilter.accept(where);
                }
            }
        }
    }

    private boolean isNeedFetchWithFinalOnValue(FetchInfo fetchInfo, Object finalOnValue, ResultSet resultSet) {
        if (Objects.isNull(finalOnValue)) {
            return false;
        }

        if (finalOnValue instanceof Collection) {
            Collection onValues = (Collection) finalOnValue;
            if (onValues.isEmpty()) {
                return false;
            }
        }
        if (fetchInfo.getWhens().isEmpty()) {
            return true;
        }

        for (FetchWhenInfo when : fetchInfo.getWhens()) {
            try {
                if (when.getPropertyTypeHandler() == null) {
                    when.setPropertyTypeHandler(configuration.getTypeHandlerRegistry().getTypeHandler(when.getProperty().getFieldInfo().getFinalClass()));
                }
                Object dbValue = when.getPropertyTypeHandler().getResult(resultSet, when.getColumn());
                if (dbValue == null) {
                    return false;
                }
                if (dbValue instanceof LocalDateTime && when.getValue() instanceof LocalDateTime) {
                    if (!Objects.equals(((LocalDateTime) when.getValue()).toLocalDate(), ((LocalDateTime) dbValue).toLocalDate())) {
                        return false;
                    }
                } else {
                    if (!Objects.equals(when.getValue(), dbValue)) {
                        return false;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public Object loadFetchValue(Class<?> resultType, Map<String, List<FetchInfo>> fetchMap, List<FetchInfo> filteredFetchInfos, Object rowValue, ResultSet resultSet) {
        for (Map.Entry<String, List<FetchInfo>> entry : fetchMap.entrySet()) {
            List<FetchPut> fetchPuts = new ArrayList<>();
            Map<String, Object> hasReadOnValueMap = new HashMap<>();
            String cacheKey = null;
            for (FetchInfo fetchInfo : entry.getValue()) {
                Object onValue;
                if (hasReadOnValueMap.containsKey(fetchInfo.getValueColumn())) {
                    onValue = hasReadOnValueMap.get(fetchInfo.getValueColumn());
                } else {
                    onValue = this.getFetchOnValue(fetchInfo, resultSet);
                    hasReadOnValueMap.put(fetchInfo.getValueColumn(), onValue);
                }

                if (Objects.isNull(onValue)) {
                    if (rowValue != null) {
                        fetchInfo.setValue(rowValue, null, this.defaultValueContext);
                    }
                    continue;
                }

                Object finalOnValue = this.getFinalFetchOnValue(fetchInfo, onValue);
                if (finalOnValue == null || (finalOnValue instanceof Collection && ((Collection) finalOnValue).isEmpty())) {
                    if (rowValue != null) {
                        fetchInfo.setValue(rowValue, null, this.defaultValueContext);
                    }
                    continue;
                }

                if (Objects.isNull(rowValue)) {
                    rowValue = configuration.getObjectFactory().create(resultType);
                }

                if (!isNeedFetchWithFinalOnValue(fetchInfo, finalOnValue, resultSet)) {
                    //不匹配 需要跳过
                    fetchInfo.setValue(rowValue, null, this.defaultValueContext);
                    continue;
                }

                FetchCache fetchCache = XbatisGlobalConfig.getFetchCache();
                if (fetchCache != null && !fetchInfo.getFetch().cacheName().isEmpty()) {
                    cacheKey = this.buildFetchCacheKey(onValue, fetchInfo, entry.getValue());
                    Object cacheValue = fetchCache.get(fetchInfo.getFetch().cacheName(), fetchInfo.getFetch(), fetchInfo.getFieldInfo(), cacheKey);
                    if (Objects.nonNull(cacheValue)) {
                        if (cacheValue instanceof cn.xbatis.core.cache.NULL) {
                            cacheValue = null;
                        }
                        fetchInfo.setValue(rowValue, cacheValue, this.defaultValueContext);
                        continue;
                    }
                }

                if (fetchInfo.isSingleFetch()) {
                    //单个 不需要后续处理
                    this.singleFetch(rowValue, fetchInfo, finalOnValue, cacheKey);
                    continue;
                }

                Set onValues = waitFetchOnValusMap.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
                if (finalOnValue instanceof Collection) {
                    onValues.addAll((Collection) finalOnValue);
                } else {
                    onValues.add(finalOnValue);
                }

                FetchPut fetchPut = new FetchPut(rowValue, finalOnValue, fetchInfo, cacheKey);
                fetchPuts.add(fetchPut);

                if (finalOnValue instanceof Collection) {
                    for (Object v : (Collection) finalOnValue) {
                        waitFetchPutValueMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).computeIfAbsent(v, k -> new ArrayList<>()).add(fetchPut);
                    }
                } else {
                    waitFetchPutValueMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).computeIfAbsent(finalOnValue, k -> new ArrayList<>()).add(fetchPut);
                }
            }

            if (!fetchPuts.isEmpty()) {
                waitFetchPutMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(fetchPuts);
            }
        }

        if (rowValue != null && filteredFetchInfos != null && !filteredFetchInfos.isEmpty()) {
            for (FetchInfo fetchInfo : filteredFetchInfos) {
                setToFetchValue(rowValue, null, fetchInfo, null);
            }
        }
        return rowValue;
    }

    public void handleFetch() {
        if (Objects.isNull(this.waitFetchGroupInfoMap) || this.waitFetchGroupInfoMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Class, Map<String, List<FetchInfo>>> entry : waitFetchGroupInfoMap.entrySet()) {
            for (Map.Entry<String, List<FetchInfo>> secondEntry : entry.getValue().entrySet()) {
                List<FetchPut> fetchPuts = waitFetchPutMap.get(secondEntry.getKey());
                if (fetchPuts == null || fetchPuts.isEmpty()) {
                    continue;
                }

                Set<Object> values = this.waitFetchOnValusMap.get(secondEntry.getKey());
                if (secondEntry.getValue().size() == 1) {
                    //非分组
                    FetchInfo firstFetchInfo = secondEntry.getValue().get(0);
                    List list;
                    if (values == null || values.isEmpty()) {
                        list = new ArrayList();
                    } else {
                        list = this.fetchData(firstFetchInfo, values, false, null, null);
                    }

                    list.stream().forEach(i -> {
                        Object matchValue;
                        Object result;
                        if (i instanceof FetchTargetValue) {
                            FetchTargetValue fetchTargetValue = (FetchTargetValue) i;
                            matchValue = TypeConvertUtil.convert(fetchTargetValue.getMatchFieldValue(), firstFetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass());
                            result = fetchTargetValue.getTarget();
                        } else {
                            try {
                                matchValue = firstFetchInfo.getSourceTargetMatchFieldGetter().invoke(i, null);
                                matchValue = TypeConvertUtil.convert(matchValue, firstFetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            result = i;
                        }

                        List<FetchPut> needPutFetchPuts = waitFetchPutValueMap.get(secondEntry.getKey()).get(matchValue);
                        for (FetchPut fetchPut : needPutFetchPuts) {
                            fetchPut.putValue(matchValue, result);
                        }
                    });

                    for (FetchPut j : fetchPuts) {
                        setToFetchValue(j.getRowValue(), j.getValues().stream().filter(Objects::nonNull).collect(Collectors.toList()), j.getFetchInfo(), j.getCacheKey());
                    }
                } else {
                    // 有分组fetch
                    FetchInfo firstFetchInfo = secondEntry.getValue().get(0);
                    List list;
                    if (values == null || values.isEmpty()) {
                        list = new ArrayList();
                    } else {
                        list = this.fetchData(secondEntry.getValue().get(0), values, false, secondEntry.getValue(), query -> {
                            if (secondEntry.getValue() != null) {
                                if (firstFetchInfo.getTargetSelect() == null) {
                                    query.select(firstFetchInfo.getFieldInfo().getFinalClass());
                                    query.returnType(firstFetchInfo.getFieldInfo().getFinalClass());
                                } else {
                                    Set<String> selected = new HashSet<>();
                                    for (FetchInfo fetchInfo : secondEntry.getValue()) {
                                        if (selected.contains(fetchInfo.getTargetSelect())) {
                                            continue;
                                        }
                                        selected.add(fetchInfo.getTargetSelect());
                                        query.select(fetchInfo.getTargetSelect());
                                    }
                                    query.returnType(secondEntry.getValue().get(0).getTargetTableInfo().getType());
                                }
                            }
                        });
                    }

                    list.stream().forEach(i -> {
                        Object matchValue;
                        Object result;
                        if (i instanceof FetchTargetValue) {
                            FetchTargetValue fetchTargetValue = (FetchTargetValue) i;
                            matchValue = TypeConvertUtil.convert(fetchTargetValue.getMatchFieldValue(), firstFetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass());
                            result = fetchTargetValue.getTarget();
                        } else {
                            try {
                                matchValue = firstFetchInfo.getSourceTargetMatchFieldGetter().invoke(i, null);
                                matchValue = TypeConvertUtil.convert(matchValue, firstFetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            result = i;
                        }


                        List<FetchPut> needPutFetchPuts = waitFetchPutValueMap.get(secondEntry.getKey()).get(matchValue);
                        if (firstFetchInfo.getTargetSelect() == null) {
                            for (FetchPut fetchPut : needPutFetchPuts) {
                                fetchPut.putValue(matchValue, result);
                            }
                        } else {
                            for (FetchPut fetchPut : needPutFetchPuts) {
                                Object value = fetchPut.getFetchInfo().getTargetSelectTableFieldInfo().getValue(result);
                                value = TypeConvertUtil.convert(value,fetchPut.getFetchInfo().getFieldInfo().getFinalClass());
                                fetchPut.putValue(matchValue, value);
                            }
                        }
                    });

                    fetchPuts.stream().forEach(j -> {
                        setToFetchValue(j.getRowValue(), j.getValues().stream().filter(Objects::nonNull).collect(Collectors.toList()), j.getFetchInfo(), j.getCacheKey());
                    });
                }
            }
        }
    }

    private <T> List<T> fetchDataWithQuery(FetchInfo fetchInfo, Query<?> query, Collection queryValueList, List<FetchInfo> mergeGroupFetchInfos) {
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
                query.eq(query.$(fetchInfo.getFetch().middle()).$(fetchInfo.getMiddleSourceTableFieldInfo().getColumnName()), queryValueList.iterator().next());
            } else {
                query.eq(query.$(fetchInfo.getFetch().target()).$(fetchInfo.getTargetTableFieldInfo().getColumnName()), queryValueList.iterator().next());
            }
        } else {
            if (fetchInfo.getMiddleTableInfo() != null) {
                //有中间表
                query.in(query.$(fetchInfo.getFetch().middle()).$(fetchInfo.getMiddleSourceTableFieldInfo().getColumnName()), queryValueList);
            } else {
                query.in(query.$(fetchInfo.getFetch().target()).$(fetchInfo.getTargetTableFieldInfo().getColumnName()), queryValueList);
            }
        }

        if (baseQuery.isEnableLog() && (baseQuery.getLogger() != null && !baseQuery.getLogger().isEmpty())) {
            query.log(baseQuery.getLogger() + ".$" + fetchInfo.getFieldInfo().getField().getName());
        }
        this.appendWhereConditions(query.$where(), fetchInfo, mergeGroupFetchInfos);

        //增加额外条件
        if (Objects.nonNull(fetchInfo.getOtherConditions()) && !StringPool.EMPTY.equals(fetchInfo.getOtherConditions())) {
            query.and(q -> Methods.cTpl(fetchInfo.getOtherConditions()));
        }

        switch (fetchInfo.getFetch().logicDeleteStrategy()) {
            case DEFAULT:
                return (List) basicMapper.list(query);
            default:
                return LogicDeleteUtil.execute(fetchInfo.getFetch().logicDeleteStrategy() == FetchLogicDeleteStrategy.INCLUDE, () -> {
                    return (List) basicMapper.list(query);
                });
        }
    }

    protected <T> List<T> fetchData(FetchInfo mainFetchInfo, Collection conditionList, boolean single, List<FetchInfo> mergeGroupFetchInfos, Consumer<Query> queryConsumer) {
        if (conditionList.isEmpty()) {
            return new ArrayList();
        }

        Query<?> query = Query.create();
        query.returnType(mainFetchInfo.getReturnType());

        //如果有中间表
        if (mainFetchInfo.getMiddleTableInfo() != null) {
            query.from(mainFetchInfo.getFetch().middle());
            query.innerJoin(mainFetchInfo.getFetch().middle(), mainFetchInfo.getFetch().target(), on -> {
                on.getJoin().getMainTable().as("middle");
                on.getJoin().getSecondTable().as("target");
                on.eq(query.$(mainFetchInfo.getFetch().middle(), mainFetchInfo.getFetch().middleTargetProperty()), query.$(mainFetchInfo.getFetch().target(), mainFetchInfo.getFetch().targetProperty()));
            });
        } else {
            query.from(mainFetchInfo.getTargetTableInfo().getType());
        }

        if (queryConsumer != null) {
            queryConsumer.accept(query);
        }

        if (query.getSelect() == null) {
            if (mainFetchInfo.getTargetSelect() == null) {
                query.select(mainFetchInfo.getReturnType());
            } else {
                query.select(mainFetchInfo.getTargetSelect());
            }
        }

        if (!single) {
            if (mainFetchInfo.getMiddleTableInfo() != null) {
                query.select(query.$(mainFetchInfo.getFetch().middle(), mainFetchInfo.getFetch().middleSourceProperty()).as(FETCH_MATCH_COLUMN));
            } else if (!mainFetchInfo.isSourceTargetMatchFieldInReturnType()) {
                query.select(query.$(mainFetchInfo.getFetch().target(), mainFetchInfo.getFetch().targetProperty()).as(FETCH_MATCH_COLUMN));
            }
        }

        if (mainFetchInfo.isGroup()) {
            if (mainFetchInfo.getMiddleTableInfo() != null) {
                query.groupBy(query.$(mainFetchInfo.getFetch().middle(), mainFetchInfo.getFetch().middleSourceProperty()));
            } else {
                query.groupBy(query.$(mainFetchInfo.getFetch().target(), mainFetchInfo.getFetch().targetProperty()));
            }
        }

        if (Objects.nonNull(mainFetchInfo.getTargetOrderBy()) && !StringPool.EMPTY.equals(mainFetchInfo.getTargetOrderBy())) {
            query.orderBy(OrderByDirection.NONE, mainFetchInfo.getTargetOrderBy());
        }

        if (baseQuery != null) {
            query.setFetchEnables(fetchEnables);
            query.setFetchFilters(fetchFilters);
            query.log(baseQuery.isEnableLog());
        }

        query.optimizeOptions(OptimizeOptions::disableAll);

        int batchSize = XbatisGlobalConfig.getFetchInBatchSize();
        int size = conditionList.size();
        if (size < batchSize) {
            //无需 分批次查
            return fetchDataWithQuery(mainFetchInfo, query, conditionList, null);
        }
        List newList;
        if (conditionList instanceof List) {
            newList = (List) conditionList;
        } else {
            newList = new ArrayList<>(conditionList);
        }

        List resultList = new ArrayList<>(size);
        for (int i = 0; i < size; i += batchSize) {
            int end = Math.min(i + batchSize, size);
            List subList = newList.subList(i, end);
            List list = fetchDataWithQuery(mainFetchInfo, query, subList, mergeGroupFetchInfos);
            if (!list.isEmpty()) {
                resultList.addAll(list);
            }
        }
        return resultList;
    }

    /**
     * 单Fetch查询，非批量
     *
     * @param rowValue
     * @param fetchInfo
     * @param onValue
     * @param cacheKey
     */
    public void singleFetch(Object rowValue, FetchInfo fetchInfo, Object onValue, String cacheKey) {
        List<Object> list;
        if (Objects.nonNull(onValue)) {
            final List values;
            if (onValue instanceof List) {
                values = (List) onValue;
            } else {
                values = Collections.singletonList(onValue);
            }
            list = singleFetchCache.computeIfAbsent(fetchInfo, key -> new HashMap<>()).computeIfAbsent(onValue, key2 -> {
                return this.fetchData(fetchInfo, values, true, null, null);
            });
        } else {
            list = new ArrayList<>();
        }
        this.setToFetchValue(rowValue, list, fetchInfo, cacheKey);
    }

    protected void setToFetchValue(Object rowValue, List<?> matchValues, FetchInfo fetchInfo, String cacheKey) {
        if (fetchInfo.getFieldInfo().isCollection()) {
            if (Objects.isNull(matchValues) || matchValues.isEmpty()) {
                fetchInfo.setValue(rowValue, matchValues, defaultValueContext);
                return;
            }

            if (matchValues.get(0) instanceof FetchTargetValue) {
                matchValues = ((List<FetchTargetValue>) matchValues)
                        .stream().map(m -> TypeConvertUtil.convert(m.getTarget(), fetchInfo.getFieldInfo().getFinalClass()))
                        .collect(Collectors.toList());
            }

            // 需要处理的情况：1. 有排序器；2. limit > 0 且需要限制大小
            boolean needSort = fetchInfo.getFetch().comparator() != Void.class;
            boolean needLimit = fetchInfo.getFetch().limit() > 0 && matchValues.size() > fetchInfo.getFetch().limit();
            if (needSort || needLimit) {
                Stream<?> stream = matchValues.stream();
                if (needSort) {
                    Comparator comparator = fetchInfo.getComparator();
                    stream = stream.sorted(comparator);
                }
                if (needLimit) {
                    stream = stream.limit(fetchInfo.getFetch().limit());
                }
                matchValues = stream.collect(Collectors.toList());
            }
            this.setCache(fetchInfo, cacheKey, matchValues);
            fetchInfo.setValue(rowValue, matchValues, defaultValueContext);
        } else {
            if (Objects.isNull(matchValues) || matchValues.isEmpty()) {
                this.setCache(fetchInfo, cacheKey, null);
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
            this.setCache(fetchInfo, cacheKey, value);
            fetchInfo.setValue(rowValue, value, defaultValueContext);
        }
    }

    private void setCache(FetchInfo fetchInfo, String cacheKey, Object result) {
        if (cacheKey == null) {
            return;
        }
        if (fetchInfo.getFetch().cacheName().isEmpty()) {
            return;
        }
        FetchCache fetchCache = XbatisGlobalConfig.getFetchCache();
        if (fetchCache != null) {
            fetchCache.set(fetchInfo.getFetch().cacheName(), fetchInfo.getFetch(), fetchInfo.getFieldInfo(), cacheKey, result);
        }
    }

    /**
     * 获取最终的匹配on的value值
     *
     * @param fetchInfo
     * @param matchValue
     * @return
     */
    private Object getFinalFetchOnValue(FetchInfo fetchInfo, Object matchValue) {
        if (matchValue == null) {
            return null;
        }

        if (matchValue instanceof Number) {
            return matchValue;
        }

        if (fetchInfo.getFetch().propertyType() == FetchPropertyType.SIMPLE) {
            return matchValue;
        }
        if ("".equals(matchValue) || "\"\"".equals(matchValue)) {
            return null;
        }
        if (fetchInfo.getFetch().propertyType() == FetchPropertyType.MULTI) {
            List<?> list = Arrays.stream(matchValue.toString().split(","))
                    .filter(i -> !"".equals(i))
                    .map(i -> TypeConvertUtil.convert(i, fetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass()))
                    .collect(Collectors.toList());

            if (list.isEmpty()) {
                return null;
            }
            return list;
        }

        if (fetchInfo.getFetch().propertyType() == FetchPropertyType.ARRAY) {
            String str = matchValue.toString();
            List<?> list = Arrays.stream(str.substring(1, str.length() - 1).split(","))
                    .filter(i -> !"".equals(i) && !"\"\"".equals(i))
                    .map(i -> i.trim().replace("\"", ""))
                    .map(i -> TypeConvertUtil.convert(i, fetchInfo.getTargetTableFieldInfo().getFieldInfo().getFinalClass()))
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                return null;
            }
            return list;
        }

        throw new RuntimeException("未适配");
    }

    @Override
    protected String prependPrefix(String columnName, String prefix) {
        if (prefix == null || prefix.isEmpty() || columnName == null || columnName.isEmpty()) {
            return columnName;
        }
        return prefix + columnName;
    }
}
