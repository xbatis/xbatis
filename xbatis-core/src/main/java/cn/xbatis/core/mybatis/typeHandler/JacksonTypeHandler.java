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


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Collection;
import java.util.Objects;

public class JacksonTypeHandler extends AbstractJsonTypeHandler {

    private static volatile ObjectMapper OBJECT_MAPPER;

    protected final JavaType javaType;

    public JacksonTypeHandler(Class<?> type) {
        super(type);
        javaType = getObjectMapper().constructType(this.type);
    }

    public JacksonTypeHandler(Class<?> type, Class<?> genericType) {
        super(type, genericType);
        TypeFactory typeFactory = getObjectMapper().getTypeFactory();
        if (this.genericType == null) {
            javaType = typeFactory.constructType(this.type);
        } else if (Collection.class.isAssignableFrom(this.type)) {
            Class<? extends Collection> collectionClass = (Class<? extends Collection>) this.type;
            javaType = typeFactory.constructCollectionType(collectionClass, typeFactory.constructType(this.genericType));
        } else if (this.type.isArray()) {
            javaType = getObjectMapper().getTypeFactory().constructArrayType(typeFactory.constructType(this.genericType));
        } else {
            javaType = getObjectMapper().constructType(this.type);
        }
    }

    private ObjectMapper getObjectMapper() {
        if (Objects.isNull(OBJECT_MAPPER)) {
            OBJECT_MAPPER = new ObjectMapper();
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return OBJECT_MAPPER;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper);
        OBJECT_MAPPER = objectMapper;
    }

    @Override
    protected String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object parseJson(String json) {
        try {
            return getObjectMapper().readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
