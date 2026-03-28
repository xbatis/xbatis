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

package db.sql.api.cmd.executor.method.condition;

import db.sql.api.Getter;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.executor.method.condition.compare.IInGetterCompare;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IInMethod<RV, COLUMN> extends IInGetterCompare<RV> {

    RV in(COLUMN column, IQuery query);

    RV in(COLUMN column, Serializable... values);

    RV in(COLUMN column, Collection<? extends Serializable> values);


    /**
     * 多列in操作方法
     *
     * @param list    数据集合，里面必须是实体类数据
     * @param getters 列的getter方法
     * @param <T>     实体类类型
     * @return 自己
     */

    default <T> RV in(List<T> list, Getter<T>... getters) {
        return in(list, 1, getters);
    }

    /**
     * 多列in操作方法
     *
     * @param when    当true 才生效
     * @param list    数据集合，里面必须是实体类数据
     * @param getters 列的getter方法
     * @param <T>     实体类类型
     * @return 自己
     */
    default <T> RV in(boolean when, List<T> list, Getter<T>... getters) {
        if (when) {
            return (RV) this;
        }
        return in(list, 1, getters);
    }

    /**
     * 多列in操作方法
     *
     * @param list    数据集合，里面必须是实体类数据
     * @param storey  实体类的层级
     * @param getters 列的getter方法
     * @param <T>     实体类类型
     * @return 自己
     */
    <T> RV in(List<T> list, int storey, Getter<T>... getters);

    /**
     * 多列in操作方法
     *
     * @param when    当true 才生效
     * @param list    数据集合，里面必须是实体类数据
     * @param storey  实体类的层级
     * @param getters 列的getter方法
     * @param <T>     实体类类型
     * @return 自己
     */
    default <T> RV in(boolean when, List<T> list, int storey, Getter<T>... getters) {
        if (when) {
            return (RV) this;
        }
        return in(list, storey, getters);
    }
}
