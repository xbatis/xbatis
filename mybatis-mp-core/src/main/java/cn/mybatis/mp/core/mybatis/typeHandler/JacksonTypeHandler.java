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

package cn.mybatis.mp.core.mybatis.typeHandler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.Objects;

public class JacksonTypeHandler extends AbstractJsonTypeHandler {

    private static volatile ObjectMapper OBJECT_MAPPER;

    public JacksonTypeHandler(Class<?> type) {
        super(type);
    }

    public JacksonTypeHandler(Class<?> type, Type genericType) {
        super(type, genericType);
    }

    private ObjectMapper getObjectMapper() {
        if (Objects.isNull(OBJECT_MAPPER)) {
            OBJECT_MAPPER = new ObjectMapper();
        }
        return OBJECT_MAPPER;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper);
        OBJECT_MAPPER = objectMapper;
    }

    @Override
    String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Object parseJson(String json) {
        ObjectMapper objectMapper = getObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(this.getDeserializeType());
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

}
