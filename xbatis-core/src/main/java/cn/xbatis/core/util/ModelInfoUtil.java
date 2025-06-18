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

import cn.xbatis.core.db.reflect.*;
import cn.xbatis.db.Model;

import java.io.Serializable;

public final class ModelInfoUtil {
    public static void setValue(ModelFieldInfo modelFieldInfo, Object target, Object value) {
        try {
            modelFieldInfo.getWriteFieldInvoker().invoke(target, new Object[]{value});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从实体类的Model类中获取ID
     *
     * @param model 实体类的model类实例
     * @return 返回ID
     */
    public static Serializable getModelIdValue(Model model) {
        return getModelIdValue(Models.get(model.getClass()), model, false);
    }

    /**
     * 从实体类中获取ID
     *
     * @param modelInfo Model类信息
     * @param model     实体类的model类实例
     * @return 返回ID
     */
    public static Serializable getModelIdValue(ModelInfo modelInfo, Model model) {
        return getModelIdValue(modelInfo, model, true);
    }

    /**
     * 从实体类中获取ID
     *
     * @param modelInfo Model类信息
     * @param model     实体类的model类实例
     * @param check     是否检查
     * @return 返回ID
     */
    public static Serializable getModelIdValue(ModelInfo modelInfo, Model model, boolean check) {
        return getModelIdValue(modelInfo, Tables.get(modelInfo.getEntityType()), model, check);
    }

    /**
     * 从Model类中获取ID
     *
     * @param modelInfo Model类信息
     * @param model     实体类的model类实例
     * @return 返回ID
     */
    public static Serializable getModelIdValue(ModelInfo modelInfo, TableInfo tableInfo, Model model, boolean check) {
        if (check) {
            if (model.getClass() != modelInfo.getType()) {
                throw new RuntimeException("Not Supported");
            }
        }
        TableInfoUtil.checkId(tableInfo);
        Serializable id;
        try {
            id = (Serializable) modelInfo.getSingleIdFieldInfo(true).getReadFieldInvoker().invoke(model, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static boolean isInsertDoBeforeTableField(ModelFieldInfo modelFieldInfo) {
        return TableInfoUtil.isCommonInsertDoBeforeTableField(modelFieldInfo.getTableFieldInfo());
    }

    public static boolean isUpdateDoBeforeTableField(ModelFieldInfo modelFieldInfo) {
        return TableInfoUtil.isCommonUpdateDoBeforeTableField(modelFieldInfo.getTableFieldInfo());
    }
}
