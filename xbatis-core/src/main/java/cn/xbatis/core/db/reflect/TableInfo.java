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
import cn.xbatis.core.logicDelete.LogicDeleteUtil;
import cn.xbatis.core.util.FieldUtil;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.annotations.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class TableInfo {

    /**
     * 对应的类
     */
    private final Class<?> type;

    /**
     * 数据库 schema
     */
    private final String schema;

    /**
     * 表名
     */
    private final String tableName;

    private final String schemaAndTableName;

    /**
     * 所有 字段
     */
    private final List<TableFieldInfo> tableFieldInfos;

    /**
     * 字段个数
     */
    private final int fieldSize;

    private final List<TableFieldInfo> idFieldInfos;

    private final String[] idColumnNames;

    private final boolean hasMultiId;

    private final TableFieldInfo idFieldInfo;

    private final boolean isSplitTable;

    private final TableSplitter tableSplitter;

    private final TableFieldInfo splitFieldInfo;

    /**
     * 乐观锁字段
     */
    private final TableFieldInfo versionFieldInfo;

    /**
     * 多租户字段
     */
    private final TableFieldInfo tenantIdFieldInfo;

    /**
     * 逻辑删除字段
     */
    private final TableFieldInfo logicDeleteFieldInfo;

    /**
     * 逻辑删除时间字段
     */
    private final TableFieldInfo logicDeleteTimeFieldInfo;

    /**
     * 外键关系
     */
    private final Map<Class<?>, ForeignInfo> foreignInfoMap;

    /**
     * 字段信息 key为属性字段名 value为字段信息
     */
    private final Map<String, TableFieldInfo> tableFieldInfoMap;

    /**
     * 是否有忽略的列
     */
    private final boolean hasIgnoreField;

    /**
     * 插入时，需要提前处理的字段
     */
    private final List<TableFieldInfo> insertDoBeforeTableFieldInfos;

    /**
     * 更新时，需要提前处理的字段
     */
    private final List<TableFieldInfo> updateDoBeforeTableFieldInfos;

    private final Table annotation;

    public TableInfo(Class<?> entity) {
        this.type = entity;

        this.annotation = entity.getAnnotation(Table.class);

        this.schema = TableInfoUtil.buildDatabaseCaseNaming(annotation, annotation.schema());

        SplitTable splitTable = entity.getAnnotation(SplitTable.class);
        this.isSplitTable = splitTable != null;
        if (this.isSplitTable) {
            try {
                this.tableSplitter = splitTable.value().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.tableSplitter = null;
        }
        this.tableName = TableInfoUtil.getTableName(entity);
        if (schema == null || StringPool.EMPTY.equals(schema)) {
            this.schemaAndTableName = tableName;
        } else {
            this.schemaAndTableName = schema + "." + tableName;
        }


        TableFieldInfo versionFieldInfo = null;
        TableFieldInfo tenantIdFieldInfo = null;
        TableFieldInfo logicDeleteFieldInfo = null;
        TableFieldInfo logicDeleteTimeFieldInfo = null;

        List<TableFieldInfo> tableFieldInfos = new ArrayList<>();
        Map<String, TableFieldInfo> tableFieldInfoMap = new HashMap<>();
        Map<Class<?>, ForeignInfo> foreignInfoMap = new HashMap<>();


        List<TableFieldInfo> idFieldInfos = new ArrayList<>(6);

        List<Field> fieldList = FieldUtil.getFields(entity);


        for (Field field : fieldList) {
            TableFieldInfo tableFieldInfo = new TableFieldInfo(entity, annotation, field);
            tableFieldInfos.add(tableFieldInfo);
            tableFieldInfoMap.put(field.getName(), tableFieldInfo);

            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                foreignInfoMap.put(foreignKey.value(), new ForeignInfo(foreignKey.value(), tableFieldInfo));
            }

            if (tableFieldInfo.isTableId()) {
                idFieldInfos.add(tableFieldInfo);
            }

            if (tableFieldInfo.isVersion()) {
                if (versionFieldInfo != null) {
                    throw new RuntimeException("Entity " + entity.getName() + " has multi @Version");
                }
                versionFieldInfo = tableFieldInfo;
            }

            if (tableFieldInfo.isTenantId()) {
                if (tenantIdFieldInfo != null) {
                    throw new RuntimeException("Entity " + entity.getName() + " has multi @TenantId");
                }
                tenantIdFieldInfo = tableFieldInfo;
            }

            if (tableFieldInfo.isLogicDelete()) {
                if (logicDeleteFieldInfo != null) {
                    throw new RuntimeException("Entity " + entity.getName() + " has multi @LogicDelete");
                }
                logicDeleteFieldInfo = tableFieldInfo;
                LogicDelete logicDeleteAnnotation = field.getAnnotation(LogicDelete.class);
                if (XbatisGlobalConfig.isDynamicValueKeyFormat(logicDeleteAnnotation.beforeValue())) {
                    throw new RuntimeException("the @LogicDelete of Entity " + entity.getName() + " has config error,the beforeValue can't be dynamic key");
                }
            }

            if (tableFieldInfo.isLogicDeleteTime()) {
                logicDeleteTimeFieldInfo = tableFieldInfo;
            }
        }

        TableFieldInfo idFieldInfo = null;
        if (idFieldInfos.size() == 1) {
            idFieldInfo = idFieldInfos.get(0);
        }

        boolean hasMutilId = idFieldInfos.size() > 1;

        this.tableFieldInfos = Collections.unmodifiableList(tableFieldInfos);
        this.fieldSize = this.tableFieldInfos.size();
        this.hasMultiId = hasMutilId;
        this.idFieldInfos = Collections.unmodifiableList(idFieldInfos);
        this.idColumnNames = idFieldInfos.stream().filter(item -> item.isTableId()).map(item -> item.getColumnName()).collect(Collectors.toList()).toArray(new String[0]);
        this.idFieldInfo = idFieldInfo;
        this.versionFieldInfo = versionFieldInfo;
        this.tenantIdFieldInfo = tenantIdFieldInfo;
        this.logicDeleteFieldInfo = logicDeleteFieldInfo;
        this.logicDeleteTimeFieldInfo = logicDeleteTimeFieldInfo;

        this.tableFieldInfoMap = Collections.unmodifiableMap(tableFieldInfoMap);
        this.foreignInfoMap = Collections.unmodifiableMap(foreignInfoMap);

        if (Objects.nonNull(this.logicDeleteTimeFieldInfo)) {
            LogicDeleteUtil.getLogicDeleteTimeValue(this);
        }

        this.hasIgnoreField = tableFieldInfos.stream().anyMatch(item -> !item.getTableFieldAnnotation().select() && !item.getTableFieldAnnotation().exists());

        if (this.isSplitTable) {
            long splitTableSize = tableFieldInfos.stream().filter(i -> i.isTableSplitKey()).count();
            if (splitTableSize == 0) {
                throw new RuntimeException("Entity " + entity.getName() + " has no @TableSplitKey");
            } else if (splitTableSize != 1) {
                throw new RuntimeException("Entity " + entity.getName() + " has multi @TableSplitKey");
            }

            this.splitFieldInfo = tableFieldInfos.stream().filter(i -> i.isTableSplitKey()).findFirst().get();
        } else {
            this.splitFieldInfo = null;
        }

        this.insertDoBeforeTableFieldInfos = Collections.unmodifiableList(this.tableFieldInfos.stream().filter(TableInfoUtil::isInsertDoBeforeTableField).collect(Collectors.toList()));
        this.updateDoBeforeTableFieldInfos = Collections.unmodifiableList(this.tableFieldInfos.stream().filter(TableInfoUtil::isUpdateDoBeforeTableField).collect(Collectors.toList()));
    }

    /**
     * 根据字段名获取字段信息
     *
     * @param property
     * @return TableFieldInfo
     */
    public final TableFieldInfo getFieldInfo(String property) {
        return tableFieldInfoMap.get(property);
    }

    /**
     * 根据列名获取字段信息
     *
     * @param columnName
     * @return TableFieldInfo
     */
    public final TableFieldInfo getFieldInfoByColumnName(String columnName) {
        return tableFieldInfos.stream().filter(item -> item.getColumnName().equals(columnName)).findFirst().orElse(null);
    }


    /**
     * 根据连接的表的类获取外键匹配信息
     *
     * @param entityClass
     * @return ForeignInfo
     */
    public final ForeignInfo getForeignInfo(Class<?> entityClass) {
        return this.foreignInfoMap.get(entityClass);
    }

    public Table getAnnotation() {
        return annotation;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchemaAndTableName() {
        return schemaAndTableName;
    }

    public Map<Class<?>, ForeignInfo> getForeignInfoMap() {
        return foreignInfoMap;
    }

    public Map<String, TableFieldInfo> getTableFieldInfoMap() {
        return tableFieldInfoMap;
    }

    public List<TableFieldInfo> getTableFieldInfos() {
        return tableFieldInfos;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public TableFieldInfo getVersionFieldInfo() {
        return versionFieldInfo;
    }

    public TableFieldInfo getTenantIdFieldInfo() {
        return tenantIdFieldInfo;
    }

    public TableFieldInfo getLogicDeleteFieldInfo() {
        return logicDeleteFieldInfo;
    }

    public boolean isHasIgnoreField() {
        return hasIgnoreField;
    }

    public boolean isHasMultiId() {
        return hasMultiId;
    }

    public List<TableFieldInfo> getIdFieldInfos() {
        return idFieldInfos;
    }


    public TableFieldInfo getSingleIdFieldInfo(boolean throwException) {
        if (throwException && Objects.isNull(this.idFieldInfo)) {
            throw new RuntimeException("Entity:" + this.type + " has multi ID or non-single ID.");
        }
        return this.idFieldInfo;
    }

    public TableFieldInfo getIdFieldInfo() {
        return idFieldInfo;
    }

    public String[] getIdColumnNames() {
        return idColumnNames;
    }

    public boolean isSplitTable() {
        return isSplitTable;
    }

    public TableSplitter getTableSplitter() {
        return tableSplitter;
    }

    public List<TableFieldInfo> getInsertDoBeforeTableFieldInfos() {
        return insertDoBeforeTableFieldInfos;
    }

    public List<TableFieldInfo> getUpdateDoBeforeTableFieldInfos() {
        return updateDoBeforeTableFieldInfos;
    }

    public TableFieldInfo getSplitFieldInfo() {
        return splitFieldInfo;
    }

    public TableFieldInfo getLogicDeleteTimeFieldInfo() {
        return logicDeleteTimeFieldInfo;
    }
}
