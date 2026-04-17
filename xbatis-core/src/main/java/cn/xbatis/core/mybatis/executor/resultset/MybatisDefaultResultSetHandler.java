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
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdCountFromQueryContext;
import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.mybatis.mapper.context.XbatisContextUtil;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.util.WhereUtil;
import cn.xbatis.core.util.*;
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
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MybatisDefaultResultSetHandler extends DefaultResultSetHandler {

    private final Map<FetchInfo, Map<Object, List<Object>>> singleFetchCache = new HashMap<>();
    private final Map<Method, Map> createdEventContextMap = new HashMap<>();
    private final String FETCH_MATCH_COLUMN = "m$v";
    private final List<Object> rowValues = new ArrayList<>();
    private BasicMapper basicMapper;
    private Map<FetchInfo, List<FetchObject>> needFetchValuesMap;
    //Fetch 信息
    private Map<Class, List<FetchInfo>> waitFetchInfosMap;
    private Map<Class, List<FetchInfo>> filteredFetchInfosMap;

    private Map<String, Consumer<Where>> fetchFilters;
    private BaseQuery<?, ?> baseQuery;
    private Map<String, Boolean> fetchEnables;
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
                        this.needFetchValuesMap = new HashMap<>();
                        this.basicMapper = XbatisContextUtil.getBasicMapper(parameterObject);

                        if (this.fetchEnables == null || this.fetchEnables.isEmpty()) {
                            this.waitFetchInfosMap = resultInfo.getFetchInfoMap();
                        } else {
                            this.waitFetchInfosMap = new HashMap<>();
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
                                        List<FetchInfo> fetchInfos = waitFetchInfosMap.get(key);
                                        if (fetchInfos == null) {
                                            fetchInfos = new ArrayList<>();
                                            waitFetchInfosMap.put(key, fetchInfos);
                                        }
                                        fetchInfos.add(item);
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
            List<FetchInfo> filteredFetchInfos = this.filteredFetchInfosMap == null ? null : this.filteredFetchInfosMap.get(returnType);
            rowValue = this.loadFetchValue(resultMap.getType(), fetchInfos, filteredFetchInfos, rowValue, rsw.getResultSet());
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

        List<FetchInfo> fetchInfos = getFetchInfo(resultMap.getType());
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

    private List<FetchInfo> getFetchInfo(Class<?> resultType) {
        if (Objects.isNull(waitFetchInfosMap) || waitFetchInfosMap.isEmpty()) {
            return null;
        }
        return waitFetchInfosMap.get(resultType);
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
            if (fetchInfo.getFetch().propertyType() == FetchPropertyType.SIMPLE) {
                if (Objects.nonNull(fetchInfo.getValueTypeHandler())) {
                    onValue = fetchInfo.getValueTypeHandler().getResult(resultSet, fetchInfo.getValueColumn());
                } else {
                    onValue = resultSet.getObject(fetchInfo.getValueColumn());
                    if (onValue != null && !(onValue instanceof Number)) {
                        onValue = onValue.toString();
                    }
                }
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

    public Object loadFetchValue(Class<?> resultType, List<FetchInfo> fetchInfos, List<FetchInfo> filteredFetchInfos, Object rowValue, ResultSet resultSet) {
        //分组Fetch 进行分组操作
        Map<String, List<FetchInfo>> groupFetchMap = fetchInfos.stream().filter(i -> !i.getFetch().mergeGroup().isEmpty()).collect(Collectors.groupingBy(i -> i.getFetch().mergeGroup()));
        if (!groupFetchMap.isEmpty()) {
            List<FetchInfo> newFetchInfos = new ArrayList<>(fetchInfos);
            Iterator<Map.Entry<String, List<FetchInfo>>> it = groupFetchMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<FetchInfo>> entry = it.next();
                if (entry.getValue().size() == 1) {
                    //只有1个 不走分组处理
                    it.remove();
                } else {
                    //从非分组中移除
                    newFetchInfos.removeAll(entry.getValue());
                }
            }
            fetchInfos = newFetchInfos;
        }


        for (FetchInfo fetchInfo : fetchInfos) {
            Object onValue = this.getFetchOnValue(fetchInfo, resultSet);
            if (Objects.isNull(onValue)) {
                continue;
            }

            if (Objects.isNull(rowValue)) {
                rowValue = configuration.getObjectFactory().create(resultType);
            }

            String cacheKey = null;
            FetchCache fetchCache = XbatisGlobalConfig.getFetchCache();
            if (fetchCache != null && !fetchInfo.getFetch().cacheName().isEmpty()) {
                cacheKey = this.buildFetchCacheKey(onValue, fetchInfo, null);
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
                this.singleFetch(rowValue, fetchInfo, onValue, cacheKey);
            } else {
                List fetchOnValueList = needFetchValuesMap.computeIfAbsent(fetchInfo, key -> new ArrayList<>());
                if (fetchInfo.getFetch().propertyType() != FetchPropertyType.SIMPLE) {
                    onValue = getFinalMatchValue(fetchInfo, onValue);
                    if (onValue == null) {
                        continue;
                    }
                }
                //先放到需要Fetch的列表中
                fetchOnValueList.add(new FetchObject(onValue, rowValue, cacheKey));
            }
        }

        // 分组Fetch 进行缓存获取
        if (!groupFetchMap.isEmpty()) {
            Iterator<Map.Entry<String, List<FetchInfo>>> it = groupFetchMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<FetchInfo>> entry = it.next();
                FetchInfo fetchInfo = entry.getValue().get(0);
                Object onValue;
                try {
                    onValue = this.getFetchOnValue(fetchInfo, resultSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (Objects.isNull(onValue)) {
                    continue;
                }

                if (Objects.isNull(rowValue)) {
                    rowValue = configuration.getObjectFactory().create(resultType);
                }

                String cacheKey = null;
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
                    this.singleConditionMergeFetch(rowValue, entry.getValue(), onValue, cacheKey);
                } else {
                    List fetchObjectList = MapUtil.computeIfAbsent(needFetchValuesMap, fetchInfo, key -> new ArrayList<>());
                    if (fetchInfo.getFetch().propertyType() != FetchPropertyType.SIMPLE) {
                        onValue = getFinalMatchValue(fetchInfo, onValue);
                        if (onValue == null) {
                            continue;
                        }
                    }
                    fetchObjectList.add(new FetchEntityObject(onValue, rowValue, entry.getValue(), cacheKey));
                }
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
        if (Objects.isNull(this.needFetchValuesMap) || this.needFetchValuesMap.isEmpty()) {
            return;
        }
        for (Map.Entry<FetchInfo, List<FetchObject>> entry : needFetchValuesMap.entrySet()) {
            List<FetchObject> fetchObjects = entry.getValue();
            Set values = new HashSet();
            for (FetchObject fetchObject : fetchObjects) {
                Object v = fetchObject.getMatchValue();
                if (v instanceof Collection) {
                    values.addAll((Collection) v);
                } else {
                    values.add(v);
                }
            }

            FetchObject fetchObject = entry.getValue().get(0);
            List<FetchInfo> mergeGroupFetchInfos;
            if (fetchObject instanceof FetchEntityObject) {
                FetchEntityObject fetchEntityObject = (FetchEntityObject) fetchObject;
                mergeGroupFetchInfos = fetchEntityObject.getFetchInfos();
            } else {
                mergeGroupFetchInfos = null;
            }

            List<?> list = this.fetchData(entry.getKey(), (List) values.stream().collect(Collectors.toList()), false, mergeGroupFetchInfos, query -> {
                if (mergeGroupFetchInfos != null) {
                    for (FetchInfo fetchInfo : mergeGroupFetchInfos) {
                        query.select(fetchInfo.getTargetSelect());
                    }
                    query.returnType(mergeGroupFetchInfos.get(0).getTargetTableInfo().getType());
                }
            });
            this.fillFetchData(entry.getKey(), fetchObjects, list);
        }
    }

    private List<Object> fetchData(FetchInfo fetchInfo, Query<?> query, List<Serializable> queryValueList, List<FetchInfo> mergeGroupFetchInfos) {
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

        if (baseQuery != null) {
            query.setFetchEnables(fetchEnables);
            if (query.getFetchFilters() == null) {
                query.setFetchFilters(fetchFilters);
            }
            query.log(baseQuery.isEnableLog());
            if (baseQuery.isEnableLog() && (baseQuery.getLogger() != null && !baseQuery.getLogger().isEmpty())) {
                query.log(baseQuery.getLogger() + ".$" + fetchInfo.getFieldInfo().getField().getName());
            }
            this.appendWhereConditions(query.$where(), fetchInfo, mergeGroupFetchInfos);
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
            values.stream().forEach(i -> {
                setToFetchValue(i.getValue(), null, fetchInfo, i.getCacheKey());
            });
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

        FetchObject firstFetchObject = values.get(0);
        if (firstFetchObject instanceof FetchEntityObject) {
            FetchEntityObject fetchEntityObject = (FetchEntityObject) firstFetchObject;
            values.forEach(item -> {
                List<Object> matchValues;
                if (item.getMatchValue() instanceof Collection) {
                    matchValues = new ArrayList<>();
                    for (Object v : (Collection<?>) item.getMatchValue()) {
                        matchValues.addAll(map.get(v.toString()));
                    }
                } else {
                    matchValues = map.get(item.getMatchValue().toString());
                }

                for (FetchInfo f : fetchEntityObject.getFetchInfos()) {
                    List<Object> vs;
                    if (matchValues != null && !matchValues.isEmpty()) {
                        vs = matchValues.stream().map(i -> {
                            FetchTargetValue fetchTargetValue = (FetchTargetValue) i;
                            return new FetchTargetValue(fetchTargetValue.getMatchFieldValue(), f.getTargetSelectTableFieldInfo().getValue(fetchTargetValue.getTarget()));
                        }).collect(Collectors.toList());
                    } else {
                        vs = new ArrayList<>();
                    }
                    this.setToFetchValue(item.getValue(), vs, f, item.getCacheKey());
                }
            });
        } else {
            values.forEach(item -> {
                List<Object> matchValues;
                if (item.getMatchValue() instanceof Collection) {
                    matchValues = new ArrayList<>();
                    for (Object v : (Collection<?>) item.getMatchValue()) {
                        matchValues.addAll(map.get(v.toString()));
                    }
                } else {
                    matchValues = map.get(item.getMatchValue().toString());
                }
                this.setToFetchValue(item.getValue(), matchValues, fetchInfo, item.getCacheKey());
            });
        }
    }

    protected List<Object> fetchData(FetchInfo mainFetchInfo, List conditionList, boolean single, List<FetchInfo> mergeGroupFetchInfos, Consumer<Query> queryConsumer) {
        if (conditionList.isEmpty()) {
            return new ArrayList();
        }

        int batchSize = XbatisGlobalConfig.getFetchInBatchSize();
        List queryValueList = new ArrayList<>(batchSize);
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

        if (conditionList.size() < batchSize) {
            //无需 分批次查
            return fetchData(mainFetchInfo, query, (List<Serializable>) conditionList, null);
        }

        List<Object> resultList = new ArrayList<>(conditionList.size());
        int size = conditionList.size();
        for (int i = 0; i < size; i++) {
            queryValueList.add(conditionList.get(i));
            if ((i + 1) % batchSize == 0) {
                //达到单次查询
                List<Object> list = fetchData(mainFetchInfo, query, (List<Serializable>) queryValueList, mergeGroupFetchInfos);
                queryValueList.clear();
                if (!list.isEmpty()) {
                    resultList.addAll(list);
                }
            }
        }

        if (queryValueList.isEmpty()) {
            return resultList;
        }

        //还有没查询的 继续查询
        List<Object> list = fetchData(mainFetchInfo, query, (List<Serializable>) queryValueList, mergeGroupFetchInfos);
        queryValueList.clear();
        if (!list.isEmpty()) {
            resultList.addAll(list);
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

    /**
     * 单条件合并Fetch查询
     *
     * @param rowValue
     * @param fetchInfos
     * @param onValue
     * @param cacheKey
     */
    public void singleConditionMergeFetch(Object rowValue, List<FetchInfo> fetchInfos, Object onValue, String cacheKey) {
        FetchInfo fetchInfo = fetchInfos.get(0);
        List<Object> list;
        if (Objects.nonNull(onValue)) {
            final List values;
            if (onValue instanceof List) {
                values = (List) onValue;
            } else {
                values = Collections.singletonList(onValue);
            }
            Class returnType = fetchInfo.getTargetTableInfo().getType();
            list = singleFetchCache.computeIfAbsent(fetchInfo, key -> new HashMap<>()).computeIfAbsent(onValue, key2 -> {
                return this.fetchData(fetchInfo, values, true, fetchInfos, query -> {
                    for (FetchInfo f : fetchInfos) {
                        query.select(f.getTargetSelect());
                    }
                    query.returnType(returnType);
                });
            });
            for (FetchInfo f : fetchInfos) {
                List<Object> fvList = list.stream().map(row -> {
                    return fetchInfo.getTargetTableInfo().getFieldInfo(f.getFetch().targetSelectProperty()).getValue(row);
                }).collect(Collectors.toList());
                this.setToFetchValue(rowValue, fvList, f, cacheKey);
            }
        } else {
            list = new ArrayList<>();
            this.setToFetchValue(rowValue, list, fetchInfo, cacheKey);
        }
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
    private Object getFinalMatchValue(FetchInfo fetchInfo, Object matchValue) {
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
