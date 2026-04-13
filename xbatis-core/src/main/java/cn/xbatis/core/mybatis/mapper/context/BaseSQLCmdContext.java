/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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


import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.provider.MybatisSqlBuilderContext;
import db.sql.api.IDbType;
import db.sql.api.SQLMode;
import db.sql.api.impl.cmd.executor.Executor;
import db.sql.api.impl.tookit.SQLOptimizeUtils;

import java.util.List;
import java.util.Objects;

public abstract class BaseSQLCmdContext<E extends Executor> implements SQLCmdContext<E> {
    protected List<Object> parameters;
    protected String sql;
    protected E execution;
    protected IDbType dbType;
    protected BasicMapper basicMapper;

    public BaseSQLCmdContext() {

    }

    public BaseSQLCmdContext(E execution) {
        this.execution = execution;
    }

    @Override
    public E getExecution() {
        return execution;
    }

    @Override
    public String sql(IDbType dbType) {
        if (Objects.nonNull(sql)) {
            return sql;
        }
        MybatisSqlBuilderContext sqlBuilderContext = new MybatisSqlBuilderContext(dbType, SQLMode.PREPARED);
        sql = getExecution().sql(null, null, sqlBuilderContext, new StringBuilder(SQLOptimizeUtils.getStringBuilderCapacity(getExecution().cmds()))).toString();
        this.parameters = sqlBuilderContext.getParams();
        return sql;
    }

    @Override
    public List<Object> getParameters() {
        return this.parameters;
    }

    @Override
    public IDbType getDbType() {
        return dbType;
    }

    @Override
    public void setDbType(IDbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public BasicMapper $getBasicMapper() {
        return this.basicMapper;
    }

    @Override
    public void $setBasicMapper(BasicMapper basicMapper) {
        this.basicMapper = basicMapper;
    }
}
