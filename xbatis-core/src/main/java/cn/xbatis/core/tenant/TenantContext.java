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

import db.sql.api.impl.cmd.struct.ConditionChain;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 多租户上下文
 */
public class TenantContext {

    private static Supplier<Serializable> TENANT_INFO_GETTER;

    private static Consumer<ConditionChain> TENANT_ON_WHERE;

    private TenantContext() {
    }

    /**
     * 注册多租户获取器
     *
     * @param tenantInfoGetter
     */
    public static void registerTenantGetter(Supplier<Serializable> tenantInfoGetter) {
        TenantContext.TENANT_INFO_GETTER = tenantInfoGetter;
    }

    /**
     * 注册多租户on where监听
     *
     * @param onWhere
     */
    public static void registerTenantOnWhere(Consumer<ConditionChain> onWhere) {
        TenantContext.TENANT_ON_WHERE = onWhere;
    }

    /**
     * 获取租户信息
     *
     * @return
     */
    public static Serializable getTenantId() {
        if (Objects.isNull(TENANT_INFO_GETTER)) {
            return null;
        }
        Serializable id = TENANT_INFO_GETTER.get();
        if (id != null && id instanceof TenantId) {
            TenantId tenantId = (TenantId) id;
            if (tenantId.getValues() == null || tenantId.getValues().length == 0) {
                return null;
            } else if (tenantId.getValues().length == 1) {
                return tenantId.getValues()[0];
            }
        }
        return id;
    }

    public static Consumer<ConditionChain> getTenantOnWhere() {
        return TENANT_ON_WHERE;
    }
}
