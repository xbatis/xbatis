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

package cn.xbatis.core.util;


import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import org.apache.ibatis.javassist.util.proxy.Proxy;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class UpdateProxyUtil {

    public static <T> T of(T instance, Consumer<String> consumer) {
        if (instance == null) {
            throw new RuntimeException("can't be null");
        }
        ProxyFactory f = new ProxyFactory();
        f.setSuperclass(instance.getClass());
        f.setFilter(m -> {
            String methodName = m.getName();
            if (!methodName.startsWith("set")) {
                return false;
            }
            return Modifier.isPublic(m.getModifiers());
        });
        Class c = f.createClass();
        T obj;
        try {
            obj = (T) c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableInfo tableInfo = Tables.get(instance.getClass());
        ((Proxy) obj).setHandler((Object self, Method m, Method proceed, Object[] args) -> {

            String methodName = m.getName();
            String fieldName;
            if (methodName.startsWith("set") && methodName.length() > 3) {
                fieldName = NamingUtil.firstToLower(methodName.substring(3));
                consumer.accept(fieldName);
                tableInfo.getFieldInfo(fieldName).getWriteFieldInvoker().invoke(instance, args);
            }

            return null;
        });
        return obj;
    }
}
