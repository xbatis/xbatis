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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.executor.BaseQuery;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.executor.Executor;

import java.util.Map;

public class XbatisContextUtil {

    public static BaseQuery<?, ?> getQueryExecution(Object parameterObject) {
        Executor executor = getExecution(parameterObject);
        if (executor instanceof BaseQuery) {
            return (BaseQuery) executor;
        }
        return null;
    }

    public static Executor getExecution(Object parameterObject) {
        if (parameterObject instanceof SQLCmdContext) {
            return ((SQLCmdContext) parameterObject).getExecution();
        }
        if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
            if (parameterMap.containsKey("execution")) {
                Object execution = parameterMap.get("execution");
                if (execution != null && execution instanceof Executor) {
                    return (Executor) execution;
                }
            }
        }
        return null;
    }

    public static DbType getDbType(Object parameterObject) {
        if (parameterObject instanceof SQLCmdQueryContext) {
            return ((SQLCmdQueryContext) parameterObject).getDbType();
        }
        if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
            if (parameterMap.containsKey("dbType")) {
                Object dbType = parameterMap.get("dbType");
                if (dbType != null && dbType instanceof DbType) {
                    return (DbType) dbType;
                }
            }
        }
        return null;
    }

    public static BasicMapper getBasicMapper(Object parameterObject) {
        if (parameterObject instanceof BaseSQLCmdContext) {
            return ((BaseSQLCmdContext) parameterObject).$getBasicMapper();
        }
        if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
            if (parameterMap.containsKey("basicMapper")) {
                Object basicMapper = parameterMap.get("basicMapper");
                if (basicMapper != null && basicMapper instanceof BasicMapper) {
                    return (BasicMapper) basicMapper;
                }
            }
        }
        return null;
    }
}
