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

import cn.xbatis.core.mybatis.typeHandler.MybatisTypeHandlerUtil;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.*;
import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

public class TableFieldInfo {

    /**
     * 字段
     */
    private final Field field;

    /**
     * 字段类型信息
     */
    private final FieldInfo fieldInfo;

    /**
     * 是否存在表中
     */
    private final boolean exists;

    /**
     * 列名
     */
    private final String columnName;


    /**
     * 字段读取反射方法
     */
    private final GetFieldInvoker readFieldInvoker;

    private final Table tableAnnotation;

    /**
     * TableField 注解信息
     */
    private final TableField tableFieldAnnotation;

    private final boolean tableId;

    private final boolean version;

    private final boolean tenantId;

    private final boolean logicDelete;

    private final boolean logicDeleteTime;

    /**
     * 逻辑删除注解
     */
    private final LogicDelete logicDeleteAnnotation;

    private final Object logicDeleteInitValue;

    private final SetFieldInvoker writeFieldInvoker;

    private final TypeHandler<?> typeHandler;

    private final boolean isTableSplitKey;

    private final boolean canUpdateField;

    public TableFieldInfo(Class clazz, Table tableAnnotation, Field field) {
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.tableAnnotation = tableAnnotation;
        this.tableFieldAnnotation = TableInfoUtil.getTableFieldAnnotation(field);
        this.exists = tableFieldAnnotation.exists();
        this.columnName = TableInfoUtil.getFieldColumnName(tableAnnotation, field);
        this.readFieldInvoker = new GetFieldInvoker(field);
        this.tableId = field.isAnnotationPresent(TableId.class) || field.isAnnotationPresent(TableId.List.class);
        this.version = field.isAnnotationPresent(Version.class);
        this.tenantId = field.isAnnotationPresent(TenantId.class);
        this.logicDelete = field.isAnnotationPresent(LogicDelete.class);
        this.logicDeleteTime = field.isAnnotationPresent(LogicDeleteTime.class);
        this.logicDeleteAnnotation = this.logicDelete ? field.getAnnotation(LogicDelete.class) : null;
        this.logicDeleteInitValue = this.logicDelete ? TypeConvertUtil.convert(this.logicDeleteAnnotation.beforeValue(), fieldInfo.getTypeClass()) : null;
        this.writeFieldInvoker = new SetFieldInvoker(field);
        typeHandler = MybatisTypeHandlerUtil.createTypeHandler(this.fieldInfo, this.tableFieldAnnotation.typeHandler());
        this.isTableSplitKey = field.isAnnotationPresent(SplitTableKey.class);
        this.canUpdateField = this.exists && !this.tableId && !this.logicDelete && !this.version && !this.logicDeleteTime;
    }

    public Object getValue(Object object) {
        try {
            return this.readFieldInvoker.invoke(object, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getField() {
        return field;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public GetFieldInvoker getReadFieldInvoker() {
        return readFieldInvoker;
    }

    public Table getTableAnnotation() {
        return tableAnnotation;
    }

    public TableField getTableFieldAnnotation() {
        return tableFieldAnnotation;
    }

    public boolean isTableId() {
        return tableId;
    }

    public boolean isVersion() {
        return version;
    }

    public boolean isTenantId() {
        return tenantId;
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public LogicDelete getLogicDeleteAnnotation() {
        return logicDeleteAnnotation;
    }

    public Object getLogicDeleteInitValue() {
        return logicDeleteInitValue;
    }

    public SetFieldInvoker getWriteFieldInvoker() {
        return writeFieldInvoker;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public boolean isTableSplitKey() {
        return isTableSplitKey;
    }

    public boolean isLogicDeleteTime() {
        return logicDeleteTime;
    }

    public boolean isCanUpdateField() {
        return canUpdateField;
    }

    public boolean isExists() {
        return exists;
    }
}
