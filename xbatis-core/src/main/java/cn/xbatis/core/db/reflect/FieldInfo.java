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

import cn.xbatis.core.util.FieldUtil;
import lombok.Data;

import java.lang.reflect.Field;


@Data
public class FieldInfo {

    private Class<?> clazz;

    /**
     * 字段
     */
    private Field field;

    /**
     * 字段的类型
     */
    private Class<?> typeClass;

    /**
     * 字段的映射类型 假如是List<T>,则是T的具体类型
     */
    private Class<?> finalClass;

    /**
     * @param clazz 具体类的class，假如是继承，不能父类
     * @param field
     */
    public FieldInfo(Class clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
        this.typeClass = FieldUtil.getFieldType(clazz, field);
        this.finalClass = FieldUtil.getFieldFinalType(clazz, field);
    }
}
