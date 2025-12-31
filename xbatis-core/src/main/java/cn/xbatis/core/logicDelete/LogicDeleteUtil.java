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

package cn.xbatis.core.logicDelete;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.executor.BaseUpdate;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.core.sql.executor.Update;
import cn.xbatis.db.annotations.LogicDelete;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.struct.On;
import db.sql.api.impl.cmd.struct.Where;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 逻辑删除工具类
 */
public final class LogicDeleteUtil {

    /**
     * 在指定逻辑开关下执行
     *
     * @param state    开关状态
     * @param supplier 返回函数
     * @param <T>      返回值
     * @return 函数执行后的返回值
     */
    public static <T> T execute(boolean state, Supplier<T> supplier) {
        try (LogicDeleteSwitch ignore = LogicDeleteSwitch.with(state)) {
            return supplier.get();
        }
    }

    /**
     * 在指定逻辑开关下执行
     *
     * @param state    开关状态
     * @param runnable 运行函数
     */
    public static void execute(boolean state, Runnable runnable) {
        try (LogicDeleteSwitch ignore = LogicDeleteSwitch.with(state)) {
            runnable.run();
        }
    }

    /**
     * 是否需要逻辑删除
     *
     * @param tableInfo 实体类tableInfo
     * @return 是否需要逻辑删除
     */
    public static boolean isNeedLogicDelete(TableInfo tableInfo) {
        return XbatisGlobalConfig.isLogicDeleteSwitchOpen() && Objects.nonNull(tableInfo.getLogicDeleteFieldInfo());
    }


    /**
     * 获取删除后的值
     *
     * @param logicDeleteFieldInfo 逻辑上删除字段
     * @param defaultValueContext  动态默认值上下文
     * @return 获取删除后的值
     */
    public static Object getLogicAfterValue(TableFieldInfo logicDeleteFieldInfo, Map<String, Object> defaultValueContext) {
        Object value;
        LogicDelete logicDelete = logicDeleteFieldInfo.getLogicDeleteAnnotation();
        Class type = logicDeleteFieldInfo.getFieldInfo().getTypeClass();
        value = XbatisGlobalConfig.getDefaultValue(logicDeleteFieldInfo.getFieldInfo().getClazz(), type, logicDelete.afterValue(), defaultValueContext);
        if (value == null) {
            throw new RuntimeException("Unable to obtain deleted value，please use XbatisConfig.setDefaultValue(\"" + logicDelete.afterValue() + "\") to resolve it");
        }
        return value;
    }

    /**
     * 获取逻辑删除时间
     *
     * @param tableInfo
     * @return 获取逻辑删除时间
     */
    public static Object getLogicDeleteTimeValue(TableInfo tableInfo) {
        TableFieldInfo deleteTimeField = tableInfo.getLogicDeleteTimeFieldInfo();
        Class type = deleteTimeField.getFieldInfo().getTypeClass();
        if (type == LocalDateTime.class) {
            return LocalDateTime.now();
        } else if (type == Date.class) {
            return new Date();
        } else if (type == Long.class) {
            return System.currentTimeMillis();
        } else if (type == Integer.class) {
            return (int) (System.currentTimeMillis() / 1000);
        } else {
            throw new RuntimeException("Unsupported types");
        }
    }

    /**
     * 设置逻辑删除字段值  例如： set deleted=1 和 删除时间设置
     *
     * @param baseUpdate
     * @param tableInfo
     */
    public static void addLogicDeleteUpdateSets(BaseUpdate baseUpdate, TableInfo tableInfo, Map<String, Object> defaultValueContext) {
        Class entityType = tableInfo.getType();
        TableField logicDeleteTableField = baseUpdate.$().field(entityType, tableInfo.getLogicDeleteFieldInfo().getField().getName(), 1);
        baseUpdate.set(logicDeleteTableField, getLogicAfterValue(tableInfo.getLogicDeleteFieldInfo(), defaultValueContext));


        if (tableInfo.getLogicDeleteTimeFieldInfo() != null) {
            TableField logicDeleteTimeTableField = baseUpdate.$().field(entityType, tableInfo.getLogicDeleteTimeFieldInfo().getField().getName(), 1);
            baseUpdate.set(logicDeleteTimeTableField, getLogicDeleteTimeValue(tableInfo));
        }
    }

    /**
     * 根据where 执行逻辑删除操作
     * 实际为update操作
     *
     * @param mapper
     * @param tableInfo
     * @param where
     * @param defaultValueContext
     * @return 影响的条数
     */
    public static int logicDelete(BasicMapper mapper, TableInfo tableInfo, Where where, Map<String, Object> defaultValueContext) {
        Update update = new Update(where);
        update.update(update.$().table(tableInfo.getType()))
                .connect(self -> {
                    LogicDeleteUtil.addLogicDeleteUpdateSets(self, tableInfo, defaultValueContext);
                });
        BiConsumer<Class<?>, BaseUpdate<?>> logicDeleteInterceptor = XbatisGlobalConfig.getLogicDeleteInterceptor();
        if (logicDeleteInterceptor != null) {
            logicDeleteInterceptor.accept(tableInfo.getType(), update);
        }
        return mapper.update(update);
    }


    /**
     * 添加逻辑删除条件
     *
     * @param table          MpTable
     * @param on On
     */
    public static void addLogicDeleteCondition(MpTable table, On on) {
        if (!XbatisGlobalConfig.isLogicDeleteSwitchOpen()) {
            return;
        }

        TableInfo tableInfo = table.getTableInfo();
        if (Objects.isNull(tableInfo.getLogicDeleteFieldInfo())) {
            return;
        }
        Object logicBeforeValue = tableInfo.getLogicDeleteFieldInfo().getLogicDeleteInitValue();
        TableField tableField = table.$(tableInfo.getLogicDeleteFieldInfo().getColumnName());
        if (Objects.isNull(logicBeforeValue)) {
            on.extConditionChain().isNull(tableField);
        } else {
            on.extConditionChain().eq(tableField, logicBeforeValue);
        }
    }

    /**
     * 添加逻辑删除条件
     *
     * @param table MpTable
     * @param where Where
     */
    public static void addLogicDeleteCondition(MpTable table, Where where) {
        if (!XbatisGlobalConfig.isLogicDeleteSwitchOpen()) {
            return;
        }

        TableInfo tableInfo = table.getTableInfo();
        if (Objects.isNull(tableInfo.getLogicDeleteFieldInfo())) {
            return;
        }
        Object logicBeforeValue = tableInfo.getLogicDeleteFieldInfo().getLogicDeleteInitValue();
        TableField tableField = table.$(tableInfo.getLogicDeleteFieldInfo().getColumnName());
        if (Objects.isNull(logicBeforeValue)) {
            where.extConditionChain().isNull(tableField);
        } else {
            where.extConditionChain().eq(tableField, logicBeforeValue);
        }
    }
}
