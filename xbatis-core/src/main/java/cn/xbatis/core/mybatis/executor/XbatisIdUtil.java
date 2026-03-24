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

import db.sql.api.IDbType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

public final class XbatisIdUtil {
    public final static String MAPPED_STATEMENT_DB_KEY_NAME = "(dbType)";

    public static String convertNewStatementIdPath(Class<?> returnType, MappedStatement ms, Object parameterObject, BoundSql boundSql) {
        IDbType dbType = MappedStatementUtil.getDbType(ms.getConfiguration(), parameterObject, boundSql);
        return returnType.getName() + "-" + dbType.getName() + "-" + MAPPED_STATEMENT_DB_KEY_NAME + "@" + ms.getId();
    }

    public static String convertResultMapIdPath(String str) {
        return "x-" + str;
    }
}
