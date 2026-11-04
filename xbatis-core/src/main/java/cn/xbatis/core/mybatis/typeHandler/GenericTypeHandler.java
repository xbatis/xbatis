/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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


import org.apache.ibatis.type.BaseTypeHandler;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;


/**
 * GenericTypeHandler，泛型基础 TypeHandler
 *
 * @param <T>
 */
public abstract class GenericTypeHandler<T> extends BaseTypeHandler<T> {

    /**
     * 目标类
     */
    protected final Type type;

    public GenericTypeHandler(Type type) {
        this.type = type;
    }

    protected GenericTypeHandler(Class<?> clazz) {
        this.type = clazz;
    }

    protected GenericTypeHandler(Class<?> clazz, Class<?> genericType) {
        if (genericType == null) {
            this.type = clazz;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            this.type = new ParameterizedTypeImpl(clazz, genericType);
        } else if (Map.class.isAssignableFrom(clazz)) {
            this.type = new ParameterizedTypeImpl(clazz, genericType);
        } else if (clazz.isArray()) {
            this.type = Array.newInstance(clazz, 0).getClass();
        } else {
            this.type = clazz;
        }
    }

    public Type getType() {
        return type;
    }
}
