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

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Objects;

public class GsonTypeHandler extends AbstractJsonTypeHandler {

    private volatile static Gson GSON;

    public GsonTypeHandler(Class<?> type) {
        super(type);
    }

    public GsonTypeHandler(Class<?> type, Type genericType) {
        super(type, genericType);
    }

    public static Gson getGson() {
        if (null == GSON) {
            GSON = new Gson();
        }

        return GSON;
    }

    public static void setGson(Gson gson) {
        Objects.requireNonNull(gson);
        GSON = gson;
    }

    @Override
    protected String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    @Override
    protected Object parseJson(String json) {
        return getGson().fromJson(json, this.getDeserializeType());
    }
}
