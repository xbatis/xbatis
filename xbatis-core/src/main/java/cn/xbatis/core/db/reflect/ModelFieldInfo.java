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

import cn.xbatis.core.exception.NotTableFieldException;
import cn.xbatis.db.annotations.ModelEntityField;
import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;

import java.lang.reflect.Field;
import java.util.Objects;

public class ModelFieldInfo {

    /**
     * 表字段信息
     */
    private final TableFieldInfo tableFieldInfo;

    private final Field field;

    private final FieldInfo fieldInfo;

    private final boolean forceUpdate;

    /**
     * 字段读取反射方法
     */
    private final GetFieldInvoker readFieldInvoker;


    private final SetFieldInvoker writeFieldInvoker;

    public ModelFieldInfo(Class entity, Class model, Field field) {
        TableInfo tableInfo = Tables.get(entity);
        String entityFieldName = field.getName();
        if (field.isAnnotationPresent(ModelEntityField.class)) {
            ModelEntityField modelEntityField = field.getAnnotation(ModelEntityField.class);
            entityFieldName = modelEntityField.value();
            this.forceUpdate = modelEntityField.forceUpdate();
        } else {
            this.forceUpdate = false;
        }
        this.tableFieldInfo = tableInfo.getFieldInfo(entityFieldName);
        if (Objects.isNull(this.tableFieldInfo)) {
            throw new NotTableFieldException(model, "", entity, entityFieldName);
        }
        if (!this.tableFieldInfo.isExists()) {
            throw new NotTableFieldException(model, "", entity, entityFieldName);
        }
        this.field = field;
        this.fieldInfo = new FieldInfo(model, field);
        this.readFieldInvoker = new GetFieldInvoker(field);
        this.writeFieldInvoker = new SetFieldInvoker(field);
    }

    public Object getValue(Object object) {
        try {
            return this.readFieldInvoker.invoke(object, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TableFieldInfo getTableFieldInfo() {
        return tableFieldInfo;
    }

    public Field getField() {
        return field;
    }

    public GetFieldInvoker getReadFieldInvoker() {
        return readFieldInvoker;
    }

    public SetFieldInvoker getWriteFieldInvoker() {
        return writeFieldInvoker;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }
}
