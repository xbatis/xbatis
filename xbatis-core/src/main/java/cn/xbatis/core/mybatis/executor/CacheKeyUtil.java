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

import cn.xbatis.core.mybatis.mapper.context.*;
import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import org.apache.ibatis.cache.CacheKey;

import java.util.List;

public final class CacheKeyUtil {

    private static void updateAll(CacheKey cacheKey, List<Object> list) {
        for (Object obj : list) {
            cacheKey.update(obj);
        }
    }

    public static CacheKey wrap(CacheKey cacheKey, Object parameterObject) {
        if (parameterObject instanceof SQLCmdQueryContext) {
            SQLCmdQueryContext context = (SQLCmdQueryContext) parameterObject;
            updateAll(cacheKey, context.getParameters());
            if (context.getExecution().getReturnType() != null) {
                cacheKey.update(context.getExecution().getReturnType().getName());
            }
        } else if (parameterObject instanceof ExecuteAndSelectPreparedContext) {
            ExecuteAndSelectPreparedContext context = (ExecuteAndSelectPreparedContext) parameterObject;
            if (context.getParameters() != null) {
                updateAll(cacheKey, context.getParameters());
            }
            if (context.getReturnType() != null) {
                cacheKey.update(context.getReturnType().getName());
            }
            cacheKey.update(System.currentTimeMillis());
        } else if (parameterObject instanceof SelectPreparedContext) {
            SelectPreparedContext context = (SelectPreparedContext) parameterObject;
            if (context.getParameters() != null) {
                updateAll(cacheKey, context.getParameters());
            }
            if (context.getReturnType() != null) {
                cacheKey.update(context.getReturnType().getName());
            }
        } else if (parameterObject instanceof SQLCmdUpdateContext) {
            SQLCmdUpdateContext context = (SQLCmdUpdateContext) parameterObject;
            updateAll(cacheKey, context.getParameters());
            if (context.getExecution() instanceof UpdateChain) {
                UpdateChain updateChain = (UpdateChain) context.getExecution();
                if (updateChain.getReturnType() != null) {
                    cacheKey.update(updateChain.getReturnType().getName());
                }
            }
            cacheKey.update(System.currentTimeMillis());
        } else if (parameterObject instanceof SQLCmdDeleteContext) {
            SQLCmdDeleteContext context = (SQLCmdDeleteContext) parameterObject;
            updateAll(cacheKey, context.getParameters());
            if (context.getExecution() instanceof DeleteChain) {
                DeleteChain deleteChain = (DeleteChain) context.getExecution();
                if (deleteChain.getReturnType() != null) {
                    cacheKey.update(deleteChain.getReturnType().getName());
                }
            }
            cacheKey.update(System.currentTimeMillis());
        }
        return cacheKey;
    }
}
