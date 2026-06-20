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

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Connection proxy to add logging.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class SQLAuditingConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

    private final Connection connection;

    private SQLAuditingConnectionLogger(Connection conn, Log statementLog, int queryStack) {
        super(statementLog, queryStack);
        this.connection = conn;
    }

    /**
     * Creates a logging version of a connection.
     *
     * @param conn         the original connection
     * @param statementLog the statement log
     * @param queryStack   the query stack
     * @return the connection with logging
     */
    public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
        InvocationHandler handler = new SQLAuditingConnectionLogger(conn, statementLog, queryStack);
        ClassLoader cl = Connection.class.getClassLoader();
        return (Connection) Proxy.newProxyInstance(cl, new Class[]{Connection.class}, handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }

            if ("prepareStatement".equals(method.getName()) || "prepareCall".equals(method.getName())) {
                String sql = removeExtraWhitespace((String) params[0]);
                if (isDebugEnabled()) {
                    debug(" Preparing: " + sql, true);
                }
                PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
                return SQLAuditingPreparedStatementLogger.newInstance(sql, stmt, statementLog, queryStack);
            }
            if ("createStatement".equals(method.getName())) {
                Statement stmt = (Statement) method.invoke(connection, params);
                String sql = removeExtraWhitespace((String) params[0]);
                return SQLAuditingStatementLogger.newInstance(sql, stmt, statementLog, queryStack);
            }
            return method.invoke(connection, params);
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * return the wrapped connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

}
