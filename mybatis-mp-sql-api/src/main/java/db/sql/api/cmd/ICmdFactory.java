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

package db.sql.api.cmd;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IDatasetField;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;

import java.util.function.Function;

public interface ICmdFactory<TABLE extends ITable<TABLE, TABLE_FIELD>
        , TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>> {

    default TABLE table(Class<?> entity) {
        return this.table(entity, 1);
    }

    /**
     * 根据实体类获取TABLE对象
     *
     * @param entity 实体类
     * @param storey 存储层级
     * @return
     */
    TABLE table(Class<?> entity, int storey);

    /**
     * 根据表名获取TABLE对象
     *
     * @param tableName
     * @return
     */
    TABLE table(String tableName);

    /**
     * 根据Lambda getter 获取列名
     *
     * @param column
     * @param <T>
     * @return
     */
    <T> String columnName(Getter<T> column);

    default <T> TABLE_FIELD field(Getter<T> column) {
        return this.field(column, 1);
    }

    /**
     * 根据Lambda getter 获取列对象
     *
     * @param column
     * @param storey 存储层级
     * @param <T>
     * @return
     */
    <T> TABLE_FIELD field(Getter<T> column, int storey);

    default <T> TABLE_FIELD[] fields(Getter<T>... columns) {
        return this.fields(1, columns);
    }

    <T> TABLE_FIELD[] fields(int storey, Getter<T>... columns);

    TABLE_FIELD[] fields(GetterField... getterFields);

    default TABLE_FIELD field(Class<?> entity, String filedName) {
        return this.field(entity, filedName, 1);
    }

    /**
     * 根据字段名获取TABLE_FIELD
     *
     * @param entity
     * @param filedName
     * @param storey
     * @return
     */
    TABLE_FIELD field(Class<?> entity, String filedName, int storey);


    /**
     * 根据dataset(可能是子查询 也可能是表),Lambda getter 创建列对象
     *
     * @param dataset
     * @param column
     * @param <T>
     * @return
     */
    <T, DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> IDatasetField field(IDataset<DATASET, DATASET_FIELD> dataset, Getter<T> column);

    /**
     * 根据dataset(可能是子查询 也可能是表) 列名，创建 列对象
     *
     * @param dataset
     * @param name
     * @return
     */
    <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> IDatasetField field(IDataset<DATASET, DATASET_FIELD> dataset, String name);

    /**
     * 所有列
     *
     * @param dataset
     * @return
     */
    <DATASET extends IDataset<DATASET, DATASET_FIELD>, DATASET_FIELD extends IDatasetField<DATASET_FIELD>> IDatasetField allField(IDataset<DATASET, DATASET_FIELD> dataset);


    /**
     * 根据Lambda getter，万能创建SQL命令方法
     *
     * @param column 列
     * @param RF     返回函数
     * @param <T>    实体类型
     * @param <R>    返回命令
     * @return
     */
    default <T, R extends Cmd> R create(Getter<T> column, Function<TABLE_FIELD, R> RF) {
        return this.create(column, 1, RF);
    }

    /**
     * 根据Lambda getter，万能创建SQL命令方法
     *
     * @param column 列
     * @param storey 缓存区
     * @param RF     返回函数
     * @param <T>    实体类型
     * @param <R>    返回命令
     * @return
     */
    <T, R extends Cmd> R create(Getter<T> column, int storey, Function<TABLE_FIELD, R> RF);
}
