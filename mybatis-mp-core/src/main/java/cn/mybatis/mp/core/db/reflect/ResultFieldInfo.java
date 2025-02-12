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

import cn.mybatis.mp.db.annotations.ResultField;
import db.sql.api.impl.tookit.SqlUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;


public class ResultFieldInfo {

    private final boolean resultMapping;

    /**
     * 字段
     */
    private final Field field;

    /**
     * 字段类型信息
     */
    private final FieldInfo fieldInfo;

    /**
     * 隐射的列名
     */
    private final String mappingColumnName;


    /**
     * typeHandler
     */
    private final Class<? extends TypeHandler<?>> typeHandler;


    /**
     * jdbcType
     */
    private final JdbcType jdbcType;

    public ResultFieldInfo(Class clazz, Field field, ResultField resultField) {
        this(true, clazz, field, getColumnName(clazz, field, resultField), getTypeHandler(field, resultField), resultField.jdbcType());
    }

    public ResultFieldInfo(boolean resultMapping, Class clazz, Field field, String mappingColumnName, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType) {
        this.resultMapping = resultMapping;
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.mappingColumnName = mappingColumnName;
        this.typeHandler = typeHandler;
        this.jdbcType = jdbcType;
    }

    static Class<? extends TypeHandler<?>> getTypeHandler(Field field, ResultField resultField) {
        if (field.isAnnotationPresent(cn.mybatis.mp.db.annotations.TypeHandler.class)) {
            cn.mybatis.mp.db.annotations.TypeHandler th = field.getAnnotation(cn.mybatis.mp.db.annotations.TypeHandler.class);
            return th.value();
        }
        return resultField.typeHandler();
    }

    static String getColumnName(Class clazz, Field field, ResultField resultField) {
        String name = resultField.value();
        if (name.isEmpty()) {
            name = SqlUtil.getAsName(clazz, field);
        }
        return name;
    }

    public boolean isResultMapping() {
        return resultMapping;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Field getField() {
        return field;
    }

    public String getMappingColumnName() {
        return mappingColumnName;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }
}
