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

package cn.mybatis.mp.core.mybatis.configuration;

import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

public class BasicMapperProxyFactory<T> extends MapperProxyFactory<T> {

    public BasicMapperProxyFactory(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T newInstance(SqlSession sqlSession) {
        BasicMapperProxy<T> mapperProxy = new BasicMapperProxy(sqlSession, getMapperInterface(), getMethodCache());
        return this.newInstance(mapperProxy);
    }
}
