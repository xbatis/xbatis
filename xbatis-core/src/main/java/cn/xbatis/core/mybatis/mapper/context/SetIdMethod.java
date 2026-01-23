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

package cn.xbatis.core.mybatis.mapper.context;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

public interface SetIdMethod {
    /**
     * 设置插入ID
     *
     * @param id    ID的值
     * @param index 第几个
     */
    void setId(Object id, int index);

    /**
     * ID是否有值
     *
     * @return 是否有值
     */
    boolean idHasValue();

    /**
     * 插入的个数
     *
     * @return 个数
     */
    int getInsertSize();

    /**
     * 获取插入的实例
     *
     * @param index 参数的索引
     * @return 插入的对象
     */
    Object getInsertData(int index);

    /**
     * 获取ID的TypeHandler
     *
     * @return TypeHandler实例
     */
    TypeHandler<?> getIdTypeHandler(Configuration configuration);

    /**
     * 获取ID的列名
     *
     * @return ID的列名
     */
    String getIdColumnName();
}
