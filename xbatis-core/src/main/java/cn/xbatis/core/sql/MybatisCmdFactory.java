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

package cn.xbatis.core.sql;

import cn.xbatis.core.db.reflect.*;
import cn.xbatis.core.sql.executor.*;
import cn.xbatis.db.Model;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.ConditionFactory;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.tookit.LambdaUtil;
import org.apache.ibatis.util.MapUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * CMD 命令工厂
 * 增加了对实体类的映射
 */
public class MybatisCmdFactory extends CmdFactory {

    protected final Map<Class<?>, TableInfo> tableInfoCache = new HashMap<>(5);

    public MybatisCmdFactory() {
        super();
    }

    public MybatisCmdFactory(String tableAsPrefix) {
        super(tableAsPrefix);
    }

    public TableInfo getTableInfo(Class<?> entityClass) {
        return tableInfoCache.computeIfAbsent(entityClass, key -> Tables.get(entityClass));
    }

    public void cacheTableInfo(TableInfo tableInfo) {
        tableInfoCache.put(tableInfo.getType(), tableInfo);
    }

    @Override
    public ConditionFactory createConditionFactory() {
        return new MybatisConditionFactory(this);
    }

    @Override
    public MpTable table(Class entity, int storey) {
        if (storey > 1) {
            //如果前面那个表没设置 则 从1 到 storey 初始化 以保证顺序
            if (this.cacheTable(entity, storey - 1) == null) {
                for (int i = 1; i < storey; i++) {
                    this.table(entity, i);
                }
            }
        }
        return (MpTable) MapUtil.computeIfAbsent(this.tableCache, storey + entity.getName(), key -> {
            TableInfo tableInfo = getTableInfo(entity);
            return new MpTable(tableInfo, tableAs(storey, ++tableNums));
        });
    }

    @Override
    public <T> TableField field(Getter<T> column, int storey) {
        LambdaUtil.LambdaFieldInfo fieldInfo = LambdaUtil.getFieldInfo(column);
        if (Model.class.isAssignableFrom(fieldInfo.getType())) {
            ModelInfo modelInfo = Models.get(fieldInfo.getType());
            this.cacheTableInfo(modelInfo.getTableInfo());
            return this.field(modelInfo.getEntityType(), modelInfo.getFieldInfo(fieldInfo.getName()).getTableFieldInfo().getField().getName(), storey);
        }
        return this.field(fieldInfo.getType(), fieldInfo.getName(), storey);
    }

    @Override
    public <T> String columnName(Getter<T> column) {
        LambdaUtil.LambdaFieldInfo fieldInfo = LambdaUtil.getFieldInfo(column);
        if (Model.class.isAssignableFrom(fieldInfo.getType())) {
            ModelInfo modelInfo = Models.get(fieldInfo.getType());
            this.cacheTableInfo(modelInfo.getTableInfo());
            return modelInfo.getFieldInfo(fieldInfo.getName()).getTableFieldInfo().getColumnName();
        } else {
            TableInfo tableInfo = this.getTableInfo(fieldInfo.getType());
            this.cacheTableInfo(tableInfo);
            return tableInfo.getFieldInfo(fieldInfo.getName()).getColumnName();
        }
    }

    @Override
    public TableField field(Class entity, String filedName, int storey) {
        MpTable table = table(entity, storey);
        TableFieldInfo tableFieldInfo = table.getTableInfo().getFieldInfo(filedName);
        if (Objects.isNull(tableFieldInfo)) {
            throw new RuntimeException("property " + filedName + " is not a column");
        }
        return new MpTableField(table, tableFieldInfo);
    }

    @Override
    public <T> TableField[] fields(int storey, Getter<T>... columns) {
        TableField[] tableFields = new TableField[columns.length];
        for (int i = 0; i < columns.length; i++) {
            tableFields[i] = this.field(columns[i], storey);
        }
        return tableFields;
    }

    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD field(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column) {
        LambdaUtil.LambdaFieldInfo fieldInfo = LambdaUtil.getFieldInfo(column);
        TableInfo tableInfo = getTableInfo(fieldInfo.getType());
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(fieldInfo.getName());

        if (dataset instanceof MpTable) {
            return (DATASET_FIELD) new MpTableField((MpTable) dataset, tableFieldInfo);
        } else if (dataset instanceof Table) {
            return (DATASET_FIELD) new TableField((Table) dataset, tableFieldInfo.getColumnName(), tableFieldInfo.isTableId());
        }
        return (DATASET_FIELD) new MpDatasetField(dataset, tableFieldInfo);
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD field(IDataset<DATASET, DATASET_FIELD> dataset, String columnName) {
        if (dataset instanceof MpTable) {
            MpTable mpTable = (MpTable) dataset;
            return (DATASET_FIELD) mpTable.$(columnName);
        }
        return super.field(dataset, columnName);
    }

    @Override
    public SubQuery createSubQuery() {
        return new SubQuery();
    }
}
