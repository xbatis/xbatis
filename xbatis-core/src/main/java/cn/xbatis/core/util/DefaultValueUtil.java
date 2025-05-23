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

package cn.xbatis.core.util;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.db.reflect.ModelFieldInfo;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.db.Model;
import db.sql.api.impl.tookit.Objects;

import java.util.Map;

/**
 * 默认值 util
 */
public class DefaultValueUtil {

    public static Object getAndSetDefaultValue(Object entity, TableFieldInfo tableFieldInfo, Map<String, Object> context) {
        Object value = getDefaultValue(tableFieldInfo, tableFieldInfo.getFieldInfo().getTypeClass(), context);
        if (Objects.nonNull(value)) {
            //默认值回写
            TableInfoUtil.setValue(tableFieldInfo, entity, value);
        }
        return value;
    }

    public static Object getAndSetUpdateDefaultValue(Object entity, TableFieldInfo tableFieldInfo, Map<String, Object> context) {
        Object value = getUpdateDefaultValue(tableFieldInfo, tableFieldInfo.getFieldInfo().getTypeClass(), context);
        if (Objects.nonNull(value)) {
            //默认值回写
            TableInfoUtil.setValue(tableFieldInfo, entity, value);
        }
        return value;
    }

    public static <M extends Model<T>, T> Object getAndSetDefaultValue(M model, ModelFieldInfo modelFieldInfo, Map<String, Object> context) {
        Object value = getDefaultValue(modelFieldInfo.getTableFieldInfo(), modelFieldInfo.getFieldInfo().getTypeClass(), context);
        if (Objects.nonNull(value)) {
            //默认值回写
            ModelInfoUtil.setValue(modelFieldInfo, model, value);
        }
        return value;
    }

    public static <M extends Model<T>, T> Object getAndSetUpdateDefaultValue(M model, ModelFieldInfo modelFieldInfo, Map<String, Object> context) {
        Object value = getUpdateDefaultValue(modelFieldInfo.getTableFieldInfo(), modelFieldInfo.getFieldInfo().getTypeClass(), context);
        if (Objects.nonNull(value)) {
            //默认值回写
            ModelInfoUtil.setValue(modelFieldInfo, model, value);
        }
        return value;
    }

    private static Object getDefaultValue(TableFieldInfo tableFieldInfo, Class<?> defaultValueType, Map<String, Object> context) {
        if (StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().defaultValue())) {
            return null;
        }
        return XbatisGlobalConfig.getDefaultValue(tableFieldInfo.getFieldInfo().getClazz(), defaultValueType, tableFieldInfo.getTableFieldAnnotation().defaultValue(), context);
    }

    private static Object getUpdateDefaultValue(TableFieldInfo tableFieldInfo, Class<?> defaultValueType, Map<String, Object> context) {
        if (StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().updateDefaultValue())) {
            return null;
        }
        return XbatisGlobalConfig.getDefaultValue(tableFieldInfo.getFieldInfo().getClazz(), defaultValueType, tableFieldInfo.getTableFieldAnnotation().updateDefaultValue(), context);
    }

}
