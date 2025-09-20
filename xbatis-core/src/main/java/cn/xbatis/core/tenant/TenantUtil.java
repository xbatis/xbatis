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
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;

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
        Serializable tenantId = getTenantId();
        if (Objects.isNull(tenantId)) {
            return null;
        }

        ModelInfo modelInfo = Models.get(model.getClass());
        if (Objects.isNull(modelInfo.getTenantIdFieldInfo())) {
            return null;
        }

        setTenantId(modelInfo.getTenantIdFieldInfo(), model, tenantId);
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param model
     */
    public static void setTenantId(ModelFieldInfo modelFieldInfo, Model model, Object tenantId) {
        setTenantId(modelFieldInfo.getWriteFieldInvoker(), model, tenantId, modelFieldInfo.getFieldInfo().getTypeClass());
    }


    public static void setTenantId(SetFieldInvoker writeFieldInvoker, Object object, Object tenantId, Class<?> targetType) {
        if (Objects.isNull(tenantId)) {
            return;
        }

        if (tenantId instanceof TenantId) {
            throw new RuntimeException("tenantId has multiple values");
        }

        try {
            tenantId = TypeConvertUtil.convert(tenantId, targetType);
            writeFieldInvoker.invoke(object, new Object[]{tenantId});
            onInsert(object);
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
     * 设置实体类的租户ID
     *
     * @param entity
     */
    public static void setTenantId(TableFieldInfo tableFieldInfo, Object entity, Object tenantId) {
        setTenantId(tableFieldInfo.getWriteFieldInvoker(), entity, tenantId, tableFieldInfo.getFieldInfo().getTypeClass());
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

        setTenantId(tableInfo.getTenantIdFieldInfo(), entity, tenantId);
        return tenantId;
    }

    private static void onInsert(Object insertObject) {
        if (Objects.isNull(insertObject)) {
            return;
        }
        if (TenantContext.getTenantOnInsert() == null) {
            return;
        }
        TenantContext.getTenantOnInsert().accept(insertObject);
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
                onWhere(tableInfo.getType(), conditionChain);
                return;
            }
            tid = tenantId.getValues()[0];
        }

        conditionChain.eq(new MpDatasetField(table, tenantIdFieldInfo.getColumnName(),
                tenantIdFieldInfo.getFieldInfo(), tenantIdFieldInfo.getTypeHandler(),
                tenantIdFieldInfo.getTableFieldAnnotation().jdbcType()), tid);
        onWhere(tableInfo.getType(), conditionChain);
    }

    private static void onWhere(Class<?> entityType, ConditionChain where) {
        if (Objects.isNull(where)) {
            return;
        }
        if (TenantContext.getTenantOnWhere() == null) {
            return;
        }
        TenantContext.getTenantOnWhere().accept(entityType, where);
    }
}
