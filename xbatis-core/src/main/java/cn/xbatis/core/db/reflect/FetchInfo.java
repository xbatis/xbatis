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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.ResultEntity;
import cn.xbatis.db.annotations.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@EqualsAndHashCode
public class FetchInfo {

    private final FieldInfo fieldInfo;

    private final Fetch fetch;

    private final String valueColumn;

    private final TypeHandler<?> valueTypeHandler;

    private final TableInfo middleTableInfo;

    private final TableFieldInfo middleSourceTableFieldInfo;

    private final TableFieldInfo middleTargetTableFieldInfo;

    private final TableInfo targetTableInfo;

    private final TableFieldInfo targetTableFieldInfo;

    private final String targetSelect;

    private final String targetOrderBy;

    private final String otherConditions;

    private final SetFieldInvoker writeFieldInvoker;

    private final boolean multiple;

    private final Class<?> returnType;

    private final boolean singleFetch;

    private final boolean group;

    private final boolean sourceTargetMatchFieldInReturnType;

    private final Field sourceTargetMatchField;

    private final GetFieldInvoker sourceTargetMatchFieldGetter;

    private final Object nullFillValue;

    public FetchInfo(Class clazz, FieldInfo fieldInfo, Fetch fetch, Class returnType, String valueColumn, TypeHandler<?> valueTypeHandler) {

        this.fieldInfo = fieldInfo;
        this.fetch = fetch;
        this.writeFieldInvoker = new SetFieldInvoker(fieldInfo.getField());
        this.valueColumn = valueColumn;
        this.valueTypeHandler = valueTypeHandler;

        Object[] objs;
        objs = parseMiddle(clazz, fieldInfo.getField(), fetch);
        this.middleTableInfo = (TableInfo) objs[0];
        this.middleSourceTableFieldInfo = (TableFieldInfo) objs[1];
        this.middleTargetTableFieldInfo = (TableFieldInfo) objs[2];

        objs = parseTarget(clazz, fieldInfo.getField(), fetch);
        this.targetTableInfo = (TableInfo) objs[0];
        this.targetTableFieldInfo = (TableFieldInfo) objs[1];

        this.targetSelect = parseDynamicColumn(clazz, fieldInfo.getField(), middleTableInfo, targetTableInfo, "@Fetch", "targetSelectProperty", fetch.targetSelectProperty());

        this.targetOrderBy = parseOrderByColumn(clazz, fieldInfo.getField(), middleTableInfo, targetTableInfo, "@Fetch", "orderBy", fetch.orderBy());

        String otherConditions = parseDynamicColumn(clazz, fieldInfo.getField(), middleTableInfo, targetTableInfo, "@Fetch", "otherConditions", fetch.otherConditions());

        this.multiple = Collection.class.isAssignableFrom(this.fieldInfo.getTypeClass());
        this.returnType = returnType;

        this.otherConditions = otherConditions;
        this.singleFetch = fetch.limit() >= 1;

        //如果自定义了select 且 里面有函数（根据括号判断）
        this.group = this.targetSelect != null && this.targetSelect.contains("(");

        //检测 source 在不在 target 返回类里
        objs = parseSourceTargetMatchField(fetch, clazz, returnType, fieldInfo, targetTableInfo, targetTableFieldInfo);
        this.sourceTargetMatchFieldInReturnType = (Boolean) objs[0];
        this.sourceTargetMatchField = (Field) objs[1];
        this.sourceTargetMatchFieldGetter = this.sourceTargetMatchField != null ? new GetFieldInvoker(this.sourceTargetMatchField) : null;


        if (fetch.nullFillValue().isEmpty() || fetch.nullFillValue().contains("{")) {
            nullFillValue = null;
        } else {
            nullFillValue = TypeConvertUtil.convert(fetch.nullFillValue(), this.fieldInfo.getTypeClass());
        }
    }

    private static RuntimeException buildException(Class clazz, Field field, String annotationName, String annotationPropertyName, String message) {
        return new RuntimeException(clazz.getName() + "." + field.getName() + " " + annotationName + "  config error,the " + annotationPropertyName + ":" + message);
    }

    /**
     * 解析中间表
     *
     * @param clazz
     * @param field
     * @param fetch
     * @return
     */
    private static Object[] parseMiddle(Class clazz, Field field, Fetch fetch) {
        if (fetch.middle() != Void.class) {
            if (StringPool.EMPTY.equals(fetch.middleSourceProperty())) {
                throw buildException(clazz, field, "@Fetch", "middleSourceProperty", "can't be empty");
            }

            if (StringPool.EMPTY.equals(fetch.middleTargetProperty())) {
                throw buildException(clazz, field, "@Fetch", "middleTargetProperty", "can't be empty");
            }
        }

        if (!StringPool.EMPTY.equals(fetch.middleSourceProperty()) || !StringPool.EMPTY.equals(fetch.middleTargetProperty())) {
            if (fetch.middle() == Void.class) {
                throw buildException(clazz, field, "@Fetch", "middle", "need set");
            }
        }

        if (fetch.middle() != Void.class && !fetch.middle().isAnnotationPresent(Table.class)) {
            throw buildException(clazz, field, "@Fetch", "middle", fetch.middle().getName() + " is not a entity");
        }

        TableInfo middleTableInfo = null;
        TableFieldInfo middleSourceTableFieldInfo = null;
        TableFieldInfo middleTargetTableFieldInfo = null;

        if (fetch.middle() != Void.class) {
            middleTableInfo = Tables.get(fetch.middle());
            middleSourceTableFieldInfo = middleTableInfo.getFieldInfo(fetch.middleSourceProperty());
            if (Objects.isNull(middleSourceTableFieldInfo)) {
                throw buildException(clazz, field, "@Fetch", "middleSourceProperty", fetch.middleSourceProperty() + " is not a entity field");
            }

            middleTargetTableFieldInfo = middleTableInfo.getFieldInfo(fetch.middleTargetProperty());
            if (Objects.isNull(middleTargetTableFieldInfo)) {
                throw buildException(clazz, field, "@Fetch", "middleTargetProperty", fetch.middleTargetProperty() + " is not a entity field");
            }
        }
        return new Object[]{middleTableInfo, middleSourceTableFieldInfo, middleTargetTableFieldInfo};
    }

    private static Object[] parseTarget(Class clazz, Field field, Fetch fetch) {
        if (StringPool.EMPTY.equals(fetch.targetProperty())) {
            throw buildException(clazz, field, "@Fetch", "targetProperty", "can't be empty");
        }

        if (!fetch.target().isAnnotationPresent(Table.class)) {
            throw buildException(clazz, field, "@Fetch", "target", fetch.target().getName() + " is not a entity");
        }

        TableInfo targetTableInfo = Tables.get(fetch.target());
        TableFieldInfo targetTableFieldInfo = targetTableInfo.getFieldInfo(fetch.targetProperty());

        if (Objects.isNull(targetTableFieldInfo)) {
            throw buildException(clazz, field, "@Fetch", "targetProperty", fetch.targetProperty() + " is not a entity field");
        }
        return new Object[]{targetTableInfo, targetTableFieldInfo};
    }

    private static Object[] parseSourceTargetMatchField(Fetch fetch, Class clazz, Class returnType, FieldInfo fieldInfo, TableInfo targetTableInfo, TableFieldInfo targetTableFieldInfo) {
        boolean sourceTargetMatchFieldInReturnType = false;
        Field sourceTargetMatchField = null;
        if (fetch.middle() != Void.class) {
            //有中间表 肯定不在
            return new Object[]{false, null};
        }

        if (returnType.isAnnotationPresent(ResultEntity.class)) {
            ResultInfo resultInfo = ResultInfos.get(returnType);
            Optional<Field> eqFieldOptional = Optional.empty();
            for (ResultFieldInfo item : resultInfo.getResultFieldInfos()) {
                if (item instanceof ResultTableFieldInfo) {
                    ResultTableFieldInfo resultTableFieldInfo = (ResultTableFieldInfo) item;
                    if (!resultTableFieldInfo.getField().isAnnotationPresent(Fetch.class) && resultTableFieldInfo.getTableFieldInfo().getField() == targetTableFieldInfo.getField()) {
                        Field itemField = item.getField();
                        eqFieldOptional = Optional.of(itemField);
                        break;
                    }
                }
            }
            if (eqFieldOptional.isPresent()) {
                sourceTargetMatchField = eqFieldOptional.get();
            }
        } else if (returnType.isAnnotationPresent(Table.class)) {
            if (returnType != targetTableInfo.getType()) {
                throw new RuntimeException(clazz.getName() + "->" + fieldInfo.getField().getName() + " fetch config error,the type can't be" + returnType.getName());
            }
            sourceTargetMatchField = targetTableFieldInfo.getField();
        }
        sourceTargetMatchFieldInReturnType = sourceTargetMatchField != null;

        return new Object[]{sourceTargetMatchFieldInReturnType, sourceTargetMatchField};
    }

    private static String parseOrderByColumn(Class clazz, Field field, TableInfo middleTableInfo, TableInfo targetTableInfo, String annotationName, String annotationPropertyName, String annotationValue) {
        String value = annotationValue.trim();
        if (value.isEmpty()) {
            return null;
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            return parseDynamicColumn(clazz, field, middleTableInfo, targetTableInfo, annotationName, annotationPropertyName, value);
        }

        StringBuilder orderByJoin = new StringBuilder();
        String[] strs = value.split(",");
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            String[] ss = str.trim().split(" ");
            if (ss.length > 2) {
                throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
            }

            String[] arr = ss[0].split("\\.");
            String property = arr[arr.length - 1];
            if (StringPool.EMPTY.equals(property)) {
                throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
            }

            TableInfo tableInfo;
            String tableAliasName;
            if (arr.length == 2) {
                if (!arr[0].equals("middle") && !arr[0].equals("target")) {
                    throw buildException(clazz, field, annotationName, annotationPropertyName, "format error, table alias just can be middle or target");
                }
                tableInfo = arr[0].equals("middle") ? middleTableInfo : targetTableInfo;
                tableAliasName = arr[0];
            } else {
                tableInfo = targetTableInfo;
                tableAliasName = middleTableInfo != null ? "target" : "t";
            }

            TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(property);
            if (Objects.isNull(tableFieldInfo)) {
                throw buildException(clazz, field, annotationName, annotationPropertyName, " the field:" + property + " is not entity field");
            }
            if (i != 0) {
                orderByJoin.append(",");
            }
            orderByJoin.append(tableAliasName).append(".");
            orderByJoin.append(tableFieldInfo.getColumnName()).append(" ").append(ss[1]);
        }
        return orderByJoin.toString();
    }

    private static String parseDynamicColumn(Class clazz, Field field, TableInfo middleTableInfo, TableInfo targetTableInfo, String annotationName, String annotationPropertyName, String annotationValue) {
        String value = annotationValue.trim();
        if (value.isEmpty()) {
            return null;
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            StringBuilder builder = new StringBuilder();
            int startIndex = 1;
            while (true) {
                int start = value.indexOf("{", startIndex);
                if (start == -1) {
                    if (builder.length() == 0 && value.length() <= 2) {
                        throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
                    } else {
                        builder.append(value, startIndex, value.length() - 1);
                        return builder.toString();
                    }
                }
                int end = value.indexOf("}", start);
                if (end == -1) {
                    throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
                }
                String property = value.substring(start + 1, end);

                String[] arr = property.split("\\.");
                if (arr.length > 2) {
                    throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
                }
                TableInfo tableInfo;
                String tableAliasName;
                if (arr.length == 2) {
                    if (!arr[0].equals("middle") && !arr[0].equals("target")) {
                        throw buildException(clazz, field, annotationName, annotationPropertyName, "format error, table alias just can be middle or target");
                    }
                    tableInfo = arr[0].equals("middle") ? middleTableInfo : targetTableInfo;
                    tableAliasName = arr[0];
                } else {
                    tableInfo = targetTableInfo;
                    tableAliasName = middleTableInfo != null ? "target" : "t";
                }

                property = arr[arr.length - 1];
                TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(property);
                if (Objects.isNull(tableFieldInfo)) {
                    throw buildException(clazz, field, annotationName, annotationPropertyName, property + " is not a entity field");
                }
                builder.append(value, startIndex, start);
                builder.append(tableAliasName).append(".");
                builder.append(tableFieldInfo.getColumnName());
                startIndex = end + 1;
            }
        }

        StringBuilder columns = new StringBuilder();
        String[] strs = value.split(",");
        for (int i = 0; i < strs.length; i++) {
            String property = strs[i].trim();
            if (StringPool.EMPTY.equals(property)) {
                throw buildException(clazz, field, annotationName, annotationPropertyName, "format error");
            }
            String[] arr = property.split("\\.");
            property = arr[arr.length - 1];

            TableInfo tableInfo;
            String tableAliasName;
            if (arr.length == 2) {
                if (!arr[0].equals("middle") && !arr[0].equals("target")) {
                    throw buildException(clazz, field, annotationName, annotationPropertyName, "format error, table alias just can be middle or target");
                }
                tableInfo = arr[0].equals("middle") ? middleTableInfo : targetTableInfo;
                tableAliasName = arr[0];
            } else {
                tableInfo = targetTableInfo;
                tableAliasName = middleTableInfo != null ? "target" : "t";
            }

            TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(property);
            if (Objects.isNull(tableFieldInfo)) {
                throw buildException(clazz, field, annotationName, annotationPropertyName, " the field:" + property + " is not entity field");
            }
            if (i != 0) {
                columns.append(",");
            }
            columns.append(tableAliasName).append(".");
            columns.append(tableFieldInfo.getColumnName());
        }
        return columns.toString();
    }

    public void setValue(Object object, Object value, Map<String, Object> defaultValueContext) {
        if (value == null) {
            if (this.fetch.nullFillValue().isEmpty()) {
                return;
            } else if (this.nullFillValue != null) {
                value = this.nullFillValue;
            } else {
                value = XbatisGlobalConfig.getDefaultValue(this.getFieldInfo().getClazz(), this.getFieldInfo().getTypeClass(), this.fetch.nullFillValue(), defaultValueContext);
            }
        }
        try {
            writeFieldInvoker.invoke(object, new Object[]{value});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
