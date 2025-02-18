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

package cn.xbatis.core;


import cn.xbatis.core.logicDelete.LogicDeleteSwitch;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.sql.XbatisSQLBuilder;
import cn.xbatis.core.sql.SQLBuilder;
import cn.xbatis.core.sql.listener.ForeignKeySQLListener;
import cn.xbatis.core.sql.listener.LogicDeleteSQLListener;
import cn.xbatis.core.sql.listener.TenantSQLListener;
import cn.xbatis.core.util.StringPool;
import cn.xbatis.core.util.TypeConvertUtil;
import db.sql.api.DbType;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.paging.IPagingProcessor;
import db.sql.api.impl.paging.PagingProcessorFactory;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 全局配置
 */
public final class XbatisConfig {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();
    private static final String COLUMN_UNDERLINE = "columnUnderline";
    private static final String TABLE_UNDERLINE = "tableUnderline";
    private static final String DEFAULT_BATCH_SIZE = "defaultBatchSize";
    private static final String SQL_BUILDER = "SQLBuilder";
    private static final String LOGIC_DELETE_SWITCH = "logicDeleteSwitch";
    private static final String DEFAULT_VALUE_MANAGER = "defaultValueManager";
    private static final String SINGLE_MAPPER_CLASS = "singleMapperClass";
    private static final List<SQLListener> SQL_LISTENER = new ArrayList<>();
    private static volatile DbType DEFAULT_DB_TYPE;

    static {
        SQL_LISTENER.add(new ForeignKeySQLListener());
        SQL_LISTENER.add(new TenantSQLListener());
        SQL_LISTENER.add(new LogicDeleteSQLListener());
        Map<String, BiFunction<Class<?>, Class<?>, Object>> defaultValueMap = new ConcurrentHashMap<>();
        defaultValueMap.put("{BLANK}", (source, type) -> {
            if (type == String.class) {
                return StringPool.EMPTY;
            } else if (type.isArray()) {
                return Array.newInstance(type, 0);
            } else if (List.class.isAssignableFrom(type)) {
                return Collections.EMPTY_LIST;
            } else if (Set.class.isAssignableFrom(type)) {
                return Collections.EMPTY_SET;
            } else if (Map.class.isAssignableFrom(type)) {
                return Collections.EMPTY_MAP;
            }
            throw new RuntimeException("Inconsistent types：" + type);
        });

        defaultValueMap.put("{NOW}", (source, type) -> {
            if (type == LocalDateTime.class) {
                return LocalDateTime.now();
            } else if (type == LocalDate.class) {
                return LocalDate.now();
            } else if (type == LocalTime.class) {
                return LocalTime.now();
            } else if (type == Date.class) {
                return new Date();
            } else if (type == Long.class) {
                return System.currentTimeMillis();
            } else if (type == Integer.class) {
                return (int) (System.currentTimeMillis() / 1000);
            }
            throw new RuntimeException("Inconsistent types：" + type);
        });
        CACHE.put(DEFAULT_VALUE_MANAGER, defaultValueMap);
    }

    private XbatisConfig() {

    }

    /**
     * 获取默认DbType
     *
     * @return
     */
    public static DbType getDefaultDbType() {
        return XbatisConfig.DEFAULT_DB_TYPE;
    }

    /**
     * 设置默认DbType
     *
     * @param defaultDbType
     */
    public static void setDefaultDbType(DbType defaultDbType) {
        XbatisConfig.DEFAULT_DB_TYPE = defaultDbType;
    }

    /**
     * 数据库列是否下划线规则 默认 true
     *
     * @return 列是否是下划线命名规则
     */
    public static boolean isColumnUnderline() {
        return (boolean) CACHE.computeIfAbsent(COLUMN_UNDERLINE, key -> true);
    }

    /**
     * 数据库列是否下划线规则（必须在项目启动时设置，否则可能永远不会成功）
     *
     * @param bool 列是否下划线命名规则
     */
    public static void setColumnUnderline(boolean bool) {
        CACHE.putIfAbsent(COLUMN_UNDERLINE, bool);
    }

    /**
     * 数据库表是否下划线规则 默认 true
     *
     * @return 是否是下划线规则
     */
    public static boolean isTableUnderline() {
        return (boolean) CACHE.computeIfAbsent(TABLE_UNDERLINE, key -> true);
    }

    /**
     * 设置数据库表是否下划线规则（必须在项目启动时设置，否则可能永远不会成功）
     *
     * @param bool 是否是下划线规则
     */
    public static void setTableUnderline(boolean bool) {
        CACHE.putIfAbsent(TABLE_UNDERLINE, bool);
    }

    /**
     * 默认1000
     *
     * @return 批量提交的默认size
     */
    public static int getDefaultBatchSize() {
        return (int) CACHE.computeIfAbsent(DEFAULT_BATCH_SIZE, key -> 1000);
    }

    public static void setDefaultBatchSize(int defaultBatchSize) {
        if (defaultBatchSize < 1) {
            throw new RuntimeException("defaultBatchSize can't less 1");
        }
        CACHE.put(DEFAULT_BATCH_SIZE, defaultBatchSize);
    }

    /**
     * 设置QUERY SQL BUILDER
     *
     * @return 返回QuerySQLBuilder
     */
    public static SQLBuilder getSQLBuilder() {
        return (SQLBuilder) CACHE.computeIfAbsent(SQL_BUILDER, key -> new XbatisSQLBuilder());
    }

    public static void setSQLBuilder(SQLBuilder sqlBuilder) {
        CACHE.put(SQL_BUILDER, sqlBuilder);
    }

    /**
     * 获取逻辑删除开关，默认开启
     *
     * @return 逻辑开关的是否打开
     */
    public static boolean isLogicDeleteSwitchOpen() {
        Boolean state = LogicDeleteSwitch.getState();
        if (state != null) {
            //局部开关 优先
            return state;
        }
        return (boolean) CACHE.computeIfAbsent(LOGIC_DELETE_SWITCH, key -> true);
    }

    /**
     * 设置逻辑删除开关状态（必须在项目启动时设置，否则可能永远false）
     *
     * @param bool 开关状态
     */
    public static void setLogicDeleteSwitch(boolean bool) {
        CACHE.putIfAbsent(LOGIC_DELETE_SWITCH, bool);
    }

    public static boolean isDefaultValueKeyFormat(String key) {
        return key.startsWith("{") && key.endsWith("}");
    }

    public static void setDefaultValue(String key, BiFunction<Class<?>, Class<?>, Object> f) {
        checkDefaultValueKey(key);
        ((Map<String, BiFunction<Class<?>, Class<?>, Object>>) CACHE.get(DEFAULT_VALUE_MANAGER)).computeIfAbsent(key, mapKey -> f);
    }

    private static void checkDefaultValueKey(String key) {
        if (!isDefaultValueKeyFormat(key)) {
            throw new RuntimeException("key must start with '{' and end with '}'");
        }
    }

    /**
     * 获取默认值
     *
     * @param clazz 字段所在的class
     * @param type  默认值的类型
     * @param key   默认值的key，key必须以{}包裹，例如:{NOW}
     * @param <T>   类型clazz的泛型
     * @return 返回指定类型clazz key的默认值
     */
    public static <T> T getDefaultValue(Class<?> clazz, Class<T> type, String key) {
        if (!isDefaultValueKeyFormat(key)) {
            return TypeConvertUtil.convert(key, type);
        }
        Map<String, BiFunction<Class<?>, Class<?>, T>> map = (Map<String, BiFunction<Class<?>, Class<?>, T>>) CACHE.get(DEFAULT_VALUE_MANAGER);
        BiFunction<Class<?>, Class<?>, T> f = map.get(key);
        if (f == null) {
            throw new RuntimeException("default value key:  " + key + " not set");
        }
        return f.apply(clazz, type);
    }

    /**
     * 获取单Mapper的class 用于BasicMapper.withSqlSession方法 statement 拼接
     *
     * @return 单Mapper的class
     */
    public static Class<? extends BasicMapper> getSingleMapperClass() {
        return (Class) CACHE.computeIfAbsent(SINGLE_MAPPER_CLASS, key -> BasicMapper.class);
    }

    /**
     * 设置单Mapper的class 用于BasicMapper.withSqlSession方法 statement 拼接
     *
     * @param singleMapperClass
     */
    public static void setSingleMapperClass(Class<? extends BasicMapper> singleMapperClass) {
        CACHE.putIfAbsent(SINGLE_MAPPER_CLASS, singleMapperClass);
    }

    /**
     * 添加SQLListener
     *
     * @param sqlListener
     */
    public static void addSQLListener(SQLListener sqlListener) {
        SQL_LISTENER.add(sqlListener);
    }

    /**
     * 移除SQLListener
     *
     * @param type
     */
    public static <T extends SQLListener> void removeSQLListener(Class<T> type) {
        Iterator<SQLListener> iterator = SQL_LISTENER.iterator();
        while (iterator.hasNext()) {
            if (type.isAssignableFrom(iterator.next().getClass())) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取所有的SQLListener
     *
     * @return
     */
    public static List<SQLListener> getSQLListeners() {
        return Collections.unmodifiableList(SQL_LISTENER);
    }

    /**
     * 设置分页处理器
     *
     * @param dbType          数据库类型
     * @param pagingProcessor 分页处理器
     */
    public static void setPagingProcessor(DbType dbType, IPagingProcessor pagingProcessor) {
        PagingProcessorFactory.setProcessor(dbType, pagingProcessor);
    }

    /**
     * 获取分页处理器
     *
     * @param dbType 数据库类型
     * @return 分页处理器
     */
    public static IPagingProcessor getPagingProcessor(DbType dbType) {
        return PagingProcessorFactory.getProcessor(dbType);
    }
}
