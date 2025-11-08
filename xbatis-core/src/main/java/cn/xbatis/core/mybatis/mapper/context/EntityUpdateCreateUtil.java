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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.db.reflect.OnListenerUtil;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.TableSplitUtil;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.sql.executor.Update;
import cn.xbatis.core.sql.util.WhereUtil;
import cn.xbatis.core.tenant.TenantUtil;
import cn.xbatis.core.util.DefaultValueUtil;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.core.util.TypeConvertUtil;
import cn.xbatis.db.annotations.TableField;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.tookit.LambdaUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EntityUpdateCreateUtil {

    public static void initUpdateValue(TableFieldInfo tableFieldInfo, Object insertData, Set<String> forceFields, Map<String, Object> defaultValueContext) {
        if (tableFieldInfo.isTenantId()) {
            boolean isForceUpdate = Objects.nonNull(forceFields) && forceFields.contains(tableFieldInfo.getField().getName());
            Object value = tableFieldInfo.getValue(insertData);
            if (isForceUpdate) {
                if (Objects.isNull(value)) {
                    value = TenantUtil.getTenantId();
                    if (Objects.isNull(value)) {
                        //虽然强制 但是租户ID没值 不修改
                        return;
                    }
                    //租户ID 回填
                    TenantUtil.setTenantId(tableFieldInfo, insertData, value);
                }
            }
            return;
        }

        if (!StringPool.EMPTY.equals(tableFieldInfo.getTableFieldAnnotation().updateDefaultValue())) {
            Object value = tableFieldInfo.getValue(insertData);
            if (value != null && !tableFieldInfo.getTableFieldAnnotation().updateDefaultValueFillAlways()) {
                return;
            }
            //读取回填 修改默认值
            DefaultValueUtil.getAndSetUpdateDefaultValue(insertData, tableFieldInfo, defaultValueContext);
            return;
        }

    }

    private static void doBefore(TableInfo tableInfo, Object insertData, Set<String> forceFields, Map<String, Object> defaultValueContext) {
        for (TableFieldInfo tableFieldInfo : tableInfo.getUpdateDoBeforeTableFieldInfos()) {
            initUpdateValue(tableFieldInfo, insertData, forceFields, defaultValueContext);
        }
        //更新动作通知
        OnListenerUtil.notifyUpdate(insertData);
    }

    public static Update create(TableInfo tableInfo, Object entity, UpdateStrategy<?> updateStrategy, Map<String, Object> defaultValueContext) {
        Where where = updateStrategy.getWhere();
        if (where == null) {
            where = WhereUtil.create(tableInfo);
        }

        Update update = new Update(where);
        if (updateStrategy.getOn() != null) {
            updateStrategy.getOn().accept(where);
        }

        boolean hasPutConditionBefore = where.hasContent();

        MybatisCmdFactory $ = update.$();
        MpTable table = (MpTable) $.table(entity.getClass());

        if (TableSplitUtil.isNeedSplitHandle(table)) {
            Object splitValue = tableInfo.getSplitFieldInfo().getValue(entity);
            if (Objects.isNull(splitValue)) {
                throw new RuntimeException("entity update has no table split value");
            } else {
                TableSplitUtil.splitHandle(table, splitValue);
            }
        }


        boolean hasIdCondition = false;
        Set<String> forceFields = LambdaUtil.getFieldNames(updateStrategy.getForceFields());
        doBefore(tableInfo, entity, forceFields, defaultValueContext);

        for (TableFieldInfo tableFieldInfo : tableInfo.getTableFieldInfos()) {
            Object value = tableFieldInfo.getValue(entity);
            boolean isForceUpdate = Objects.nonNull(forceFields) && forceFields.contains(tableFieldInfo.getField().getName());
            if (tableFieldInfo.isTableId()) {
                if (Objects.nonNull(value)) {
                    if (update.$where().hasContent()) {
                        update.$where().extConditionChain().eq($.field(table, tableFieldInfo.getColumnName()), Methods.cmd(value));
                    } else {
                        update.$where().conditionChain().eq($.field(table, tableFieldInfo.getColumnName()), Methods.cmd(value));
                    }
                    hasIdCondition = true;
                }
                continue;
            } else if (tableFieldInfo.isTenantId()) {
                if (!isForceUpdate || Objects.isNull(value)) {
                    //租户ID不修改
                    continue;
                }
            } else if (tableFieldInfo.isVersion()) {
                if (Objects.isNull(value)) {
                    //乐观锁字段无值 不增加乐观锁条件
                    continue;
                }
                //乐观锁+1
                Object version = TypeConvertUtil.convert(Long.valueOf(value.toString()) + 1, tableFieldInfo.getField().getType());
                //乐观锁设置
                update.set($.field(table, tableFieldInfo.getColumnName()), Methods.cmd(version));
                //乐观锁条件
                update.$where().extConditionChain().eq($.field(table, tableFieldInfo.getColumnName()), Methods.cmd(value));
                //乐观锁回写
                TableInfoUtil.setValue(tableFieldInfo, entity, version);
                continue;
            }
            if (!tableFieldInfo.getTableFieldAnnotation().exists()) {
                continue;
            }
            if (!isForceUpdate && !tableFieldInfo.getTableFieldAnnotation().update()) {
                continue;
            }

            if (isForceUpdate || updateStrategy.isAllFieldUpdate()) {
                if (Objects.isNull(value)) {
                    update.set($.field(table, tableFieldInfo.getColumnName()), NULL.NULL);
                    continue;
                }
            }

            if (Objects.nonNull(value)) {
                TableField tableField = tableFieldInfo.getTableFieldAnnotation();
                update.set($.field(table, tableFieldInfo.getColumnName()), CmdParamUtil.build(tableField, value));
            }
        }

        if (!hasIdCondition && !hasPutConditionBefore) {
            throw new RuntimeException("update has no where condition content ");
        }
        update.update(table);
        return update;
    }
}
