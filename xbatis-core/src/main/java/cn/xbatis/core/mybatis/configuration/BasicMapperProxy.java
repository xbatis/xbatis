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


import cn.xbatis.core.mybatis.mapper.ShareVariableName;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;

public class BasicMapperProxy<T> extends BaseMapperProxy<T> {

    public final static String SET_SHARE_VARIABLES_MAP = "$setShareVariablesMap";

    public BasicMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map methodCache) {
        super(sqlSession, mapperInterface, methodCache);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //设置公共共享变量
        if (SET_SHARE_VARIABLES_MAP.equals(method.getName())) {
            this.shareVariables = (Map<ShareVariableName, Object>) args[0];
            return null;
        }
        return super.invoke(proxy, method, args);
    }
}
