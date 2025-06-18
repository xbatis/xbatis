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

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.listener.OnInsertListener;
import cn.xbatis.listener.OnUpdateListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnListenerUtil {

    private static final Map<Class, Object> CACHE = new ConcurrentHashMap<>();

    public static <T, LISTENER> LISTENER getListener(Class<T> clazz) {
        LISTENER listener = (LISTENER) CACHE.get(clazz);
        if (listener != null) {
            return listener;
        }
        synchronized (clazz) {
            listener = (LISTENER) CACHE.get(clazz);
            if (listener != null) {
                return listener;
            }
            return (LISTENER) CACHE.computeIfAbsent(clazz, key -> {
                try {
                    return clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 通知
     *
     * @param object
     */
    public static void notifyInsert(Object object) {
        if (object == null) {
            return;
        }
        OnInsertListener listener = OnInsertListeners.get(object.getClass());
        if (listener != null) {
            listener.onInsert(object);
        }
        listener = XbatisGlobalConfig.getGlobalOnInsertListener();
        if (listener != null) {
            listener.onInsert(object);
        }
    }

    /**
     * 通知
     *
     * @param object
     */
    public static void notifyUpdate(Object object) {
        if (object == null) {
            return;
        }
        OnUpdateListener listener = OnUpdateListeners.get(object.getClass());
        if (listener != null) {
            listener.onUpdate(object);
        }
        listener = XbatisGlobalConfig.getGlobalOnUpdateListener();
        if (listener != null) {
            listener.onUpdate(object);
        }
    }
}
