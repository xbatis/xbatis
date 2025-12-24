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

package cn.xbatis.core.mybatis.logging;

import cn.xbatis.core.mybatis.mapper.context.XbatisContextUtil;
import db.sql.api.impl.cmd.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class XbatisLogFactory {

    public final static Log NO_LOGGER = new NoLoggingImpl(null);
    private final static Map<String, Log> LOGS = new ConcurrentHashMap<>();

    public static Log getLog(Object parameter, Log statementLog) {
        Executor executor = XbatisContextUtil.getExecution(parameter);
        if (executor == null) {
            return statementLog;
        }
        if (!(executor instanceof Loggable)) {
            return statementLog;
        }
        Loggable loggable = (Loggable) executor;
        if (!loggable.isEnableLog()) {
            return NO_LOGGER;
        }
        if (loggable.getLogger() == null || loggable.getLogger().isEmpty()) {
            return statementLog;
        }
        return getLog(loggable.getLogger());
    }

    private static Log getLog(String name) {
        return LOGS.computeIfAbsent(name, key -> LogFactory.getLog(key));
    }
}
