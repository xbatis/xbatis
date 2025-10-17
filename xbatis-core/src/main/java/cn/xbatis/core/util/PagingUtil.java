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

package cn.xbatis.core.util;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.MappedStatementUtil;
import cn.xbatis.core.mybatis.provider.PagingCountSqlSource;
import cn.xbatis.core.mybatis.provider.PagingListSqlSource;
import cn.xbatis.db.annotations.Paging;
import cn.xbatis.page.IPager;
import cn.xbatis.page.PageUtil;
import cn.xbatis.page.PagerField;
import db.sql.api.DbType;
import db.sql.api.impl.paging.OracleRowNumPagingProcessor;
import db.sql.api.impl.paging.SQLServerRowNumberOverPagingProcessor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public final class PagingUtil {

    public static void handleMappedStatement(MappedStatement ms) {
        if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }

        Method mapperMethod = MappedStatementUtil.getMethod(ms);
        if (Objects.isNull(mapperMethod)) {
            return;
        }

        if (mapperMethod.isDefault() || Modifier.isStatic(mapperMethod.getModifiers())) {
            return;
        }

        if (!mapperMethod.isAnnotationPresent(Paging.class)) {
            return;
        }

        Paging paging = mapperMethod.getAnnotation(Paging.class);
        if (IPager.class.isAssignableFrom(mapperMethod.getReturnType())) {
            addPagingCountMappedStatement(ms, paging);
        }
        addPagingListMappedStatement(ms, mapperMethod);
    }

    private static void addPagingListMappedStatement(MappedStatement ms, Method mapperMethod) {
        String id = ms.getId() + "&list";
        Class returnType = ms.getResultMaps().get(0).getType();

        ResultMap resultMap;
        if (IPager.class.isAssignableFrom(returnType)) {
            resultMap = new ResultMap.Builder(ms.getConfiguration(), id + "-inline", GenericUtil.getGenericParameterTypes(mapperMethod).get(0), Collections.emptyList()).build();
        } else {
            resultMap = ms.getResultMaps().get(0);
        }

        SqlSource sqlSource = new PagingListSqlSource(ms.getConfiguration(), ms.getSqlSource());
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), id, sqlSource, ms.getSqlCommandType())
                .resource(ms.getResource())
                .resultMaps(Collections.singletonList(resultMap))
                .parameterMap(ms.getParameterMap())
                .keyGenerator(ms.getKeyGenerator())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .lang(ms.getLang())
                .timeout(ms.getTimeout())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());
        MappedStatement newMappedStatement = msBuilder.build();
        try {
            ms.getConfiguration().addMappedStatement(newMappedStatement);
        } catch (IllegalArgumentException e) {
            ms.getStatementLog().warn(e.getMessage());
        }
    }

    private static void addPagingCountMappedStatement(MappedStatement ms, Paging paging) {
        String id = ms.getId() + "&count";

        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), id + "-inline", Integer.class, Collections.emptyList()).build();
        List<ResultMap> resultMaps = Collections.singletonList(resultMap);

        SqlSource sqlSource = new PagingCountSqlSource(ms.getConfiguration(), ms.getSqlSource(), paging.optimize());
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), id, sqlSource, ms.getSqlCommandType())
                .resource(ms.getResource())
                .resultMaps(resultMaps)
                .parameterMap(ms.getParameterMap())
                .keyGenerator(ms.getKeyGenerator())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .lang(ms.getLang())
                .timeout(ms.getTimeout())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());
        MappedStatement newMappedStatement = msBuilder.build();
        try {
            ms.getConfiguration().addMappedStatement(newMappedStatement);
        } catch (IllegalArgumentException e) {
            ms.getStatementLog().warn(e.getMessage());
        }
    }


    public static String getLimitedSQL(DbType dbType, IPager<?> pager, String sql) {
        Integer number = pager.get(PagerField.NUMBER);
        Integer size = pager.get(PagerField.SIZE);
        if (size < 0) {
            return sql;
        }
        int offset = PageUtil.getOffset(number, size);

        if (dbType == DbType.ORACLE && XbatisGlobalConfig.getPagingProcessor(dbType) instanceof OracleRowNumPagingProcessor) {
            return getOracleRowNumLimitedSQL(size, offset, sql);
        }

        if (dbType == DbType.SQL_SERVER && XbatisGlobalConfig.getPagingProcessor(dbType) instanceof SQLServerRowNumberOverPagingProcessor) {
            return getSQLServerRowNumLimitedSQL(size, offset, sql);
        }

        if (dbType == DbType.SQL_SERVER) {
            return sql + " OFFSET " + offset + " ROWS FETCH NEXT " + size + " ROWS ONLY";
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM (");
        sqlBuilder.append(sql).append(") T ");
        if (dbType == DbType.ORACLE) {
            sqlBuilder.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(size).append(" ROWS ONLY");
            return sqlBuilder.toString();
        }
        sqlBuilder.append(" LIMIT ").append(size).append(" OFFSET ").append(offset);
        return sqlBuilder.toString();
    }

    private static String getOracleRowNumLimitedSQL(Integer size, int offset, String sql) {
        return "SELECT *  FROM ( SELECT IT.*,ROWNUM R$N FROM (" +
                sql + ") IT WHERE ROWNUM <= " +
                (size + offset) +
                ") NT WHERE NT.R$N  >" + offset;
    }

    private static String getSQLServerRowNumLimitedSQL(Integer size, int offset, String sql) {
        String upperCaseSql = sql.toUpperCase();
        int formIndex = upperCaseSql.toUpperCase().indexOf("FROM");
        String selectSql = sql.substring(0, formIndex) + ",ROW_NUMBER() OVER(";
        String middleSql = sql.substring(formIndex, upperCaseSql.length());
        int orderByIndex = middleSql.toUpperCase().lastIndexOf("ORDER BY");
        String orderBy;
        if (orderByIndex != -1) {
            orderBy = middleSql.substring(orderByIndex);
            middleSql = middleSql.substring(0, orderByIndex);
        } else {
            orderBy = " ORDER BY CURRENT_TIMESTAMP";
        }
        selectSql = selectSql + orderBy + ") R$N ";

        return "SELECT TOP " + size + " * FROM  ( " + selectSql + middleSql + " ) T WHERE R$N > " + offset;
    }

    private static String removeOrderBy(String sql, boolean optimize) {
        if (optimize) {
            String upperCaseSql = sql.toUpperCase();
            //移除最外层的order by
            int orderByIndex = upperCaseSql.lastIndexOf("ORDER BY");
            if (orderByIndex > 0) {
                if (upperCaseSql.indexOf("OFFSET", orderByIndex + 1) > 0 || upperCaseSql.indexOf("LIMIT", orderByIndex + 1) > 0) {
                    //后面有 分页 不处理
                    return sql;
                }

                Stack<Character> stack = new Stack<>();
                for (int i = orderByIndex + "ORDER BY".length() + 1; i < sql.length(); i++) {
                    char ch = sql.charAt(i);
                    if (ch == '(') {
                        stack.push('(');
                    } else if (ch == ')') {
                        if (stack.isEmpty() || stack.pop() != '(') {
                            //后面有 ), 不进行 order by剔除
                            return sql;
                        }
                    }
                }
                if (!stack.isEmpty()) {
                    //后面有 ), 不进行 order by剔除
                    return sql;
                }
                sql = sql.substring(0, orderByIndex + (sql.length() - upperCaseSql.length()));
            }
        }
        return sql;
    }

    public static String getCountSQL(DbType dbType, String sql, boolean optimize) {
        if (dbType == DbType.SQL_SERVER) {
            //sql server 必须移除order by
            optimize = true;
        }
        return "SELECT COUNT(*) FROM (" + removeOrderBy(sql, optimize) + ") T";
    }

}
