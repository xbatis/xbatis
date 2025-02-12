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

package cn.mybatis.mp.core.db.reflect;

import cn.mybatis.mp.core.exception.NotTableClassException;
import cn.mybatis.mp.core.util.FieldUtil;
import cn.mybatis.mp.core.util.GenericUtil;
import cn.mybatis.mp.db.annotations.Table;
import db.sql.api.impl.tookit.Objects;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelInfo {

    /**
     * 对应的类
     */
    private final Class<?> type;

    /**
     * 对应实体类的type
     */
    private final Class<?> entityType;

    /**
     * 表信息
     */
    private final TableInfo tableInfo;

    /**
     * 所有 字段
     */
    private final List<ModelFieldInfo> modelFieldInfos;

    /**
     * 字段信息 key为属性字段名 value为字段信息
     */
    private final Map<String, ModelFieldInfo> modelFieldInfoMap;

    /**
     * 字段个数
     */
    private final int fieldSize;

    /**
     * ID 字段信息
     */
    private final List<ModelFieldInfo> idFieldInfos;

    /**
     * 乐观锁字段
     */
    private final ModelFieldInfo versionFieldInfo;

    /**
     * 逻辑删除字段
     */
    private final ModelFieldInfo logicDeleteFieldInfo;

    /**
     * 多租户ID
     */
    private final ModelFieldInfo tenantIdFieldInfo;

    private final ModelFieldInfo idFieldInfo;

    public ModelInfo(Class<?> model) {
        this.type = model;
        Class<?> entity = GenericUtil.getGenericInterfaceClass(model).stream().filter(item -> item.isAnnotationPresent(Table.class)).findFirst().orElseThrow(() -> new RuntimeException(MessageFormat.format("class {0} have no generic type", model.getName())));
        this.entityType = entity;

        try {
            this.tableInfo = Tables.get(entity);
        } catch (NotTableClassException e) {
            throw new RuntimeException(MessageFormat.format("unable match model class {0} , the generic class {1} is not a entity", model.getName(), entity.getName()));
        }

        List<ModelFieldInfo> modelFieldInfos = FieldUtil.getResultMappingFields(model).stream().map(field -> new ModelFieldInfo(entity, model, field)).collect(Collectors.toList());

        this.modelFieldInfoMap = modelFieldInfos.stream().collect(Collectors.toMap((item) -> item.getField().getName(), account -> account));

        this.idFieldInfos = modelFieldInfos.stream().filter(item -> item.getTableFieldInfo().isTableId()).collect(Collectors.toList());
        this.idFieldInfo = this.idFieldInfos.size() == 1 ? this.idFieldInfos.get(0) : null;
        this.versionFieldInfo = modelFieldInfos.stream().filter(item -> item.getTableFieldInfo().isVersion()).findFirst().orElse(null);
        this.tenantIdFieldInfo = modelFieldInfos.stream().filter(item -> item.getTableFieldInfo().isTenantId()).findFirst().orElse(null);
        this.logicDeleteFieldInfo = modelFieldInfos.stream().filter(item -> item.getTableFieldInfo().isLogicDelete()).findFirst().orElse(null);

        this.modelFieldInfos = Collections.unmodifiableList(modelFieldInfos);
        this.fieldSize = this.modelFieldInfos.size();
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public ModelFieldInfo getVersionFieldInfo() {
        return versionFieldInfo;
    }

    public ModelFieldInfo getLogicDeleteFieldInfo() {
        return logicDeleteFieldInfo;
    }

    public ModelFieldInfo getTenantIdFieldInfo() {
        return tenantIdFieldInfo;
    }

    public List<ModelFieldInfo> getModelFieldInfos() {
        return modelFieldInfos;
    }

    public List<ModelFieldInfo> getIdFieldInfos() {
        return idFieldInfos;
    }

    /**
     * 根据字段名获取字段信息
     *
     * @param property
     * @return
     */
    public final ModelFieldInfo getFieldInfo(String property) {
        return modelFieldInfoMap.get(property);
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public ModelFieldInfo getSingleIdFieldInfo(boolean throwException) {
        if (throwException && Objects.isNull(this.idFieldInfo)) {
            throw new RuntimeException("Model:" + this.type + " has multi ID or non-single ID.");
        }
        return this.idFieldInfo;
    }

    public ModelFieldInfo getIdFieldInfo() {
        return idFieldInfo;
    }
}
