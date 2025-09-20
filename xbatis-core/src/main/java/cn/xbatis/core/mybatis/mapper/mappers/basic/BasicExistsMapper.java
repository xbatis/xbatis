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

package cn.xbatis.core.mybatis.mapper.mappers.basic;

import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.mappers.utils.ExistsMethodUtil;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public interface BasicExistsMapper extends BasicBaseMapper {

    /**
     * 是否存在
     *
     * @param entityType 实体类
     * @param consumer   where consumer
     * @return 是否存在
     */
    default <T> boolean exists(Class<T> entityType, Consumer<Where> consumer) {
        return ExistsMethodUtil.exists(getBasicMapper(), Tables.get(entityType), consumer);
    }

    /**
     * 是否存在
     *
     * @param entityType 实体类
     * @param where
     * @return 是否存在
     */
    default <T> boolean exists(Class<T> entityType, Where where) {
        return ExistsMethodUtil.exists(getBasicMapper(), Tables.get(entityType), where);
    }
}
