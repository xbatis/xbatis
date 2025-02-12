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

package cn.mybatis.mp.core.mybatis.mapper.context;

import cn.mybatis.mp.core.db.reflect.ModelFieldInfo;
import cn.mybatis.mp.core.db.reflect.ModelInfo;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.mybatis.mp.core.sql.MybatisCmdFactory;
import cn.mybatis.mp.core.sql.executor.Update;
import cn.mybatis.mp.core.sql.util.WhereUtil;
import cn.mybatis.mp.core.tenant.TenantUtil;
import cn.mybatis.mp.core.util.DefaultValueUtil;
import cn.mybatis.mp.core.util.ModelInfoUtil;
import cn.mybatis.mp.core.util.StringPool;
import cn.mybatis.mp.core.util.TypeConvertUtil;
import cn.mybatis.mp.db.Model;
import cn.mybatis.mp.db.annotations.TableField;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.tookit.LambdaUtil;

import java.util.Objects;
import java.util.Set;

public class ModelUpdateCmdCreateUtil {

    public static <M extends Model<T>, T> Update create(ModelInfo modelInfo, M model, UpdateStrategy<M> updateStrategy) {
        Where where = updateStrategy.getWhere();
        if (where == null) {
            where = WhereUtil.create(modelInfo.getTableInfo());
        }

        Update update = new Update(where);
        if (updateStrategy.getOn() != null) {
            updateStrategy.getOn().accept(where);
        }

        boolean hasPutConditionBefore = where.hasContent();

        MybatisCmdFactory $ = update.$();
        Table table = $.table(modelInfo.getEntityType());

        boolean hasIdCondition = false;

        Set<String> forceFields = LambdaUtil.getFieldNames(updateStrategy.getForceFields());

        for (ModelFieldInfo modelFieldInfo : modelInfo.getModelFieldInfos()) {
            boolean isForceUpdate = Objects.nonNull(forceFields) && forceFields.contains(modelFieldInfo.getField().getName());
            Object value = modelFieldInfo.getValue(model);
            if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                if (Objects.nonNull(value)) {
                    if (update.$where().hasContent()) {
                        update.$where().extConditionChain().eq($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), Methods.cmd(value));
                    } else {
                        update.$where().conditionChain().eq($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), Methods.cmd(value));
                    }
                    hasIdCondition = true;
                }
                continue;
            } else if (modelFieldInfo.getTableFieldInfo().isTenantId()) {
                if (isForceUpdate) {
                    if (Objects.isNull(value)) {
                        value = TenantUtil.getTenantId();
                        if (Objects.isNull(value)) {
                            //虽然强制 但是租户ID没值 不修改
                            continue;
                        }
                        //租户ID 回填
                        TenantUtil.setTenantId(modelFieldInfo, model, value);
                    }
                } else {
                    //租户ID不修改
                    continue;
                }
            } else if (modelFieldInfo.getTableFieldInfo().isVersion()) {
                if (Objects.isNull(value)) {
                    //乐观锁字段无值 不增加乐观锁条件
                    continue;
                }
                //乐观锁+1
                Object version = TypeConvertUtil.convert(Long.valueOf(1) + 1, modelFieldInfo.getField().getType());
                //乐观锁设置
                update.set($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), Methods.cmd(version));
                //乐观锁条件
                update.$where().extConditionChain().eq($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), Methods.cmd(value));
                //乐观锁回写
                ModelInfoUtil.setValue(modelFieldInfo, model, version);
                continue;
            }

            if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().updateDefaultValue())) {
                //读取回填 修改默认值
                value = DefaultValueUtil.getAndSetUpdateDefaultValue(model, modelFieldInfo);
            }

            if (!isForceUpdate && !modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().update()) {
                continue;
            }

            if (isForceUpdate || updateStrategy.isAllFieldUpdate()) {
                if (Objects.isNull(value)) {
                    update.set($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), NULL.NULL);
                    continue;
                }
            }

            if (Objects.nonNull(value)) {
                TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
                MybatisParameter mybatisParameter = new MybatisParameter(value, tableField.typeHandler(), tableField.jdbcType());
                update.set($.field(table, modelFieldInfo.getTableFieldInfo().getColumnName()), Methods.cmd(mybatisParameter));
            }
        }

        if (!hasIdCondition && !hasPutConditionBefore) {
            throw new RuntimeException("update has no where condition content ");
        }
        update.update(table);
        return update;
    }

}
