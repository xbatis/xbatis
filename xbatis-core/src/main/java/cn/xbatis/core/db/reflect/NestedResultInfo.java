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

import cn.xbatis.db.annotations.NestedResultEntity;

import java.lang.reflect.Field;
import java.util.List;

public class NestedResultInfo {

    /**
     * 内嵌信息
     */
    public final List<NestedResultInfo> nestedResultInfos;


    private final Field field;

    private final FieldInfo fieldInfo;
    /**
     * 目标实体类
     */
    private final Class<?> targetEntityType;
    /**
     * 实体类存储层级
     */
    private final int storey;
    /**
     * 所有的 ResultFieldInfo 不包括内嵌的
     */
    private final List<ResultFieldInfo> resultFieldInfos;

    public NestedResultInfo(Class clazz, Field field, NestedResultEntity nestedResultEntity, List<ResultFieldInfo> resultFieldInfos, List<NestedResultInfo> nestedResultInfos) {
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.targetEntityType = nestedResultEntity.target();
        this.storey = nestedResultEntity.storey();
        this.resultFieldInfos = resultFieldInfos;
        this.nestedResultInfos = nestedResultInfos;
    }

    public Field getField() {
        return field;
    }

    public int getStorey() {
        return storey;
    }

    public Class<?> getTargetEntityType() {
        return targetEntityType;
    }

    public List<ResultFieldInfo> getResultFieldInfos() {
        return resultFieldInfos;
    }

    public List<NestedResultInfo> getNestedResultInfos() {
        return nestedResultInfos;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }
}
