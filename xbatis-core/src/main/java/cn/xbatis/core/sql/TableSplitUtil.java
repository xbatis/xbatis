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

package cn.xbatis.core.sql;

import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.mybatis.mapper.context.MybatisParameter;
import cn.xbatis.core.sql.executor.MpTable;
import cn.xbatis.db.annotations.TableSplitter;
import db.sql.api.impl.cmd.basic.BasicValue;

import java.util.Collection;
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

        Object v = null;
        if (value.getClass().isArray()) {
            //假如是数组
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
                return v;
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            //假如是数组
            Collection collection = (Collection) value;
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
                return v;
            }
        } else if (splitter.support(value.getClass())) {
            return value;
        }
        return v;
    }

    public static boolean isNeedSplitHandle(MpTable mpTable) {
        if (!mpTable.getTableInfo().isSplitTable()) {
            return false;
        }
        if (mpTable.getName().equals(mpTable.getTableInfo().getSchemaAndTableName())) {
            return true;
        }
        return false;
    }

    public static String getSplitTableName(TableInfo tableInfo, Object value) {
        TableSplitter splitter = tableInfo.getTableSplitter();
        Object v = getSplitValue(value, splitter);
        if (v == null) {
            return null;
        }
        String splitTableName = splitter.split(tableInfo.getTableName(), v);
        if (splitTableName == null) {
            return null;
        }
        if (!tableInfo.getSchema().isEmpty()) {
            splitTableName = tableInfo.getSchema() + "." + splitTableName;
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
        String splitTableName = getSplitTableName(mpTable.getTableInfo(), value);
        if (splitTableName == null) {
            return;
        }
        mpTable.setName(splitTableName);
    }
}
