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

package cn.mybatis.mp.core.mybatis.mapper.context;


import cn.mybatis.mp.core.mybatis.provider.MybatisSqlBuilderContext;
import db.sql.api.DbType;
import db.sql.api.SQLMode;
import db.sql.api.impl.cmd.executor.Executor;
import db.sql.api.impl.tookit.SQLOptimizeUtils;

import java.util.Objects;

public abstract class BaseSQLCmdContext<E extends Executor> implements SQLCmdContext<E> {

    protected MybatisSqlBuilderContext sqlBuilderContext;

    protected String sql;
    protected E execution;

    private DbType dbType;

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
    public void init(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public String sql(DbType dbType) {
        if (Objects.nonNull(sql)) {
            return sql;
        }
        sqlBuilderContext = new MybatisSqlBuilderContext(dbType, SQLMode.PREPARED);
        sql = getExecution().sql(null, null, sqlBuilderContext, new StringBuilder(SQLOptimizeUtils.getStringBuilderCapacity(getExecution().cmds()))).toString();
        return sql;
    }

    @Override
    public Object[] getParameters() {
        return sqlBuilderContext.getParams();
    }

    public DbType getDbType() {
        return dbType;
    }
}
