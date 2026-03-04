/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package cn.xbatis.core.sql.executor;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.context.MybatisParameter;
import cn.xbatis.db.annotations.TableSplitter;
import db.sql.api.impl.cmd.basic.BasicValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class TableSplitUtil {

    private static Object getSplitValue(Object value, TableSplitter splitter) {
        if (value == null) {
            return null;
        }

        if (value instanceof BasicValue) {
            return getSplitValue(((BasicValue) value).getValue(), splitter);
        }

        if (value instanceof MybatisParameter) {
            return getSplitValue(((MybatisParameter) value).getValue(), splitter);
        }

        if (value instanceof Supplier) {
            return getSplitValue(((Supplier) value).get(), splitter);
        }

        Object v;
        if (splitter.support(value.getClass())) {
            return value;
        } else if (value.getClass().isArray()) {
            //假如是数组
            List list = new ArrayList();
            Object[] arr = (Object[]) value;
            for (Object o : arr) {
                if (o == null) {
                    continue;
                }
                v = getSplitValue(o, splitter);
                if (v == null) {
                    continue;
                }
                if (!splitter.support(v.getClass())) {
                    continue;
                }
                //从数组中找到符合的
                list.add(v);
            }
            return list;
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            //假如是数组
            Collection collection = (Collection) value;
            List list = new ArrayList();
            for (Object item : collection) {
                if (item == null) {
                    continue;
                }
                v = getSplitValue(item, splitter);
                if (v == null) {
                    continue;
                }
                if (!splitter.support(v.getClass())) {
                    continue;
                }
                //从数组中找到符合的
                list.add(v);
            }
            return list;
        }
        return null;
    }

    public static boolean isNeedSplitHandle(MpTable mpTable) {
        return mpTable.allowSplitTable;
    }

    /**
     * 获取分表
     *
     * @param tableInfo
     * @param value     不能是集合 数组
     * @return 分隔后的表
     */
    public static String getSplitTableNameAllowMulti(TableInfo tableInfo, Object value) {
        TableSplitter splitter = tableInfo.getTableSplitter();
        Object v = getSplitValue(value, splitter);
        if (v == null) {
            return null;
        }
        if (!splitter.support(v.getClass())) {
            return null;
        }
        return splitter.split(tableInfo.getTableName(), v);
    }

    private static String getSplitTableName(MpTable mpTable, Object value) {
        TableInfo tableInfo = mpTable.tableInfo;
        TableSplitter splitter = tableInfo.getTableSplitter();
        Object v = getSplitValue(value, splitter);
        if (v == null) {
            return null;
        }
        String splitTableName = null;
        if (v instanceof List) {
            List list = (List) v;
            for (Object item : list) {
                String name = splitter.split(tableInfo.getTableName(), item);
                if (name == null) {
                    continue;
                }
                if (splitTableName != null && !splitTableName.equals(name)) {
                    //包含多个分区 直接设置为原始表；且不允许 分表了
                    mpTable.allowSplitTable = false;
                    mpTable.setName(tableInfo.getTableName());
                    return null;
                }
                splitTableName = name;
            }
        } else {
            splitTableName = splitter.split(tableInfo.getTableName(), v);
            if (splitTableName == null) {
                return null;
            }
        }
        return splitTableName;
    }


    public static void splitHandle(MpTable mpTable, Object value) {
        if (value == null) {
            return;
        }
        if (!isNeedSplitHandle(mpTable)) {
            return;
        }
        String splitTableName = getSplitTableName(mpTable, value);
        if (splitTableName == null) {
            return;
        }
        if (!mpTable.getName().equals(mpTable.getTableInfo().getTableName()) && !mpTable.getName().equals(splitTableName)) {
            // 包含多个分区 直接设置为原始表；且不允许 分表了
            mpTable.setName(mpTable.getTableInfo().getTableName());
            mpTable.allowSplitTable = false;
            return;
        }

        mpTable.setName(splitTableName);
    }
}
