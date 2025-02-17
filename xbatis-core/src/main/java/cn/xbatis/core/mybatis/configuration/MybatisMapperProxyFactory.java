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

package cn.xbatis.core.mybatis.configuration;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import cn.xbatis.core.util.GenericUtil;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

public class MybatisMapperProxyFactory<T> extends MapperProxyFactory<T> {

    private final Class<?> entityType;

    private final TableInfo tableInfo;

    public MybatisMapperProxyFactory(Class<T> mapperInterface) {
        super(mapperInterface);
        this.entityType = GenericUtil.getGenericInterfaceClass(mapperInterface).get(0);
        this.tableInfo = Tables.get(this.entityType);
    }

    @Override
    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> mapperProxy = new MybatisMapperProxy(sqlSession, getMapperInterface(), getMethodCache(), entityType, tableInfo);
        return this.newInstance(mapperProxy);
    }
}
