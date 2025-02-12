/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.db.reflect;

import cn.mybatis.mp.core.util.TypeConvertUtil;
import cn.mybatis.mp.db.annotations.PutEnumValue;
import lombok.Getter;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

@Getter
public class PutEnumValueInfo {

    private final Field field;

    private final String valueColumn;

    private final TypeHandler<?> valueTypeHandler;

    private final Class<?> valueType;

    private final SetFieldInvoker writeFieldInvoker;

    private final PutEnumValue annotation;

    private final Object defaultValue;

    public PutEnumValueInfo(Field field, PutEnumValue annotation, Class<?> valueType, String valueColumn, TypeHandler<?> valueTypeHandler) {
        this.field = field;
        this.valueType = valueType;
        this.valueColumn = valueColumn;
        this.valueTypeHandler = valueTypeHandler;
        this.writeFieldInvoker = new SetFieldInvoker(field);
        this.annotation = annotation;
        if (!annotation.defaultValue().isEmpty()) {
            this.defaultValue = TypeConvertUtil.convert(annotation.defaultValue(), field.getType());
        } else {
            this.defaultValue = null;
        }
    }
}
