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

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.BasicMapper;

public interface BaseMapper<T> {

    /**
     * 获取当前实体类
     *
     * @return Class
     */
    Class<T> getEntityType();

    /**
     * 获取当前实体类的TableInfo
     *
     * @return
     */
    TableInfo getTableInfo();

    /**
     * 获取基础Mapper
     *
     * @return BasicMapper
     */
    BasicMapper getBasicMapper();
}
