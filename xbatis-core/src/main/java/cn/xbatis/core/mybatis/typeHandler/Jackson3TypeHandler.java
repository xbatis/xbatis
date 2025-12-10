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

import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.Objects;

public class Jackson3TypeHandler extends AbstractJsonTypeHandler {

    private static volatile JsonMapper OBJECT_MAPPER;

    public Jackson3TypeHandler(Class<?> type) {
        super(type);
    }

    public Jackson3TypeHandler(Class<?> type, Type genericType) {
        super(type, genericType);
    }

    private JsonMapper getJsonMapper() {
        if (Objects.isNull(OBJECT_MAPPER)) {
            OBJECT_MAPPER = new JsonMapper();
        }
        return OBJECT_MAPPER;
    }

    public static void setJsonMapper(JsonMapper jsonMapper) {
        Objects.requireNonNull(jsonMapper);
        OBJECT_MAPPER = jsonMapper;
    }

    @Override
    protected String toJson(Object obj) {
        try {
            return getJsonMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object parseJson(String json) {
        JsonMapper jsonMapper = getJsonMapper();
        TypeFactory typeFactory = jsonMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(this.getDeserializeType());
        try {
            return jsonMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
