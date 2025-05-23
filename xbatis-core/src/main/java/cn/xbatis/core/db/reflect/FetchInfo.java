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
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.Fetch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Data
@EqualsAndHashCode
public class FetchInfo {

    private final Field field;

    private final FieldInfo fieldInfo;

    private final Fetch fetch;

    private final String valueColumn;

    private final TypeHandler<?> valueTypeHandler;

    private final String targetMatchColumn;

    private final String targetSelectColumn;

    private final String orderBy;

    private final String groupBy;

    private final String otherConditions;

    private final GetFieldInvoker eqGetFieldInvoker;

    private final SetFieldInvoker writeFieldInvoker;

    private final boolean multiple;

    private final Class<?> returnType;

    private final boolean isUseIn;

    private final boolean isUseResultFetchKeyValue;

    private final Object nullFillValue;

    public FetchInfo(Class clazz, Field field, Fetch fetch, Class returnType, String valueColumn, TypeHandler<?> valueTypeHandler, Field targetMatchField, String targetMatchColumn, String targetSelectColumn, String orderBy, String groupBy, String otherConditions) {
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.fetch = fetch;
        this.writeFieldInvoker = new SetFieldInvoker(field);
        this.valueColumn = valueColumn;
        this.valueTypeHandler = valueTypeHandler;
        this.eqGetFieldInvoker = Objects.isNull(targetMatchField) ? null : new GetFieldInvoker(targetMatchField);
        this.targetMatchColumn = targetMatchColumn;
        this.targetSelectColumn = targetSelectColumn;
        this.multiple = Collection.class.isAssignableFrom(this.fieldInfo.getTypeClass());
        this.returnType = returnType;
        this.orderBy = orderBy;
        this.groupBy = groupBy;
        this.otherConditions = otherConditions;

        boolean isUseIn = true;

        if (fetch.limit() >= 1) {
            isUseIn = false;
        } else if (!fetch.forceUseIn() && Objects.isNull(this.eqGetFieldInvoker) && this.targetSelectColumn != null && this.targetSelectColumn.contains("(")) {
            isUseIn = false;
        }

        this.isUseIn = isUseIn;
        this.isUseResultFetchKeyValue = this.isUseIn && Objects.isNull(this.eqGetFieldInvoker) && this.returnType.getPackage().getName().contains("java.lang");
        if (fetch.nullFillValue().isEmpty() || fetch.nullFillValue().contains("{")) {
            nullFillValue = null;
        } else {
            nullFillValue = TypeConvertUtil.convert(fetch.nullFillValue(), this.fieldInfo.getTypeClass());
        }
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
