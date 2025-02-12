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


import cn.mybatis.mp.core.MybatisMpConfig;
import cn.mybatis.mp.core.mybatis.provider.MybatisSqlBuilderContext;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import db.sql.api.DbType;
import db.sql.api.SQLMode;

import java.util.Objects;

public class SQLCmdInsertContext<T extends BaseInsert, DATA> extends BaseSQLCmdContext<T> {

    protected Class<?> entityType;

    protected boolean useBatchExecutor = false;

    private DATA insertData;

    public SQLCmdInsertContext() {

    }

    public SQLCmdInsertContext(T t) {
        super(t);
    }

    public SQLCmdInsertContext(DATA insertData) {
        this.insertData = insertData;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public DATA getInsertData() {
        return insertData;
    }

    @Override
    public String sql(DbType dbType) {
        if (Objects.nonNull(sql)) {
            return sql;
        }
        sqlBuilderContext = new MybatisSqlBuilderContext(dbType, SQLMode.PREPARED);
        sql = MybatisMpConfig.getSQLBuilder().buildInsertSQL(getExecution(), sqlBuilderContext).toString();
        return sql;
    }

    public void setUseBatchExecutor(boolean useBatchExecutor) {
        this.useBatchExecutor = useBatchExecutor;
    }
}
