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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Statement proxy to add logging.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class SQLAuditingStatementLogger extends BaseJdbcLogger implements InvocationHandler {

    private final static Field VALUES_FIELD;

    static {
        try {
            VALUES_FIELD = BaseJdbcLogger.class.getDeclaredField("columnValues");
            VALUES_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final Statement statement;
    private final SQLAuditing sqlAuditing = XbatisGlobalConfig.getSQLAuditing();
    private final long startTime;
    private final String sql;
    private List<Object> auditingParams;

    private SQLAuditingStatementLogger(long startTime, String sql, Statement stmt, Log statementLog, int queryStack) {
        super(statementLog, queryStack);
        this.statement = stmt;
        this.startTime = startTime;
        this.sql = sql;
    }

    /**
     * Creates a logging version of a Statement.
     *
     * @param stmt         the statement
     * @param statementLog the statement log
     * @param queryStack   the query stack
     * @return the proxy
     */
    public static Statement newInstance(String sql, Statement stmt, Log statementLog, int queryStack) {
        InvocationHandler handler = new SQLAuditingStatementLogger(System.currentTimeMillis(), sql, stmt, statementLog, queryStack);
        ClassLoader cl = Statement.class.getClassLoader();
        return (Statement) Proxy.newProxyInstance(cl, new Class[]{Statement.class}, handler);
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
                    debug(" Executing: " + removeExtraWhitespace((String) params[0]), true);
                }
                if ("executeQuery".equals(method.getName())) {
                    ResultSet rs = (ResultSet) method.invoke(statement, params);
                    return rs == null ? null : SQLAuditingResultSetLogger.newInstance(startTime, sql, this.getAuditingParams(), statement.getUpdateCount(), rs, statementLog, queryStack);
                } else {
                    return method.invoke(statement, params);
                }
            }
            if ("getResultSet".equals(method.getName())) {
                ResultSet rs = (ResultSet) method.invoke(statement, params);
                return rs == null ? null : SQLAuditingResultSetLogger.newInstance(startTime, sql, this.getAuditingParams(), statement.getUpdateCount(), rs, statementLog, queryStack);
            } else {
                return method.invoke(statement, params);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * return the wrapped statement.
     *
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

}
