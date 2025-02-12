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

package cn.mybatis.mp.core.mybatis.mapper.mappers.basicMapper;

import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.db.reflect.Tables;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.MapWithKeyMapperUtil;
import cn.mybatis.mp.core.sql.util.QueryUtil;
import cn.mybatis.mp.core.sql.util.WhereUtil;
import db.sql.api.GetterFun;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.tookit.LambdaUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public interface MapWithKeyBasicMapper extends BaseBasicMapper {

    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey map的key
     * @param ids    ids
     * @param <K>    map的key的类型
     * @return 一个map
     */
    default <T, K, ID extends Serializable> Map<K, T> mapWithKey(GetterFun<T, K> mapKey, ID... ids) {
        if (Objects.isNull(ids) || ids.length < 1) {
            return Collections.emptyMap();
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(mapKey);
        return MapWithKeyMapperUtil.mapWithKey(getBasicMapper(), Tables.get(lambdaFieldInfo.getType()), lambdaFieldInfo.getName(), ids);
    }

    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey map的key
     * @param ids    ids
     * @param <K>    map的key的类型
     * @return 一个map
     */
    default <T, K, ID extends Serializable> Map<K, T> mapWithKey(GetterFun<T, K> mapKey, Collection<ID> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(mapKey);
        return MapWithKeyMapperUtil.mapWithKey(getBasicMapper(), Tables.get(lambdaFieldInfo.getType()), lambdaFieldInfo.getName(), ids);
    }


    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey   map的key
     * @param consumer where consumer
     * @param <K>      map的key的类型
     * @return 一个map
     */
    default <T, K> Map<K, T> mapWithKey(GetterFun<T, K> mapKey, Consumer<Where> consumer) {
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(mapKey);
        TableInfo tableInfo = Tables.get(lambdaFieldInfo.getType());
        return getBasicMapper().mapWithKey(lambdaFieldInfo.getName(), QueryUtil.buildNoOptimizationQuery(tableInfo, WhereUtil.create(tableInfo, consumer)));
    }

    /**
     * 以主键为key的查询
     *
     * @param entityType 实体类
     * @param ids        指定ID
     * @param <ID>
     * @param <T>
     * @return 以主键为key的MAP
     */
    default <ID extends Serializable, T> Map<ID, T> map(Class<T> entityType, ID... ids) {
        return MapWithKeyMapperUtil.map(getBasicMapper(), Tables.get(entityType), ids);
    }

    /**
     * 以主键为key的查询
     *
     * @param entityType 实体类
     * @param ids        指定ID
     * @param <ID>
     * @param <T>
     * @return 以主键为key的MAP
     */
    default <ID extends Serializable, T> Map<ID, T> map(Class<T> entityType, Collection<ID> ids) {
        return MapWithKeyMapperUtil.map(getBasicMapper(), Tables.get(entityType), ids);
    }
}
