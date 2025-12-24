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

package cn.xbatis.core.mybatis.executor;

import cn.xbatis.core.mybatis.logging.XbatisLogFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MybatisReuseExecutor extends ReuseExecutor {

    private Log statementLog;

    public MybatisReuseExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        statementLog = XbatisLogFactory.getLog(parameter, ms.getStatementLog());
        return super.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        statementLog = XbatisLogFactory.getLog(parameter, ms.getStatementLog());
        return super.query(ms, parameter, rowBounds, resultHandler);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        statementLog = XbatisLogFactory.getLog(parameter, ms.getStatementLog());
        return super.query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        statementLog = XbatisLogFactory.getLog(parameter, ms.getStatementLog());
        return super.update(ms, parameter);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        CacheKey cacheKey = super.createCacheKey(ms, parameterObject, rowBounds, boundSql);
        return CacheKeyUtil.wrap(cacheKey, parameterObject);
    }

    @Override
    protected Connection getConnection(Log statementLog) throws SQLException {
        return super.getConnection(this.statementLog);
    }
}
