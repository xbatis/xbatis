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

import cn.xbatis.core.db.reflect.ModelInfo;
import cn.xbatis.core.db.reflect.Models;
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.db.Model;
import db.sql.api.DbType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.Map;
import java.util.Objects;

public class ModelInsertContext<T extends Model> extends SQLCmdInsertContext<BaseInsert, T> implements SetIdMethod {

    private final BaseInsert<?> insert;

    private final T model;

    private final ModelInfo modelInfo;

    private final SaveStrategy strategy;

    private final boolean idHasValue;

    private final Map<String, Object> defaultValueContext;

    public ModelInsertContext(BaseInsert<?> insert, T model, SaveStrategy strategy, Map<String, Object> defaultValueContext) {
        this.insert = insert;
        this.model = model;
        this.strategy = strategy;
        this.modelInfo = Models.get(model.getClass());
        this.entityType = modelInfo.getEntityType();
        this.idHasValue = IdUtil.isIdExists(model, modelInfo.getIdFieldInfo());
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
        return ModelInsertCreateUtil.create(insert, modelInfo, model, strategy, dbType, defaultValueContext);
    }

    @Override
    public void setId(Object id, int index) {
        IdUtil.setId(this.model, this.modelInfo.getSingleIdFieldInfo(true), id);
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
        return this.model;
    }

    @Override
    public TypeHandler<?> getIdTypeHandler(Configuration configuration) {
        if (Objects.isNull(this.modelInfo.getIdFieldInfo())) {
            return null;
        }
        if (!this.modelInfo.getIdFieldInfo().getTableFieldInfo().getFieldInfo().getTypeClass().isAssignableFrom(this.modelInfo.getIdFieldInfo().getFieldInfo().getTypeClass())) {
            return null;
        }
        TypeHandler typeHandler = this.modelInfo.getIdFieldInfo().getTableFieldInfo().getTypeHandler();
        if (Objects.isNull(typeHandler)) {
            return configuration.getTypeHandlerRegistry().getTypeHandler(this.modelInfo.getIdFieldInfo().getTableFieldInfo().getFieldInfo().getTypeClass());
        }
        return null;
    }

    @Override
    public String getIdColumnName() {
        return this.modelInfo.getTableInfo().getIdFieldInfo().getColumnName();
    }
}
