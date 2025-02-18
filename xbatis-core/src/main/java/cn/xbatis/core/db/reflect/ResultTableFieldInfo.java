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

import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;


public class ResultTableFieldInfo extends ResultFieldInfo {

    private final TableInfo tableInfo;

    private final TableFieldInfo tableFieldInfo;

    private final int storey;

    private final Class type;

    public ResultTableFieldInfo(Class type, int storey, String tablePrefix, TableInfo tableInfo, TableFieldInfo tableFieldInfo, Field field) {
        this(true, type, storey, tablePrefix, tableInfo, tableFieldInfo, field);
    }

    public ResultTableFieldInfo(boolean resultMapping, Class type, int storey, String tablePrefix, TableInfo tableInfo, TableFieldInfo tableFieldInfo, Field field) {
        super(resultMapping, type, field, tablePrefix + tableFieldInfo.getColumnName(), getTypeHandler(field, tableFieldInfo), tableFieldInfo.getTableFieldAnnotation().jdbcType());
        this.type = type;
        this.tableInfo = tableInfo;
        this.tableFieldInfo = tableFieldInfo;
        this.storey = storey;
    }

    static Class<? extends TypeHandler<?>> getTypeHandler(Field field, TableFieldInfo tableFieldInfo) {
        if (field.isAnnotationPresent(cn.xbatis.db.annotations.TypeHandler.class)) {
            cn.xbatis.db.annotations.TypeHandler th = field.getAnnotation(cn.xbatis.db.annotations.TypeHandler.class);
            return th.value();
        } else {
            return tableFieldInfo.getTableFieldAnnotation().typeHandler();
        }
    }

    public int getStorey() {
        return storey;
    }

    public Class getType() {
        return type;
    }

    public TableFieldInfo getTableFieldInfo() {
        return tableFieldInfo;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }
}
