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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;

    public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = null;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public Type getRawType() {
        return this.rawType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
            if (!Arrays.equals(this.actualTypeArguments, that.actualTypeArguments)) {
                return false;
            } else {
                if (this.ownerType != null) {
                    if (this.ownerType.equals(that.ownerType)) {
                        return Objects.equals(this.rawType, that.rawType);
                    }
                } else if (that.ownerType == null) {
                    return Objects.equals(this.rawType, that.rawType);
                }

                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.actualTypeArguments != null ? Arrays.hashCode(this.actualTypeArguments) : 0;
        result = 31 * result + (this.ownerType != null ? this.ownerType.hashCode() : 0);
        result = 31 * result + (this.rawType != null ? this.rawType.hashCode() : 0);
        return result;
    }
}
