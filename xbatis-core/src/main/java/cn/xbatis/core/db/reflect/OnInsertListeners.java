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

import cn.xbatis.listener.OnInsertListener;
import cn.xbatis.listener.annotations.OnInsert;

public class OnInsertListeners {

    private OnInsertListeners() {

    }


    /**
     * 获取实体类、Model类的的OnInsert监听器
     *
     * @param clazz
     * @return OnInsertListener<T>
     */

    public static <T> OnInsertListener<T> get(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(OnInsert.class)) {
            return null;
        }
        OnInsert annotation = clazz.getAnnotation(OnInsert.class);
        return OnListenerUtil.getListener(annotation.value());
    }
}
