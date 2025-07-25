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

package cn.xbatis.core.db.reflect;

import cn.xbatis.core.exception.NotTableClassException;
import cn.xbatis.core.exception.NotTableFieldException;
import cn.xbatis.core.util.FieldUtil;
import cn.xbatis.core.util.GenericUtil;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.db.annotations.*;
import lombok.Data;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ResultInfo {

    /**
     * 所有的 FetchInfo 包括内嵌的
     */
    private final Map<Class, List<FetchInfo>> fetchInfoMap;

    /**
     * 所有的 PutValue注解的信息 包括内嵌的
     */
    private final Map<Class, List<PutValueInfo>> putValueInfoMap;

    /**
     * 所有的 PutEnumValue注解的信息 包括内嵌的
     */
    private final Map<Class, List<PutEnumValueInfo>> putEnumValueInfoMap;

    /**
     * 所有的 ResultFieldInfo 不包括内嵌的
     */
    private final List<ResultFieldInfo> resultFieldInfos;

    /**
     * 内嵌信息
     */
    private final List<NestedResultInfo> nestedResultInfos;

    /**
     * 表对应的前缀 包括内嵌的
     */
    private final Map<Class, Map<Integer, String>> tablePrefixes;

    /**
     * 类上的PutValues注解
     */
    private final Map<Class, List<CreatedEventInfo>> createdEventInfos;

    public ResultInfo(Class<?> clazz) {

        ParseResult parseResult = parse(clazz);
        this.fetchInfoMap = Collections.unmodifiableMap(parseResult.fetchInfoMap);
        this.putValueInfoMap = Collections.unmodifiableMap(parseResult.putValueInfoMap);
        this.putEnumValueInfoMap = Collections.unmodifiableMap(parseResult.putEnumValueInfoMap);
        this.resultFieldInfos = Collections.unmodifiableList(parseResult.resultFieldInfos);
        this.tablePrefixes = Collections.unmodifiableMap(parseResult.tablePrefixes);
        this.nestedResultInfos = Collections.unmodifiableList(parseResult.nestedResultInfos);
        this.createdEventInfos = Collections.unmodifiableMap(parseResult.createdEventInfos.stream().collect(Collectors.groupingBy(CreatedEventInfo::getClazz)));
    }

    private static ParseResult parse(Class<?> clazz) {
        ResultEntity resultEntity = clazz.getAnnotation(ResultEntity.class);
        Objects.requireNonNull(resultEntity);
        ParseResult parseResult = new ParseResult();
        parseResultEntity(parseResult, clazz, resultEntity);
        return parseResult;
    }

    private static void parseResultEntity(ParseResult parseResult, Class<?> clazz, ResultEntity resultEntity) {
        if (clazz.isAnnotationPresent(CreatedEvent.class)) {
            parseResult.createdEventInfos.add(new CreatedEventInfo(clazz, clazz.getAnnotation(CreatedEvent.class)));
        }
        TableInfo resultEntityTableInfo = resultEntity.value().isAnnotationPresent(Table.class) ? Tables.get(resultEntity.value()) : null;

        int tableCount = 0;
        if (Objects.nonNull(resultEntityTableInfo)) {
            tableCount = createPrefix(resultEntity.value(), resultEntity.storey(), parseResult.tablePrefixes, 0);
        } else if (resultEntity.value() != Void.TYPE && resultEntity.value() != Void.class) {
            throw new NotTableClassException(resultEntity.value());
        }

        List<Field> fieldList = FieldUtil.getFields(clazz);
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(ResultField.class)) {
                //普通字段
                ResultField resultField = field.getAnnotation(ResultField.class);
                parseResult.resultFieldInfos.add(new ResultFieldInfo(clazz, field, resultField));
                continue;
            }

            if (field.isAnnotationPresent(Fetch.class)) {
                //Fetch
                tableCount = parseFetch(parseResult, resultEntityTableInfo, parseResult.resultFieldInfos, clazz, field, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(PutValue.class)) {
                //PutValue
                tableCount = parsePutValue(parseResult, resultEntityTableInfo, parseResult.resultFieldInfos, clazz, field, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(PutEnumValue.class)) {
                //PutEnumValue
                tableCount = parsePutEnumValue(parseResult, resultEntityTableInfo, parseResult.resultFieldInfos, clazz, field, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(NestedResultEntity.class)) {
                //内嵌类字段
                NestedResultEntity nestedResultEntity = field.getAnnotation(NestedResultEntity.class);
                NestedResultInfo nestedResultInfo = new NestedResultInfo(clazz, field, nestedResultEntity, new ArrayList<>(), new ArrayList<>());

                parseResult.nestedResultInfos.add(nestedResultInfo);

                tableCount = parseNestedResultEntity(parseResult, nestedResultInfo, field, nestedResultEntity, tableCount);
                continue;
            }


            if (Objects.isNull(resultEntityTableInfo)) {
                throw new RuntimeException("the class:" + clazz + "'s @ResultEntity not set correct value");
            }

            TableInfo tableInfo;
            TableFieldInfo tableFieldInfo;
            String tableFieldName;
            Class<?> entity;
            int storey;

            if (field.isAnnotationPresent(ResultEntityField.class)) {
                ResultEntityField resultEntityField = field.getAnnotation(ResultEntityField.class);
                if (resultEntityField.target() != Void.class) {
                    entity = resultEntityField.target();
                    storey = resultEntityField.storey();
                    tableInfo = Tables.get(entity);
                    if (Objects.isNull(tableInfo)) {
                        throw new NotTableClassException(entity);
                    }
                } else {
                    if (resultEntity.storey() != resultEntityField.storey()) {
                        throw new RuntimeException(" class: no config ?, the field:" + field.getName() + " config @ResultEntityField(storey) error");
                    }
                    entity = resultEntity.value();
                    storey = resultEntity.storey();
                    tableInfo = resultEntityTableInfo;
                }
                tableFieldName = resultEntityField.property();
                if (tableFieldName.isEmpty()) {
                    tableFieldName = field.getName();
                }
            } else {
                tableInfo = resultEntityTableInfo;
                entity = resultEntity.value();
                storey = resultEntity.storey();
                tableFieldName = field.getName();
            }

            tableFieldInfo = tableInfo.getFieldInfo(tableFieldName);
            if (Objects.isNull(tableFieldInfo)) {
                throw new NotTableFieldException(entity, tableFieldName);
            }

            if (resultEntity.value() != entity || resultEntity.storey() != storey) {
                tableCount = createPrefix(entity, storey, parseResult.tablePrefixes, tableCount);
            }

            //获取前缀
            String tablePrefix = getTablePrefix(parseResult.tablePrefixes, entity, storey);
            //表字段
            parseResult.resultFieldInfos.add(new ResultTableFieldInfo(clazz, storey, tablePrefix, tableInfo, tableFieldInfo, field));
        }
    }

    /**
     * 解析内嵌字段
     *
     * @param parseResult        解析结果
     * @param nestedResultInfo   内嵌信息
     * @param sourceField        字段
     * @param nestedResultEntity 内嵌注解
     * @param tableCount         当前表个数
     * @return 当前已存在表的个数
     */
    private static int parseNestedResultEntity(ParseResult parseResult, NestedResultInfo nestedResultInfo, Field sourceField, NestedResultEntity nestedResultEntity, int tableCount) {
        //添加前缀
        tableCount = createPrefix(nestedResultEntity.target(), nestedResultEntity.storey(), parseResult.tablePrefixes, tableCount);

        Class targetType = nestedResultInfo.getFieldInfo().getTypeClass();
        //假如是集合类型
        if (Collection.class.isAssignableFrom(targetType)) {
            List<Class<?>> types = GenericUtil.getGeneric(nestedResultInfo.getField().getGenericType());
            if (Objects.nonNull(types) && !types.isEmpty()) {
                targetType = types.get(0);
            }
        }


        //是否隐射的实体类
        boolean fieldTypeIsEntity = targetType.isAnnotationPresent(Table.class);
        if (!fieldTypeIsEntity) {
            if (targetType.isAnnotationPresent(CreatedEvent.class)) {
                parseResult.createdEventInfos.add(new CreatedEventInfo(targetType, (CreatedEvent) targetType.getAnnotation(CreatedEvent.class)));
            }
        }

        TableInfo tableInfo = Tables.get(nestedResultEntity.target());
        if (Objects.isNull(tableInfo)) {
            throw new NotTableClassException(nestedResultEntity.target());
        }

        for (Field field : FieldUtil.getFields(targetType)) {
            if (field.isAnnotationPresent(ResultField.class)) {
                //普通字段
                ResultField resultField = field.getAnnotation(ResultField.class);
                nestedResultInfo.getResultFieldInfos().add(new ResultFieldInfo(targetType, field, resultField));
                continue;
            }

            if (field.isAnnotationPresent(Fetch.class)) {
                //Fetch
                FieldInfo fieldInfo = new FieldInfo(targetType, sourceField);
                Class fetchType = fieldInfo.getFinalClass();

                tableCount = parseFetch(parseResult, tableInfo, nestedResultInfo.getResultFieldInfos(), fetchType, field, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(ResultEntityField.class)) {
                ResultEntityField resultEntityField = field.getAnnotation(ResultEntityField.class);
                if (resultEntityField.target() == Void.class) {
                    throw new RuntimeException(" class:" + field.getDeclaringClass() + ", the field:" + field.getName() + " config @ResultEntityField error");
                }

                Class<?> entity = resultEntityField.target();
                int storey = resultEntityField.storey();

                tableInfo = Tables.get(entity);
                if (Objects.isNull(tableInfo)) {
                    throw new NotTableClassException(entity);
                }

                String tableFieldName = resultEntityField.property();
                if (tableFieldName.isEmpty()) {
                    tableFieldName = field.getName();
                }

                TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(tableFieldName);
                if (Objects.isNull(tableFieldInfo)) {
                    throw new NotTableFieldException(entity, tableFieldName);
                }

                tableCount = createPrefix(entity, storey, parseResult.tablePrefixes, tableCount);

                //获取前缀
                String tablePrefix = getTablePrefix(parseResult.tablePrefixes, entity, storey);

                //表字段
                nestedResultInfo.getResultFieldInfos().add(new ResultTableFieldInfo(targetType, storey, tablePrefix, tableInfo, tableFieldInfo, field));
                continue;
            }


            if (field.isAnnotationPresent(NestedResultEntity.class)) {
                //内嵌类字段
                NestedResultEntity newNestedResultEntity = field.getAnnotation(NestedResultEntity.class);

                NestedResultInfo newNestedResultInfo = new NestedResultInfo(targetType, field, newNestedResultEntity, new ArrayList<>(), new ArrayList<>());
                nestedResultInfo.getNestedResultInfos().add(newNestedResultInfo);

                tableCount = parseNestedResultEntity(parseResult, newNestedResultInfo, field, newNestedResultEntity, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(PutValue.class)) {
                //PutValue
                tableCount = parsePutValue(parseResult, tableInfo, parseResult.resultFieldInfos, targetType, field, tableCount);
                continue;
            }

            if (field.isAnnotationPresent(PutEnumValue.class)) {
                //PutEnumValue
                tableCount = parsePutEnumValue(parseResult, tableInfo, parseResult.resultFieldInfos, targetType, field, tableCount);
                continue;
            }

            String targetFieldName = field.getName();
            NestedResultEntityField nestedResultEntityField = field.getAnnotation(NestedResultEntityField.class);
            if (Objects.nonNull(nestedResultEntityField)) {
                targetFieldName = nestedResultEntityField.value();
            }

            TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(targetFieldName);
            if (Objects.isNull(nestedResultEntityField) && fieldTypeIsEntity) {
                if (!tableFieldInfo.getTableFieldAnnotation().select()) {
                    continue;
                }
            }
            if (Objects.isNull(tableFieldInfo)) {
                throw new NotTableFieldException(nestedResultEntity.target(), field.getName());
            }

            //获取前缀
            String tablePrefix = getTablePrefix(parseResult.tablePrefixes, nestedResultEntity.target(), nestedResultEntity.storey());

            //表字段
            nestedResultInfo.getResultFieldInfos().add(new ResultTableFieldInfo(targetType, nestedResultEntity.storey(), tablePrefix, tableInfo, tableFieldInfo, field));
        }

        return tableCount;
    }

    private static RuntimeException buildException(Class clazz, Field field, String annotationName, String annotationPropertyName, String message) {
        return new RuntimeException(clazz.getName() + "." + field.getName() + " " + annotationName + "  config error,the " + annotationPropertyName + ":" + message);
    }


    /**
     * 解析内嵌字段
     *
     * @param parseResult      解析结果
     * @param currentTableInfo 当前对应TableInfo
     * @param field            字段
     * @param tableCount       当前表个数
     * @return 当前已存在表的个数
     */
    private static int parseFetch(ParseResult parseResult, TableInfo currentTableInfo, List<ResultFieldInfo> resultFieldInfos, Class<?> clazz, Field field, int tableCount) {
        Fetch fetch = field.getAnnotation(Fetch.class);

        String valueColumn = fetch.column();
        TypeHandler<?> valueTypeHandler = null;
        if (StringPool.EMPTY.equals(valueColumn)) {
            TableFieldInfo fetchFieldInfo;
            TableInfo fetchTableInfo;
            if (fetch.source() != Void.class) {
                if (!fetch.source().isAnnotationPresent(Table.class)) {
                    throw new RuntimeException(clazz.getName() + "->" + field.getName() + " fetch config error,the source: " + fetch.source().getName() + " is not a entity");
                }
                fetchTableInfo = Tables.get(fetch.source());
                fetchFieldInfo = fetchTableInfo.getFieldInfo(fetch.property());
            } else {
                //如果没有设置Fetch source 字段，则当前作用域的TableInfo
                fetchTableInfo = currentTableInfo;
                fetchFieldInfo = fetchTableInfo.getFieldInfo(fetch.property());
            }

            if (Objects.isNull(fetchFieldInfo)) {
                throw new RuntimeException(clazz.getName() + "->" + field.getName() + " fetch config error,the property: " + fetch.property() + " is not a entity field");
            }
            valueTypeHandler = fetchFieldInfo.getTypeHandler();
            //以字段为基础的查询
            //创建前缀
            tableCount = createPrefix(fetchTableInfo.getType(), fetch.storey(), parseResult.tablePrefixes, tableCount);
            //获取前缀
            String tablePrefix = getTablePrefix(parseResult.tablePrefixes, fetchTableInfo.getType(), fetch.storey());

            resultFieldInfos.add(new ResultTableFieldInfo(false, clazz, fetch.storey(), tablePrefix, fetchTableInfo, fetchFieldInfo, field));
            valueColumn = tablePrefix + fetchFieldInfo.getColumnName();
        }

        FieldInfo fieldInfo = new FieldInfo(clazz, field);

        Class returnType = fieldInfo.getFinalClass();

        if (!returnType.isAnnotationPresent(Table.class)) {
            if (returnType.isAnnotationPresent(CreatedEvent.class)) {
                parseResult.createdEventInfos.add(new CreatedEventInfo(returnType, (CreatedEvent) returnType.getAnnotation(CreatedEvent.class)));
            }
        }

        parseResult.fetchInfoMap.computeIfAbsent(clazz, key -> new ArrayList<>()).add(new FetchInfo(clazz, fieldInfo, fetch, returnType, valueColumn, valueTypeHandler));
        return tableCount;
    }


    /**
     * 解析内嵌字段
     *
     * @param parseResult      解析结果
     * @param currentTableInfo 当前对应TableInfo
     * @param field            字段
     * @param tableCount       当前表个数
     * @return 当前已存在表的个数
     */
    private static int parsePutValue(ParseResult parseResult, TableInfo currentTableInfo, List<ResultFieldInfo> resultFieldInfos, Class clazz, Field field, int tableCount) {
        PutValue putValue = field.getAnnotation(PutValue.class);

        TableInfo fetchTableInfo;
        if (putValue.source() == Void.class) {
            fetchTableInfo = currentTableInfo;
        } else {
            if (!putValue.source().isAnnotationPresent(Table.class)) {
                throw new RuntimeException(clazz.getName() + "->" + field.getName() + " @PutValue config error,the source: " + putValue.source().getName() + " is not a entity");
            }
            fetchTableInfo = Tables.get(putValue.source());
        }

        String[] properties = putValue.property().split(",");
        String[] valuesColumn = new String[properties.length];
        Class<?>[] valueTypes = new Class[properties.length];
        TypeHandler<?>[] valuesTypeHandler = new TypeHandler[properties.length];
        for (int i = 0; i < properties.length; i++) {

            TableFieldInfo fetchFieldInfo = fetchTableInfo.getFieldInfo(putValue.property());

            if (Objects.isNull(fetchFieldInfo)) {
                throw new RuntimeException(clazz.getName() + "->" + field.getName() + " fetch config error,the property: " + putValue.property() + " is not a entity field");
            }
            //创建前缀
            tableCount = createPrefix(fetchTableInfo.getType(), putValue.storey(), parseResult.tablePrefixes, tableCount);
            //获取前缀
            String tablePrefix = getTablePrefix(parseResult.tablePrefixes, fetchTableInfo.getType(), putValue.storey());

            resultFieldInfos.add(new ResultTableFieldInfo(false, clazz, putValue.storey(), tablePrefix, fetchTableInfo, fetchFieldInfo, field));

            valuesColumn[i] = tablePrefix + fetchFieldInfo.getColumnName();
            valuesTypeHandler[i] = fetchFieldInfo.getTypeHandler();
            valueTypes[i] = fetchFieldInfo.getFieldInfo().getTypeClass();
        }

        parseResult.putValueInfoMap.computeIfAbsent(clazz, key -> new ArrayList<>()).add(new PutValueInfo(clazz, field, putValue, valueTypes, valuesColumn, valuesTypeHandler));
        return tableCount;
    }

    /**
     * 解析内嵌字段
     *
     * @param parseResult      解析结果
     * @param currentTableInfo 当前对应TableInfo
     * @param field            字段
     * @param tableCount       当前表个数
     * @return 当前已存在表的个数
     */
    private static int parsePutEnumValue(ParseResult parseResult, TableInfo currentTableInfo, List<ResultFieldInfo> resultFieldInfos, Class clazz, Field field, int tableCount) {
        PutEnumValue putEnumValue = field.getAnnotation(PutEnumValue.class);

        TableInfo fetchTableInfo;
        if (putEnumValue.source() == Void.class) {
            fetchTableInfo = currentTableInfo;
        } else {
            if (!putEnumValue.source().isAnnotationPresent(Table.class)) {
                throw new RuntimeException(clazz.getName() + "->" + field.getName() + " @PutEnumValue config error,the source: " + putEnumValue.source().getName() + " is not a entity");
            }
            fetchTableInfo = Tables.get(putEnumValue.source());
        }

        TableFieldInfo fetchFieldInfo = fetchTableInfo.getFieldInfo(putEnumValue.property());

        if (Objects.isNull(fetchFieldInfo)) {
            throw new RuntimeException(clazz.getName() + "->" + field.getName() + " fetch config error,the property: " + putEnumValue.property() + " is not a entity field");
        }
        //创建前缀
        tableCount = createPrefix(fetchTableInfo.getType(), putEnumValue.storey(), parseResult.tablePrefixes, tableCount);
        //获取前缀
        String tablePrefix = getTablePrefix(parseResult.tablePrefixes, fetchTableInfo.getType(), putEnumValue.storey());

        resultFieldInfos.add(new ResultTableFieldInfo(false, clazz, putEnumValue.storey(), tablePrefix, fetchTableInfo, fetchFieldInfo, field));

        String valueColumn = tablePrefix + fetchFieldInfo.getColumnName();
        TypeHandler<?> valueTypeHandler = fetchFieldInfo.getTypeHandler();


        parseResult.putEnumValueInfoMap.computeIfAbsent(clazz, key -> new ArrayList<>()).add(new PutEnumValueInfo(field, putEnumValue, fetchFieldInfo.getFieldInfo().getTypeClass(), valueColumn, valueTypeHandler));
        return tableCount;
    }

    /**
     * 返回 新的index
     *
     * @param entity          实体类
     * @param storey          存储层级
     * @param entityPrefixMap 实体类前缀 map
     * @param tableCount      当前表个数
     * @return
     */
    private static int createPrefix(Class entity, int storey, Map<Class, Map<Integer, String>> entityPrefixMap, int tableCount) {
        if (Objects.nonNull(entity) && entity != Void.class) {
            tableCount++;
            String prefix;
            if (tableCount == 1) {
                prefix = "";
            } else {
                prefix = "x" + tableCount + "$";
            }
            if (!addPrefix(entityPrefixMap, entity, storey, prefix)) {
                tableCount--;
            }
        }

        return tableCount;
    }

    private static boolean addPrefix(Map<Class, Map<Integer, String>> entityPrefixMap, Class<?> entityType, int storey, String prefix) {
        Map<Integer, String> prefixMap = entityPrefixMap.computeIfAbsent(entityType, key -> new HashMap<>());
        if (prefixMap.containsKey(storey)) {
            return false;
        }
        prefixMap.put(storey, prefix);
        return true;
    }

    /**
     * 获取表的前缀
     *
     * @param entityPrefixMap
     * @param entity          实体类的类型
     * @param storey          实体类存储层级
     * @return 前缀
     */
    private static String getTablePrefix(Map<Class, Map<Integer, String>> entityPrefixMap, Class<?> entity, int storey) {
        String prefix = entityPrefixMap.get(entity).get(storey);
        Objects.requireNonNull(prefix);
        return prefix;
    }

    public List<ResultFieldInfo> getResultFieldInfos() {
        return resultFieldInfos;
    }

    public List<NestedResultInfo> getNestedResultInfos() {
        return nestedResultInfos;
    }

    public Map<Class, List<FetchInfo>> getFetchInfoMap() {
        return fetchInfoMap;
    }

    public Map<Class, Map<Integer, String>> getTablePrefixes() {
        return tablePrefixes;
    }

    static class ParseResult {

        public final Map<Class, List<FetchInfo>> fetchInfoMap = new HashMap<>();

        public final List<ResultFieldInfo> resultFieldInfos = new ArrayList<>();

        public final List<NestedResultInfo> nestedResultInfos = new ArrayList<>();

        public final Map<Class, Map<Integer, String>> tablePrefixes = new HashMap<>();

        public final Map<Class, List<PutValueInfo>> putValueInfoMap = new HashMap<>();

        public final Map<Class, List<PutEnumValueInfo>> putEnumValueInfoMap = new HashMap<>();

        public final List<CreatedEventInfo> createdEventInfos = new ArrayList<>();
    }
}
