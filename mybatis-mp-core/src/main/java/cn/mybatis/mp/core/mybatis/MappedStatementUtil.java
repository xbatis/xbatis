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

package cn.mybatis.mp.core.mybatis;

import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;

public final class MappedStatementUtil {

    public static Method getMethod(MappedStatement ms) {
        String methodName;
        Class mapperClass;
        try {
            String mapperName;
            int dot = ms.getId().lastIndexOf(".");
            if (dot == -1) {
                dot = ms.getId().indexOf("@");
                String id = ms.getId().substring(dot + 1);
                dot = id.lastIndexOf("-");
                mapperName = id.substring(0, dot).replaceAll("-", ".");
                methodName = id.substring(dot + 1);
            } else {
                mapperName = ms.getId().substring(0, dot);
                methodName = ms.getId().substring(dot + 1);
            }
            mapperClass = Class.forName(mapperName);
        } catch (Exception e) {
            return null;
        }

        if (methodName.contains("&")) {
            return null;
        }

        Method mapperMethod = MethodUtil.getMethod(mapperClass, methodName);
        if (mapperMethod == null) {
            for (Class clazz : mapperClass.getInterfaces()) {
                mapperMethod = MethodUtil.getMethod(clazz, methodName);
                if (mapperMethod != null) {
                    break;
                }
            }
        }
        return mapperMethod;
    }
}
