/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.mybatis.mapper.mappers.utils;

import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.mybatis.mapper.BasicMapper;
import cn.mybatis.mp.core.sql.util.QueryUtil;
import cn.mybatis.mp.core.sql.util.WhereUtil;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public final class CountMethodUtil {

    public static int count(BasicMapper basicMapper, TableInfo tableInfo, Consumer<Where> consumer) {
        return count(basicMapper, tableInfo, WhereUtil.create(tableInfo, consumer));
    }

    public static int count(BasicMapper basicMapper, TableInfo tableInfo, Where where) {
        return basicMapper.count(QueryUtil.buildNoOptimizationQuery(tableInfo, where, q -> {
            q.selectCount1();
            QueryUtil.fillQueryDefault(q, tableInfo);
        }));
    }

    public static int countAll(BasicMapper basicMapper, TableInfo tableInfo) {
        return basicMapper.count(QueryUtil.buildNoOptimizationQuery(tableInfo, q -> {
            q.selectCount1();
            QueryUtil.fillQueryDefault(q, tableInfo);
        }));
    }
}
