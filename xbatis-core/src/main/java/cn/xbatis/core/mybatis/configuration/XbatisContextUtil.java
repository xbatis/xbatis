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

package cn.xbatis.core.mybatis.configuration;

import cn.xbatis.core.mybatis.mapper.context.SQLCmdQueryContext;
import cn.xbatis.core.sql.executor.BaseQuery;

import java.util.Map;

public class XbatisContextUtil {

    public static BaseQuery<?, ?> getExecution(Object parameterObject) {
        if (parameterObject instanceof SQLCmdQueryContext) {
            return ((SQLCmdQueryContext) parameterObject).getExecution();
        }
        if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
            if (parameterMap.containsKey("execution")) {
                Object execution = parameterMap.get("execution");
                if (execution != null && execution instanceof BaseQuery) {
                    return (BaseQuery) execution;
                }
            }
        }
        return null;
    }
}
