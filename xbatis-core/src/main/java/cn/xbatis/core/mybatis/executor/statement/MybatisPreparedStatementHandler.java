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

package cn.xbatis.core.mybatis.executor.statement;

import cn.xbatis.core.mybatis.mapper.context.SetIdMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MybatisPreparedStatementHandler extends PreparedStatementHandler {

    public MybatisPreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof SetIdMethod) {
            SetIdMethod setIdMethod = (SetIdMethod) parameterObject;
            if (setIdMethod.idHasValue()) {
                String sql = boundSql.getSql();
                if (mappedStatement.getResultSetType() == ResultSetType.DEFAULT) {
                    return connection.prepareStatement(sql);
                } else {
                    return connection.prepareStatement(sql, mappedStatement.getResultSetType().getValue(),
                            ResultSet.CONCUR_READ_ONLY);
                }
            }
        }
        return super.instantiateStatement(connection);
    }
}
