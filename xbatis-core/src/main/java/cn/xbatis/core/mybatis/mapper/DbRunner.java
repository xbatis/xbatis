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

package cn.xbatis.core.mybatis.mapper;

import cn.xbatis.core.mybatis.mapper.context.ExecuteAndSelectPreparedContext;
import cn.xbatis.core.mybatis.mapper.context.PreparedContext;
import cn.xbatis.core.mybatis.mapper.context.SelectPreparedContext;
import cn.xbatis.core.mybatis.provider.PreparedSQLProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

public interface DbRunner {

    /**
     * 执行原生非查询类sql
     *
     * @param sql    例如 update xx set name=? where id=?
     * @param params 例如 abc ,1
     * @return 影响的数量
     */
    default int execute(String sql, Object... params) {
        return this.$execute(new PreparedContext(sql, params));
    }

    @UpdateProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL)
    int $execute(PreparedContext preparedContext);


    /**
     * 执行原生非查询类 RETURNING sql,并返回单个修改的结果
     *
     * @param returnType 返回的类型
     * @param sql        例如 update xx set name=? where id=?
     * @param params     例如 abc ,1
     * @return 影响的数量
     */
    default <T> T executeAndReturning(Class<T> returnType, String sql, Object... params) {
        return this.$executeAndReturning(new ExecuteAndSelectPreparedContext(returnType, sql, params));
    }

    /**
     * 执行原生非查询类 RETURNING sql,并返回多个修改的结果
     *
     * @param returnType 返回的类型
     * @param sql        例如 update xx set name=? where id=?
     * @param params     例如 abc ,1
     * @return 影响的数量
     */
    default <T> List<T> executeAndReturningList(Class<T> returnType, String sql, Object... params) {
        return this.$executeAndReturningList(new ExecuteAndSelectPreparedContext(returnType, sql, params));
    }

    @SelectProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL, affectData = true)
    <T> T $executeAndReturning(ExecuteAndSelectPreparedContext preparedContext);

    @SelectProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL, affectData = true)
    <T> List<T> $executeAndReturningList(ExecuteAndSelectPreparedContext preparedContext);

    /**
     * 执行原生单个查询查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 select xx from table where id=?
     * @param params     例如 1
     * @return 影响的数量
     */
    default <T> T select(Class<T> returnType, String sql, Object... params) {
        return this.$select(new SelectPreparedContext(returnType, sql, params));
    }

    @SelectProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL)
    <T> T $select(SelectPreparedContext preparedContext);

    /**
     * 执行原生List查询查询类sql
     *
     * @param returnType 返回的类型
     * @param sql        例如 select xx from table where id=?
     * @param params     例如 1
     * @return 影响的数量
     */
    default <T> List<T> selectList(Class<T> returnType, String sql, Object... params) {
        return this.$selectList(new SelectPreparedContext(returnType, sql, params));
    }

    @SelectProvider(value = PreparedSQLProvider.class, method = PreparedSQLProvider.SQL)
    <T> List<T> $selectList(SelectPreparedContext preparedContext);
}
