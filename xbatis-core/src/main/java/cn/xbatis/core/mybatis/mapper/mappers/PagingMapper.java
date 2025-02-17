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

import cn.xbatis.core.mybatis.mapper.mappers.utils.PagingMethodUtil;
import cn.xbatis.page.IPager;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public interface PagingMapper<T> extends BaseMapper<T> {

    /**
     * 分页查询
     *
     * @param consumer where consumer
     * @param pager    分页参数
     * @return 分页结果
     */
    default <P extends IPager<T>> P paging(P pager, Consumer<Where> consumer) {
        return this.paging(pager, consumer, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param consumer     where consumer
     * @param pager        pager
     * @param selectFields select指定列
     * @return
     */
    default <P extends IPager<T>> P paging(P pager, Consumer<Where> consumer, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), getTableInfo(), pager, consumer, selectFields);
    }

    /**
     * 分页查询
     *
     * @param where where
     * @param pager 分页参数
     * @return 分页结果
     */
    default <P extends IPager<T>> P paging(P pager, Where where) {
        return this.paging(pager, where, (Getter<T>[]) null);
    }

    default <P extends IPager<T>> P paging(P pager, Where where, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), getTableInfo(), pager, where, selectFields);
    }
}
