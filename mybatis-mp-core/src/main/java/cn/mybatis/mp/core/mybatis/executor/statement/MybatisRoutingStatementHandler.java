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

package cn.mybatis.mp.core.mybatis.executor.statement;

import cn.mybatis.mp.core.mybatis.mapper.context.BaseSQLCmdContext;
import db.sql.api.DbType;
import db.sql.api.impl.tookit.Objects;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MybatisRoutingStatementHandler extends RoutingStatementHandler {

    private final Object parameter;

    public MybatisRoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        this.parameter = parameter;
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement statement = super.prepare(connection, transactionTimeout);
        if (this.parameter instanceof BaseSQLCmdContext) {
            BaseSQLCmdContext sqlCmdContext = (BaseSQLCmdContext) this.parameter;
            if (sqlCmdContext.getExecution() instanceof Timeoutable) {
                Timeoutable timeoutable = (Timeoutable) sqlCmdContext.getExecution();
                if (Objects.nonNull(timeoutable.getTimeout())) {
                    statement.setQueryTimeout(timeoutable.getTimeout());
                    StatementUtil.applyTransactionTimeout(statement, timeoutable.getTimeout(), transactionTimeout);
                }
            }

            if (sqlCmdContext.getExecution() instanceof Fetchable) {
                Fetchable fetchable = (Fetchable) sqlCmdContext.getExecution();
                if (Objects.nonNull(fetchable.getFetchSize())) {
                    statement.setFetchSize(fetchable.getFetchSize());
                }
                if (Objects.nonNull(fetchable.getFetchDirection()) && sqlCmdContext.getDbType() != DbType.SQLITE) {
                    statement.setFetchDirection(fetchable.getFetchDirection());
                }
            }
        }
        return statement;
    }
}
