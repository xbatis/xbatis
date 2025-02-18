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

package cn.xbatis.core.mybatis.mapper.mappers;

import cn.xbatis.core.mybatis.mapper.mappers.utils.CountMethodUtil;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public interface CountMapper<T> extends BaseMapper<T> {

    /**
     * 总数
     *
     * @return count数
     */
    default int countAll() {
        return CountMethodUtil.countAll(getBasicMapper(), getTableInfo());
    }

    /**
     * 是否存在
     *
     * @param consumer where consumer
     * @return count数
     */
    default int count(Consumer<Where> consumer) {
        return CountMethodUtil.count(getBasicMapper(), getTableInfo(), consumer);
    }

    /**
     * 是否存在
     *
     * @param where
     * @return count数
     */
    default int count(Where where) {
        return CountMethodUtil.count(getBasicMapper(), getTableInfo(), where);
    }
}
