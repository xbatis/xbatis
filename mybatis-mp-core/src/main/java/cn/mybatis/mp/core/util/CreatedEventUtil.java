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

package cn.mybatis.mp.core.util;

import cn.mybatis.mp.core.db.reflect.CreatedEventInfo;
import db.sql.api.impl.tookit.Objects;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CreatedEventUtil {

    public static void onCreated(Object rowValue, Map context, CreatedEventInfo createdEventInfo) {
        if (Objects.isNull(rowValue)) {
            return;
        }
        try {
            if (createdEventInfo.isHasContextParam()) {
                createdEventInfo.getMethod().invoke(null, context, rowValue);
            } else {
                createdEventInfo.getMethod().invoke(null, rowValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
