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

package db.sql.api.impl.cmd;


import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.ICmdFactory;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.executor.ISubQuery;
import db.sql.api.impl.cmd.basic.AllField;
import db.sql.api.impl.cmd.basic.DatasetField;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.executor.AbstractSubQuery;
import db.sql.api.impl.cmd.executor.SubQuery;
import db.sql.api.tookit.LambdaUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;


public class CmdFactory implements ICmdFactory<Table, TableField> {

    protected final Map<String, Table> tableCache = new HashMap<>(5);

    private final String tableAsPrefix;

    protected int tableNums = 0;

    public CmdFactory() {
        this("t");
    }

    public CmdFactory(String tableAsPrefix) {
        this.tableAsPrefix = tableAsPrefix;
    }

    protected String tableAs(int storey, int tableNums) {
        return this.tableAsPrefix +
                (tableNums == 1 ? "" : tableNums);
    }

    public ConditionFactory createConditionFactory() {
        return new ConditionFactory(this);
    }

    public Table cacheTable(Class<?> entity, int storey) {
        return this.tableCache.get(storey + entity.getName());
    }

    public boolean existsTable(Class<?> entity, int storey) {
        return this.tableCache.containsKey(storey + entity.getName());
    }

    @Override
    public Table table(Class<?> entity, int storey) {
        if (storey > 1) {
            //如果前面那个表没设置 则 从1 到 storey 初始化 以保证顺序
            if (this.cacheTable(entity, storey - 1) == null) {
                for (int i = 1; i < storey; i++) {
                    this.table(entity, i);
                }
            }
        }

        return tableCache.computeIfAbsent(storey + entity.getName(), key -> {
            Table table = new Table(entity.getSimpleName());
            table.as(tableAs(storey, ++tableNums));
            return table;
        });
    }

    @Override
    public Table table(String tableName) {
        return new Table(tableName);
    }

    @Override
    public <T> String columnName(Getter<T> column) {
        return LambdaUtil.getName(column);
    }

    @Override
    public <T> TableField field(Getter<T> column, int storey) {
        LambdaUtil.LambdaFieldInfo fieldInfo = LambdaUtil.getFieldInfo(column);
        return this.field(fieldInfo.getType(), storey, fieldInfo.getName());
    }

    @Override
    public <T> TableField[] fields(int storey, Getter<T>... columns) {
        TableField[] tableFields = new TableField[columns.length];
        for (int i = 0; i < columns.length; i++) {
            tableFields[i] = field(columns[i], storey);
        }
        return tableFields;
    }

    @Override
    @SafeVarargs
    public final TableField[] fields(GetterField... getterFields) {
        TableField[] tableFields = new TableField[getterFields.length];
        for (int i = 0; i < getterFields.length; i++) {
            GetterField getterField = getterFields[i];
            tableFields[i] = field(getterField.getGetter(), getterField.getStorey());
        }
        return tableFields;
    }

    public <T> TableField field(Table table, Getter<T> column) {
        return new TableField(table, columnName(column));
    }

    @Override
    public TableField field(Class<?> entity, String filedName, int storey) {
        return this.field(entity, storey, filedName);
    }

    public TableField field(Table table, String columnName, boolean id) {
        return new TableField(table, columnName, id);
    }

    @Override
    public <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD field(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column) {
        String filedName = LambdaUtil.getName(column);
        return (DATASET_FIELD) new DatasetField(dataset, filedName);
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD field(IDataset<DATASET, DATASET_FIELD> dataset, String columnName) {
        if (dataset instanceof Table) {
            return (DATASET_FIELD) new TableField((Table) dataset, columnName);
        }
        return (DATASET_FIELD) new DatasetField(dataset, columnName);
    }

    @Override
    public <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> DATASET_FIELD allField(IDataset<DATASET, DATASET_FIELD> dataset) {
        return (DATASET_FIELD) new AllField(dataset);
    }

    @Override
    public <T, R extends Cmd> R create(Getter<T> column, int storey, Function<TableField, R> RF) {
        return RF.apply(this.field(column, storey));
    }

    @Override
    public AbstractSubQuery<?, ?> createSubQuery() {
        return new SubQuery();
    }

    @Override
    public <T, E> ISubQuery createExistsOrNotExistsSubQuery(T executor, Class<E> entity, BiConsumer<T, ISubQuery> consumer) {
        ISubQuery subQuery = this.createSubQuery();
        subQuery.from(entity);

        if (consumer != null) {
            consumer.accept(executor, subQuery);
        }

        if (subQuery.getSelect() == null || subQuery.getSelect().getSelectField().isEmpty()) {
            subQuery.select1();
        }
        return subQuery;
    }


    @Override
    public <T, E1, E2> ISubQuery createExistsOrNotExistsSubQuery(T executor, Getter<E1> sourceGetter, int sourceStorey, Getter<E2> targetGetter, BiConsumer<T, ISubQuery> consumer) {
        ISubQuery subQuery = this.createSubQuery();
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(targetGetter);
        subQuery.from(lambdaFieldInfo.getType());
        subQuery.eq(targetGetter, this.field(sourceGetter, sourceStorey));

        if (consumer != null) {
            consumer.accept(executor, subQuery);
        }

        if (subQuery.getSelect() == null || subQuery.getSelect().getSelectField().isEmpty()) {
            subQuery.select1();
        }
        return subQuery;
    }

    @Override
    public <T, E> ISubQuery createInOrNotInSubQuery(T executor, Getter<E> selectGetter, BiConsumer<T, ISubQuery> consumer) {
        ISubQuery subQuery = this.createSubQuery();
        subQuery.select(selectGetter);
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(selectGetter);
        subQuery.from(lambdaFieldInfo.getType());
        if (consumer != null) {
            consumer.accept(executor, subQuery);
        }
        return subQuery;
    }

    protected TableField field(Class<?> clazz, int storey, String filedName) {
        Table table = table(clazz, storey);
        return new TableField(table, filedName);
    }
}
