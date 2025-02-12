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


import org.apache.ibatis.type.BaseTypeHandler;

import java.lang.reflect.Type;


/**
 * GenericTypeHandler，泛型基础 TypeHandler
 *
 * @param <T>
 */
public abstract class GenericTypeHandler<T> extends BaseTypeHandler<T> {

    /**
     * 目标类
     */
    protected final Class<?> type;

    /**
     * 目标字段上的泛型
     */
    protected final Type genericType;

    public GenericTypeHandler(Class<?> type) {
        this(type, null);
    }

    public GenericTypeHandler(Class<?> type, Type genericType) {
        this.type = type;
        this.genericType = genericType;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }
}
