/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.mybatis.mapper.mappers.basicMapper;


import cn.mybatis.mp.core.db.reflect.Tables;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.SaveMethodUtil;
import cn.mybatis.mp.core.sql.executor.Insert;
import db.sql.api.Getter;

import java.util.Collection;
import java.util.Objects;

public interface SaveBasicMapper extends BaseBasicMapper {

    /**
     * 实体类新增
     *
     * @param entity
     * @return 影响条数
     */
    default <T> int save(T entity) {
        return save(entity, false);
    }

    /**
     * 实体类新增
     *
     * @param entity        实体类实例
     * @param allFieldForce 所有字段都强制保存,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int save(T entity, boolean allFieldForce) {
        return SaveMethodUtil.save(getBasicMapper(), Tables.get(entity.getClass()), entity, allFieldForce, null);
    }

    /**
     * 实体类新增
     *
     * @param entity      实体类实例
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int save(T entity, Getter<T>... forceFields) {
        return SaveMethodUtil.save(getBasicMapper(), Tables.get(entity.getClass()), entity, false, forceFields);
    }


    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @return 影响条数
     */
    default <T> int save(Collection<T> list) {
        return save(list, false);
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @param allFieldForce 所有字段都强制保存,null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int save(Collection<T> list, boolean allFieldForce) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        T first = list.stream().findFirst().get();
        return SaveMethodUtil.save(getBasicMapper(), Tables.get(first.getClass()), list, allFieldForce, (Getter<T>[]) null);
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int save(Collection<T> list, Getter<T>... forceFields) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return 0;
        }
        T first = list.stream().findFirst().get();
        return SaveMethodUtil.save(getBasicMapper(), Tables.get(first.getClass()), list, false, forceFields);
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     *
     * @param list
     * @return 影响条数
     */
    default <T> int saveBatch(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        T first = list.stream().findFirst().get();
        return SaveMethodUtil.saveBatch(getBasicMapper(), new Insert(), Tables.get(first.getClass()), list);
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     * <p>
     * 会自动加入 主键 租户ID 逻辑删除列 乐观锁
     * 自动设置 默认值,不会忽略NULL值字段
     *
     * @param list
     * @param forceFields 指定那些列强制插入，null值将会以NULL的形式插入
     * @return 影响条数
     */
    default <T> int saveBatch(Collection<T> list, Getter<T>... forceFields) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        T first = list.stream().findFirst().get();
        return SaveMethodUtil.saveBatch(getBasicMapper(), new Insert(), Tables.get(first.getClass()), list, forceFields);
    }
}
