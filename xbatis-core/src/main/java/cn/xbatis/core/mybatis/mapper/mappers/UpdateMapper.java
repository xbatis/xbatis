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

import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.mybatis.mapper.mappers.utils.UpdateMethodUtil;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.Collection;
import java.util.function.Consumer;

public interface UpdateMapper<T> extends BaseMapper<T> {

    /**
     * 实体类修改
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    default int update(T entity, UpdateStrategy<T> updateStrategy) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy);
    }

    /**
     * 实体类修改
     *
     * @param entity 实体类对象
     * @return 影响条数
     */
    default int update(T entity) {
        return this.update(entity, false);
    }

    /**
     * 实体类修改
     *
     * @param entity        实体类对象
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    default int update(T entity, boolean allFieldForce) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce);
        });
    }

    /**
     * 实体类修改
     *
     * @param entity
     * @param forceFields 强制更新指定，解决需要修改为null的需求
     * @return 影响条数
     */
    default int update(T entity, Getter<T>... forceFields) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.forceFields(forceFields);
        });
    }

    /**
     * 动态条件修改
     *
     * @param entity 实体类
     * @param where  where
     * @return 影响条数
     */
    default int update(T entity, Consumer<Where> where) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.on(where);
        });
    }

    /**
     * 动态条件修改
     *
     * @param entity        实体类对象
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @param where         where
     * @return 影响条数
     */
    default int update(T entity, boolean allFieldForce, Consumer<Where> where) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce).on(where);
        });
    }


    /**
     * 指定where 修改
     *
     * @param entity 实体类对象
     * @param where  where
     * @return 影响条数
     */
    default int update(T entity, Where where) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.on(where);
        });
    }

    /**
     * 指定where 修改
     *
     * @param entity        实体类对象
     * @param where         where
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    default int update(T entity, boolean allFieldForce, Where where) {
        return UpdateMethodUtil.update(getBasicMapper(), getTableInfo(), entity, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce).on(where);
        });
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list           实体类对象List
     * @param updateStrategy 策略
     * @return 影响条数
     */
    default int update(Collection<T> list, UpdateStrategy<T> updateStrategy) {
        return UpdateMethodUtil.updateList(getBasicMapper(), getTableInfo(), list, updateStrategy);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list 实体类对象List
     * @return 影响条数
     */
    default int update(Collection<T> list) {
        return this.update(list, false);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list          实体类对象List
     * @param allFieldForce 是否所有字段都修改，如果是null值，则变成NULL
     * @return 影响条数
     */
    default int update(Collection<T> list, boolean allFieldForce) {
        return UpdateMethodUtil.updateList(getBasicMapper(), getTableInfo(), list, updateStrategy -> {
            updateStrategy.allFieldUpdate(allFieldForce);
        });
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list        实体类对象List
     * @param forceFields 强制更新指定，解决需要修改为null的需求
     * @return 影响条数
     */
    default int update(Collection<T> list, Getter<T>... forceFields) {
        return UpdateMethodUtil.updateList(getBasicMapper(), getTableInfo(), list, updateStrategy -> {
            updateStrategy.forceFields(forceFields);
        });
    }
}
