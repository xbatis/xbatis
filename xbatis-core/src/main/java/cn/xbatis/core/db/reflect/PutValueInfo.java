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

import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.PutValue;
import lombok.Getter;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

@Getter
public class PutValueInfo {

    private final Field field;

    private final FieldInfo fieldInfo;

    private final PutValue annotation;

    private final Class<?>[] valueTypes;

    private final String[] valuesColumn;

    private final TypeHandler<?>[] valuesTypeHandler;

    private final SetFieldInvoker writeFieldInvoker;

    private final Object defaultValue;

    public PutValueInfo(Class clazz, Field field, PutValue annotation, Class<?>[] valueTypes, String[] valuesColumn, TypeHandler<?>[] valuesTypeHandler) {
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.annotation = annotation;
        this.valueTypes = valueTypes;
        this.valuesColumn = valuesColumn;
        this.valuesTypeHandler = valuesTypeHandler;
        this.writeFieldInvoker = new SetFieldInvoker(field);

        if (!annotation.defaultValue().isEmpty()) {
            this.defaultValue = TypeConvertUtil.convert(annotation.defaultValue(), this.fieldInfo.getTypeClass());
        } else {
            this.defaultValue = null;
        }
    }
}
