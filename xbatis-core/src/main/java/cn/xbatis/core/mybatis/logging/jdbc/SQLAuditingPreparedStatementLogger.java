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

package cn.xbatis.core.mybatis.logging.jdbc;


import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.executor.SQLAuditing;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * PreparedStatement proxy to add logging.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class SQLAuditingPreparedStatementLogger extends BaseJdbcLogger implements InvocationHandler {

    private final static Field VALUES_FIELD;

    static {
        try {
            VALUES_FIELD = BaseJdbcLogger.class.getDeclaredField("columnValues");
            VALUES_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final PreparedStatement statement;
    private final String sql;
    private final long startTime;
    private final SQLAuditing sqlAuditing = XbatisGlobalConfig.getSQLAuditing();
    private List<Object> auditingParams;

    private SQLAuditingPreparedStatementLogger(long startTime, String sql, PreparedStatement stmt, Log statementLog, int queryStack) {
        super(statementLog, queryStack);
        this.statement = stmt;
        this.startTime = startTime;
        this.sql = sql;
    }

    /**
     * Creates a logging version of a PreparedStatement.
     *
     * @param stmt         - the statement
     * @param statementLog - the statement log
     * @param queryStack   - the query stack
     * @return - the proxy
     */
    public static PreparedStatement newInstance(String sql, PreparedStatement stmt, Log statementLog, int queryStack) {
        InvocationHandler handler = new SQLAuditingPreparedStatementLogger(System.currentTimeMillis(), sql, stmt, statementLog, queryStack);
        ClassLoader cl = PreparedStatement.class.getClassLoader();
        return (PreparedStatement) Proxy.newProxyInstance(cl,
                new Class[]{PreparedStatement.class, CallableStatement.class}, handler);
    }

    private List<Object> getAuditingParams() {
        if (sqlAuditing != null && auditingParams == null) {
            try {
                auditingParams = new ArrayList<>((List<Object>) VALUES_FIELD.get(this));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return auditingParams;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }
            if (EXECUTE_METHODS.contains(method.getName())) {
                if (isDebugEnabled()) {
                    debug("Parameters: " + getParameterValueString(), true);
                }

                if (sqlAuditing != null) {
                    auditingParams = new ArrayList<>((List<Object>) VALUES_FIELD.get(this));
                }

                clearColumnInfo();
                if ("executeQuery".equals(method.getName())) {
                    ResultSet rs = (ResultSet) method.invoke(statement, params);
                    return rs == null ? null : SQLAuditingResultSetLogger.newInstance(startTime, sql, getAuditingParams(), statement.getUpdateCount(), rs, statementLog, queryStack);
                } else {
                    return method.invoke(statement, params);
                }
            }
            if (SET_METHODS.contains(method.getName())) {
                if ("setNull".equals(method.getName())) {
                    setColumn(params[0], null);
                } else {
                    setColumn(params[0], params[1]);
                }
                return method.invoke(statement, params);
            } else if ("getResultSet".equals(method.getName())) {
                ResultSet rs = (ResultSet) method.invoke(statement, params);
                return rs == null ? null : SQLAuditingResultSetLogger.newInstance(startTime, sql, getAuditingParams(), statement.getUpdateCount(), rs, statementLog, queryStack);
            } else if ("getUpdateCount".equals(method.getName())) {
                int updateCount = (Integer) method.invoke(statement, params);
                if (updateCount != -1) {
                    debug("   Updates: " + updateCount, false);
                }

                // 增删改修改 SQL审计
                if (sqlAuditing != null) {
                    try {
                        sqlAuditing.auditOperation(sql, getAuditingParams(), startTime, System.currentTimeMillis(), -1, updateCount);
                    } catch (Exception ignored) {
                    }
                }
                return updateCount;
            } else {
                return method.invoke(statement, params);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * Return the wrapped prepared statement.
     *
     * @return the PreparedStatement
     */
    public PreparedStatement getPreparedStatement() {
        return statement;
    }

}

