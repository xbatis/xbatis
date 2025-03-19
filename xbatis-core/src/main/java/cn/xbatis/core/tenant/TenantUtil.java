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

package cn.xbatis.core.tenant;

import cn.xbatis.core.db.reflect.*;
import cn.xbatis.core.sql.executor.MpDatasetField;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.Model;
import db.sql.api.impl.cmd.struct.ConditionChain;

import java.io.Serializable;
import java.util.Objects;

public final class TenantUtil {

    public static Serializable getTenantId() {
        return TenantContext.getTenantId();
    }

    /**
     * 设置实体类的租户ID
     *
     * @param model 实体类model
     */
    public static Serializable setTenantId(Model model) {
        ModelInfo modelInfo = Models.get(model.getClass());
        if (Objects.isNull(modelInfo.getTenantIdFieldInfo())) {
            return null;
        }

        Serializable tenantId = getTenantId();
        if (Objects.isNull(tenantId)) {
            return null;
        }

        if (tenantId instanceof TenantId) {
            throw new RuntimeException("tenantId has multiple values");
        }

        try {
            modelInfo.getTenantIdFieldInfo().getWriteFieldInvoker().invoke(model, new Object[]{tenantId});
            return tenantId;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置实体类的租户ID
     *
     * @param entity
     */
    public static Serializable setTenantId(Object entity) {
        TableInfo tableInfo = Tables.get(entity.getClass());
        return setTenantId(tableInfo, entity);
    }

    /**
     * 设置租户ID
     *
     * @param entity
     */
    public static void setTenantId(TableFieldInfo tableFieldInfo, Object entity, Object tenantId) {
        if (Objects.isNull(tenantId)) {
            return;
        }

        if (tenantId instanceof TenantId) {
            throw new RuntimeException("tenantId has multiple values");
        }

        try {
            tenantId = TypeConvertUtil.convert(tenantId, tableFieldInfo.getFieldInfo().getTypeClass());
            tableFieldInfo.getWriteFieldInvoker().invoke(entity, new Object[]{tenantId});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置租户ID
     *
     * @param entity
     */
    public static void setTenantId(ModelFieldInfo modelFieldInfo, Model entity, Object tenantId) {
        if (Objects.isNull(tenantId)) {
            return;
        }

        if (tenantId instanceof TenantId) {
            throw new RuntimeException("tenantId has multiple values");
        }

        try {
            tenantId = TypeConvertUtil.convert(tenantId, modelFieldInfo.getFieldInfo().getTypeClass());
            modelFieldInfo.getWriteFieldInvoker().invoke(entity, new Object[]{tenantId});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置实体类的租户ID
     *
     * @param entity
     */
    public static Serializable setTenantId(TableInfo tableInfo, Object entity) {
        if (Objects.isNull(tableInfo.getTenantIdFieldInfo())) {
            return null;
        }

        Serializable tenantId = getTenantId();
        if (Objects.isNull(tenantId)) {
            return null;
        }

        if (tenantId instanceof TenantId) {
            throw new RuntimeException("tenantId has multiple values");
        }

        try {
            tableInfo.getTenantIdFieldInfo().getWriteFieldInvoker().invoke(entity, new Object[]{
                    tenantId
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return tenantId;
    }

    /**
     * 添加租户条件
     *
     * @param table          MpTable
     * @param conditionChain ConditionChain
     */
    public static void addTenantCondition(MpTable table, ConditionChain conditionChain) {
        Serializable tid = TenantUtil.getTenantId();
        if (Objects.isNull(tid)) {
            return;
        }
        TableInfo tableInfo = table.getTableInfo();
        if (Objects.isNull(tableInfo.getTenantIdFieldInfo())) {
            return;
        }
        TableFieldInfo tenantIdFieldInfo = tableInfo.getTenantIdFieldInfo();
        if (tid instanceof TenantId) {
            TenantId tenantId = (TenantId) tid;
            if (tenantId.isMultiValue()) {
                conditionChain.in(new MpDatasetField(table, tenantIdFieldInfo.getColumnName(),
                        tenantIdFieldInfo.getFieldInfo(), tenantIdFieldInfo.getTypeHandler(),
                        tenantIdFieldInfo.getTableFieldAnnotation().jdbcType()), tenantId.getValues());
                return;
            }
            tid = tenantId.getValues()[0];
        }

        conditionChain.eq(new MpDatasetField(table, tenantIdFieldInfo.getColumnName(),
                tenantIdFieldInfo.getFieldInfo(), tenantIdFieldInfo.getTypeHandler(),
                tenantIdFieldInfo.getTableFieldAnnotation().jdbcType()), tid);
    }
}
