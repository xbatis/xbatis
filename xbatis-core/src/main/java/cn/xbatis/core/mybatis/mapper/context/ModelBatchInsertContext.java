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
import cn.xbatis.core.mybatis.mapper.context.strategy.SaveBatchStrategy;
import cn.xbatis.core.sql.executor.BaseInsert;
import cn.xbatis.db.Model;
import db.sql.api.DbType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.Collection;
import java.util.Objects;

public class ModelBatchInsertContext<M extends Model> extends SQLCmdInsertContext<BaseInsert, M[]> implements SetIdMethod {

    private final SaveBatchStrategy<M> saveBatchStrategy;

    private final ModelInfo modelInfo;

    private final boolean idHasValue;

    private final BaseInsert<?> insert;

    public ModelBatchInsertContext(BaseInsert<?> insert, ModelInfo modelInfo, Collection<M> list, SaveBatchStrategy<M> saveBatchStrategy) {
        super((M[]) list.toArray(new Model[0]));
        this.insert = insert;
        this.modelInfo = modelInfo;
        this.saveBatchStrategy = saveBatchStrategy;
        this.entityType = modelInfo.getEntityType();
        this.idHasValue = IdUtil.isIdExists(this.getInsertData()[0], modelInfo.getIdFieldInfo());
    }

    @Override
    public void init(DbType dbType) {
        super.init(dbType);
        if (Objects.isNull(this.execution)) {
            this.execution = ModelBatchInsertCreateUtil.create(insert, this.modelInfo, this.getInsertData(), saveBatchStrategy, dbType, useBatchExecutor);
        }
    }

    @Override
    public void setId(Object id, int index) {
        IdUtil.setId(this.getInsertData()[index], this.modelInfo.getSingleIdFieldInfo(true), id);
    }

    @Override
    public boolean idHasValue() {
        return idHasValue;
    }

    @Override
    public int getInsertSize() {
        return this.getInsertData().length;
    }

    @Override
    public Object getInsertData(int index) {
        return this.getInsertData()[index];
    }

    @Override
    public TypeHandler<?> getIdTypeHandler(Configuration configuration) {
        if (Objects.nonNull(this.modelInfo.getIdFieldInfo())) {
            TypeHandler typeHandler = this.modelInfo.getIdFieldInfo().getTableFieldInfo().getTypeHandler();
            if (Objects.isNull(typeHandler)) {
                return configuration.getTypeHandlerRegistry().getTypeHandler(this.modelInfo.getIdFieldInfo().getFieldInfo().getTypeClass());
            }
        }
        return null;
    }

    @Override
    public String getIdColumnName() {
        return this.modelInfo.getTableInfo().getIdFieldInfo().getColumnName();
    }
}
