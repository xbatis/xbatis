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

package cn.xbatis.core.mybatis.mapper.mappers.utils;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.util.QueryUtil;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public final class ExistsMethodUtil {

    public static boolean exists(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer) {
        return exists(basicMapper, tableInfo, WhereUtil.create(tableInfo, consumer));
    }

    public static boolean exists(BasicMapper basicMapper, TableInfo tableInfo, Where where) {
        return basicMapper.exists(QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> q.from(tableInfo.getType()).select1()));
    }
}
