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

package cn.xbatis.core.util;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.db.reflect.Default;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.exception.NotTableClassException;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.Getter;
import db.sql.api.tookit.LambdaUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;


public final class TableInfoUtil {

    public static void setValue(TableFieldInfo tableFieldInfo, Object target, Object value) {
        try {
            tableFieldInfo.getWriteFieldInvoker().invoke(target, new Object[]{value});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查是否有ID
     *
     * @param tableInfo
     */
    public static void checkId(TableInfo tableInfo) {
        tableInfo.getSingleIdFieldInfo(true);
    }

    /**
     * 从实体类中获取ID
     *
     * @param entity 实体
     * @return 返回ID
     */
    public static Serializable getEntityIdValue(Object entity) {
        return getEntityIdValue(Tables.get(entity.getClass()), entity, false);
    }

    /**
     * 从实体类中获取ID
     *
     * @param tableInfo 表信息
     * @param entity    实体
     * @return 返回ID
     */
    public static Serializable getEntityIdValue(TableInfo tableInfo, Object entity) {
        return getEntityIdValue(tableInfo, entity, true);
    }

    /**
     * 从实体类中获取ID
     *
     * @param tableInfo 表信息
     * @param entity    实体
     * @param check     是否检查
     * @return 返回ID
     */
    public static Serializable getEntityIdValue(TableInfo tableInfo, Object entity, boolean check) {
        if (check) {
            if (entity.getClass() != tableInfo.getType()) {
                throw new RuntimeException("Not Supported");
            }
        }
        TableInfoUtil.checkId(tableInfo);
        return (Serializable) getEntityFieldValue(tableInfo.getSingleIdFieldInfo(true), entity);
    }

    /**
     * @param tableFieldInfo
     * @param entity
     * @return
     */
    public static Object getEntityFieldValue(TableFieldInfo tableFieldInfo, Object entity) {
        try {
            return tableFieldInfo.getReadFieldInvoker().invoke(entity, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getTableName(Class entity) {
        Table table = (Table) entity.getAnnotation(Table.class);
        String tableName = table.value();
        if (StringPool.EMPTY.equals(tableName)) {
            //未设置表
            tableName = entity.getSimpleName();
            if (XbatisGlobalConfig.isTableUnderline()) {
                tableName = NamingUtil.camelToUnderline(tableName);
            }
        }

        tableName = buildDatabaseCaseNaming(table, tableName);
        return tableName;
    }

    /**
     * 获取主键的注解信息，非ID 返回 null
     *
     * @param field
     * @param dbType
     * @return
     */
    public static TableId getTableIdAnnotation(Field field, DbType dbType) {
        TableId[] tableIdAnnotations = field.getAnnotationsByType(TableId.class);
        if (tableIdAnnotations.length < 1) {
            return null;
        }
        TableId tableId = null;
        for (TableId item : tableIdAnnotations) {
            if (item.dbType() == dbType) {
                tableId = item;
                break;
            }
        }
        if (Objects.isNull(tableId)) {
            tableId = tableIdAnnotations[0];
        }
        return tableId;
    }

    /**
     * 获取TableField注解信息 未配置则用默认的 Default.defaultTableFieldAnnotation()
     *
     * @param field
     * @return
     */
    public static TableField getTableFieldAnnotation(Field field) {
        TableField tableFieldAnnotation = field.getAnnotation(TableField.class);
        if (Objects.isNull(tableFieldAnnotation)) {
            tableFieldAnnotation = Default.defaultTableFieldAnnotation();
        }
        return tableFieldAnnotation;
    }

    /**
     * 获取列名
     *
     * @param field
     * @return
     */
    public static String getFieldColumnName(Table table, Field field) {
        TableField tableFieldAnnotation = getTableFieldAnnotation(field);
        String columnName = tableFieldAnnotation.value();
        if (StringPool.EMPTY.equals(columnName)) {
            columnName = field.getName();
            switch (table.columnNameRule()) {
                case IGNORE: {
                    if (XbatisGlobalConfig.isColumnUnderline()) {
                        columnName = NamingUtil.camelToUnderline(columnName);
                    }
                    break;
                }
                case UNDERLINE: {
                    columnName = NamingUtil.camelToUnderline(columnName);
                    break;
                }
                case USE_FIELD_NAME: {
                    break;
                }
            }
        }

        columnName = buildDatabaseCaseNaming(table, columnName);
        return columnName;
    }

    public static <E> String getColumnName(Getter<E> column) {
        LambdaUtil.LambdaFieldInfo fieldInfo = LambdaUtil.getFieldInfo(column);
        Class entity = fieldInfo.getType();
        TableInfo tableInfo;
        try {
            tableInfo = Tables.get(entity);
        } catch (NotTableClassException e) {
            throw new RuntimeException("class " + entity.getName() + " is not entity");
        }

        String filedName = fieldInfo.getName();
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(filedName);
        if (Objects.isNull(tableFieldInfo)) {
            throw new RuntimeException("property " + filedName + " is not a column");
        }
        return tableFieldInfo.getColumnName();
    }

    public static boolean isInsertDoBeforeTableField(TableFieldInfo tableFieldInfo) {
        if (!tableFieldInfo.getTableFieldAnnotation().insert()) {
            return false;
        }
        return isCommonInsertDoBeforeTableField(tableFieldInfo);
    }

    static boolean isCommonInsertDoBeforeTableField(TableFieldInfo tableFieldInfo) {
        if (tableFieldInfo.isTableId()) {
            return true;
        }
        if (tableFieldInfo.isLogicDelete()) {
            return true;
        }
        if (!StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().defaultValue())) {
            return true;
        }
        if (tableFieldInfo.isVersion()) {
            return true;
        }

        return false;
    }

    public static boolean isUpdateDoBeforeTableField(TableFieldInfo tableFieldInfo) {
        if (!tableFieldInfo.getTableFieldAnnotation().update()) {
            return false;
        }
        return isCommonUpdateDoBeforeTableField(tableFieldInfo);
    }

    public static boolean isCommonUpdateDoBeforeTableField(TableFieldInfo tableFieldInfo) {
        if (tableFieldInfo.isTenantId()) {
            return true;
        }
        if (!StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().updateDefaultValue())) {
            return true;
        }
        return false;
    }

    public static String buildDatabaseCaseNaming(Table table, String name) {
        switch (table.databaseCaseRule()) {
            case DEFAULT: {
                // 采用全局的规则
                return XbatisGlobalConfig.getDatabaseCaseRule().convert(name);
            }
            default: {
                // 采用注解的规则
                return table.databaseCaseRule().convert(name);
            }
        }
    }
}
