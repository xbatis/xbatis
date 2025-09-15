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

package cn.xbatis.core.mybatis.mapper.mappers.basicMapper;

import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.mybatis.mapper.mappers.utils.PagingMethodUtil;
import cn.xbatis.page.IPager;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public interface BasicPagingMapper extends BasicBaseMapper {

    /**
     * 分页查询
     *
     * @param entityType 实体类
     * @param pager      分页参数
     * @return 分页结果
     */
    default <T, P extends IPager<T>> P paging(Class<T> entityType, P pager) {
        return this.paging(entityType, pager, (Where) null, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param entityType 实体类
     * @param consumer   where consumer
     * @param pager      分页参数
     * @return 分页结果
     */
    default <T, P extends IPager<T>> P paging(Class<T> entityType, P pager, Consumer<Where> consumer) {
        return this.paging(entityType, pager, consumer, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param entityType   实体类
     * @param consumer     where consumer
     * @param pager        pager
     * @param selectFields select指定列
     * @return
     */
    default <T, P extends IPager<T>> P paging(Class<T> entityType, P pager, Consumer<Where> consumer, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), Tables.get(entityType), pager, consumer, selectFields);
    }

    /**
     * 分页查询
     *
     * @param entityType 实体类
     * @param where      where
     * @param pager      分页参数
     * @return 分页结果
     */
    default <T, P extends IPager<T>> P paging(Class<T> entityType, P pager, Where where) {
        return this.paging(entityType, pager, where, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param entityType   实体类
     * @param pager
     * @param where
     * @param selectFields
     * @param <T>
     * @param <P>
     * @return
     */
    default <T, P extends IPager<T>> P paging(Class<T> entityType, P pager, Where where, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), Tables.get(entityType), pager, where, selectFields);
    }
}
