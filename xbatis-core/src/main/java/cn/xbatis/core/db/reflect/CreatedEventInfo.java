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

package cn.xbatis.core.db.reflect;

import cn.xbatis.db.annotations.CreatedEvent;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

@Data
public class CreatedEventInfo {

    private final Class clazz;

    private final CreatedEvent annotation;

    private final Method method;

    private final boolean hasContextParam;

    public CreatedEventInfo(Class clazz, CreatedEvent annotation) {
        this.clazz = clazz;
        this.annotation = annotation;
        Method method;
        boolean hasContextParam;
        try {
            method = annotation.value().getDeclaredMethod("onCreatedEvent", Map.class, clazz);
            hasContextParam = true;
        } catch (NoSuchMethodException e) {
            try {
                method = annotation.value().getDeclaredMethod("onCreatedEvent", clazz);
                hasContextParam = false;
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.method = method;
        this.hasContextParam = hasContextParam;
    }
}
