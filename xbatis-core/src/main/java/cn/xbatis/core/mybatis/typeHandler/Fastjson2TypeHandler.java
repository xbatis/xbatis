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

package cn.xbatis.core.mybatis.typeHandler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Fastjson2TypeHandler extends AbstractJsonTypeHandler {

    protected final ParameterizedType parameterizedType;

    public Fastjson2TypeHandler(Class<?> type) {
        super(type);
        parameterizedType = new ParameterizedTypeImpl(null, null, type);
    }

    public Fastjson2TypeHandler(Class<?> type, Class<?> genericType) {
        super(type, genericType);
        if (genericType == null) {
            parameterizedType = new ParameterizedTypeImpl(null, null, type);
        } else {
            parameterizedType = new ParameterizedTypeImpl(new Type[]{genericType}, null, type);
        }
    }

    @Override
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, Feature.WriteMapNullValue);
    }

    @Override
    protected Object parseJson(String json) {
        return JSON.parseObject(json, parameterizedType);
    }
}
