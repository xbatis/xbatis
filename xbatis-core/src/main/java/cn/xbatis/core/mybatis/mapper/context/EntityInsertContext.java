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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.sql.executor.BaseInsert;
import db.sql.api.DbType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.Map;
import java.util.Objects;

public class EntityInsertContext<T> extends SQLCmdInsertContext<BaseInsert, T> implements SetIdMethod {
    private final BaseInsert<?> insert;

    private final T entity;

    private final TableInfo tableInfo;

    private final SaveStrategy strategy;

    private final boolean idHasValue;

    private final Map<String, Object> defaultValueContext;

    public EntityInsertContext(BaseInsert<?> insert, TableInfo tableInfo, T entity, SaveStrategy strategy, Map<String, Object> defaultValueContext) {
        this.insert = insert;
        this.entity = entity;
        this.strategy = strategy;
        this.entityType = entity.getClass();
        this.tableInfo = tableInfo;
        this.idHasValue = IdUtil.isIdExists(entity, tableInfo.getIdFieldInfo());
        this.defaultValueContext = defaultValueContext;
    }

    @Override
    public void init(DbType dbType) {
        super.init(dbType);
        if (Objects.isNull(this.execution)) {
            this.execution = createCmd(dbType);
        }
    }

    private BaseInsert createCmd(DbType dbType) {
        return EntityInsertCreateUtil.create(insert, tableInfo, entity, strategy, dbType, defaultValueContext);
    }

    @Override
    public void setId(Object id, int index) {
        IdUtil.setId(this.entity, this.tableInfo.getSingleIdFieldInfo(true), id);
    }

    @Override
    public boolean idHasValue() {
        return idHasValue;
    }

    @Override
    public int getInsertSize() {
        return 1;
    }

    @Override
    public Object getInsertData(int index) {
        return this.entity;
    }

    @Override
    public TypeHandler<?> getIdTypeHandler(Configuration configuration) {
        if (Objects.nonNull(this.tableInfo.getIdFieldInfo())) {
            TypeHandler typeHandler = this.tableInfo.getIdFieldInfo().getTypeHandler();
            if (Objects.isNull(typeHandler)) {
                return configuration.getTypeHandlerRegistry().getTypeHandler(this.tableInfo.getIdFieldInfo().getFieldInfo().getTypeClass());
            }
        }
        return null;
    }

    @Override
    public String getIdColumnName() {
        return this.tableInfo.getIdFieldInfo().getColumnName();
    }
}
