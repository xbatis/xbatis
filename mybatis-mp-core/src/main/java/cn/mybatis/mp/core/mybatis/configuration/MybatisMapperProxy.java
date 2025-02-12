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

import cn.mybatis.mp.core.MybatisMpConfig;
import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.mybatis.mapper.BasicMapper;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class MybatisMapperProxy<T> extends BaseMapperProxy<T> {

    public final static String ENTITY_TYPE_METHOD_NAME = "getEntityType";
    public final static String MAPPER_TYPE_METHOD_NAME = "getMapperType";
    public final static String TABLE_INFO_METHOD_NAME = "getTableInfo";
    public final static String GET_BASIC_MAPPER_METHOD_NAME = "getBasicMapper";

    private final Class<T> mapperInterface;
    private final Class<?> entityType;
    private final TableInfo tableInfo;
    private BasicMapper basicMapper;

    public MybatisMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map methodCache, Class<?> entityType, TableInfo tableInfo) {
        super(sqlSession, mapperInterface, methodCache);
        this.mapperInterface = mapperInterface;
        this.entityType = entityType;
        this.tableInfo = tableInfo;
    }

    private BasicMapper getBasicMapper() {
        if (Objects.isNull(basicMapper)) {
            basicMapper = sqlSession.getMapper(MybatisMpConfig.getSingleMapperClass());
        }
        return basicMapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            return super.invoke(proxy, method, args);
        }
        switch (method.getName()) {
            case ENTITY_TYPE_METHOD_NAME:
                return this.entityType;
            case MAPPER_TYPE_METHOD_NAME:
                return this.mapperInterface;
            case TABLE_INFO_METHOD_NAME:
                return this.tableInfo;
            case GET_BASIC_MAPPER_METHOD_NAME:
                return getBasicMapper();
        }

        return super.invoke(proxy, method, args);
    }
}
