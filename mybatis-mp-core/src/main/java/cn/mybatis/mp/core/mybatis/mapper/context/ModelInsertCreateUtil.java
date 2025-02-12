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
import cn.mybatis.mp.core.db.reflect.TableIds;
import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.incrementer.IdentifierGenerator;
import cn.mybatis.mp.core.incrementer.IdentifierGeneratorFactory;
import cn.mybatis.mp.core.mybatis.mapper.context.strategy.SaveStrategy;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.Insert;
import cn.mybatis.mp.core.sql.executor.MpTable;
import cn.mybatis.mp.core.sql.executor.MpTableField;
import cn.mybatis.mp.core.tenant.TenantUtil;
import cn.mybatis.mp.core.util.DefaultValueUtil;
import cn.mybatis.mp.core.util.ModelInfoUtil;
import cn.mybatis.mp.core.util.StringPool;
import cn.mybatis.mp.core.util.TypeConvertUtil;
import cn.mybatis.mp.db.IdAutoType;
import cn.mybatis.mp.db.Model;
import cn.mybatis.mp.db.annotations.TableField;
import cn.mybatis.mp.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelInsertCreateUtil {

    public static <M extends Model<T>, T> BaseInsert<?> create(BaseInsert<?> insert, ModelInfo modelInfo, M insertData, SaveStrategy<T> saveStrategy, DbType dbType) {
        insert = insert == null ? new Insert() : insert;
        TableInfo tableInfo = modelInfo.getTableInfo();

        insert.$().cacheTableInfo(tableInfo);
        MpTable table = (MpTable) insert.$().table(tableInfo.getType());
        insert.insert(table);

        //设置租户ID
        TenantUtil.setTenantId(insertData);
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < modelInfo.getFieldSize(); i++) {
            ModelFieldInfo modelFieldInfo = modelInfo.getModelFieldInfos().get(i);
            boolean isNeedInsert = false;
            Object value = modelFieldInfo.getValue(insertData);
            if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                if (!IdUtil.isIdValueExists(value)) {
                    TableId tableId = TableIds.get(modelInfo.getTableInfo().getType(), dbType);
                    if (tableId.value() == IdAutoType.GENERATOR) {
                        isNeedInsert = true;
                        IdentifierGenerator identifierGenerator = IdentifierGeneratorFactory.getIdentifierGenerator(tableId.generatorName());
                        Object id = identifierGenerator.nextId(modelInfo.getType());
                        if (IdUtil.setId(insertData, modelFieldInfo, id)) {
                            value = id;
                        }
                    }
                } else {
                    isNeedInsert = true;
                }
            } else if (Objects.nonNull(value)) {
                isNeedInsert = true;
            } else if (modelFieldInfo.getTableFieldInfo().isLogicDelete()) {
                //逻辑删除字段
                //设置删除初始值
                value = modelFieldInfo.getTableFieldInfo().getLogicDeleteInitValue();
                if (value != null) {
                    isNeedInsert = true;
                    //逻辑删除初始值回写
                    ModelInfoUtil.setValue(modelFieldInfo, insertData, value);
                } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                    //读取回填 @TableField里的默认值
                    value = DefaultValueUtil.getAndSetDefaultValue(insertData, modelFieldInfo);
                    isNeedInsert = Objects.nonNull(value);
                }
            } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                //读取回填 默认值
                value = DefaultValueUtil.getAndSetDefaultValue(insertData, modelFieldInfo);
                isNeedInsert = Objects.nonNull(value);
            } else if (modelFieldInfo.getTableFieldInfo().isVersion()) {
                isNeedInsert = true;

                //乐观锁设置 默认值1
                value = TypeConvertUtil.convert(Integer.valueOf(1), modelFieldInfo.getField().getType());
                //乐观锁回写
                ModelInfoUtil.setValue(modelFieldInfo, insertData, value);
            }

            // 看是否是强制字段
            if (!isNeedInsert && (saveStrategy.isAllFieldSave() || (Objects.nonNull(saveStrategy.getForceFields()) && saveStrategy.getForceFields().contains(modelFieldInfo.getField().getName())))) {
                isNeedInsert = true;
                if (modelFieldInfo.getTableFieldInfo().isTableId() && value == null) {
                    isNeedInsert = false;
                }
            }

            if (isNeedInsert) {
                insert.fields(new MpTableField(table, modelFieldInfo.getTableFieldInfo()));
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
                    MybatisParameter mybatisParameter = new MybatisParameter(value, tableField.typeHandler(), tableField.jdbcType());
                    values.add(Methods.cmd(mybatisParameter));
                }
            }
        }
        insert.values(values);

        if (saveStrategy.getConflictAction() != null) {
            insert.conflictKeys(saveStrategy.getConflictKeys());
            insert.conflictKeys(saveStrategy.getConflictColumns());
            insert.onConflict(saveStrategy.getConflictAction());
        }
        return insert;
    }
}
