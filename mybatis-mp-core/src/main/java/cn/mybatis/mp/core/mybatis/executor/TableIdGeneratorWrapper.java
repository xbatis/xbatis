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

package cn.mybatis.mp.core.mybatis.executor;

import cn.mybatis.mp.core.db.reflect.TableIds;
import cn.mybatis.mp.core.db.reflect.TableInfo;
import cn.mybatis.mp.core.db.reflect.Tables;
import cn.mybatis.mp.core.exception.NotTableClassException;
import cn.mybatis.mp.core.mybatis.executor.keygen.MybatisJdbc3KeyGenerator;
import cn.mybatis.mp.core.mybatis.executor.keygen.MybatisSelectKeyGenerator;
import cn.mybatis.mp.core.mybatis.provider.SQLCmdSqlSource;
import cn.mybatis.mp.core.util.GenericUtil;
import cn.mybatis.mp.core.util.StringPool;
import cn.mybatis.mp.db.annotations.Table;
import cn.mybatis.mp.db.annotations.TableId;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TableIdGeneratorWrapper {

    private static String getMapperName(MappedStatement ms) {
        return ms.getId().substring(0, ms.getId().lastIndexOf(StringPool.DOT));
    }

    private static Class getEntityClass(MappedStatement ms) {
        try {
            Class clazz = Class.forName(getMapperName(ms), false, Thread.currentThread().getContextClassLoader());
            List<Class<?>> list = GenericUtil.getGenericInterfaceClass(clazz);
            if (Objects.isNull(list) || list.isEmpty()) {
                return null;
            }
            return list.stream().filter(item -> item.isAnnotationPresent(Table.class)).findFirst().orElse(null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addEntityKeyGenerator(MappedStatement ms, Class entityClass) {
        String selectKeyId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        if (ms.getConfiguration().hasKeyGenerator(selectKeyId)) {
            return;
        }

        synchronized (selectKeyId.intern()) {
            if (ms.getConfiguration().hasKeyGenerator(selectKeyId)) {
                return;
            }
            try {
                TableInfo tableInfo = Tables.get(entityClass);
                if (tableInfo.getIdFieldInfos().size() == 1) {
                    addEntityKeyGenerator(ms, selectKeyId, tableInfo);
                }
            } catch (NotTableClassException e) {
                //忽略
            }
        }
    }

    public static void addEntityKeyGenerator(MappedStatement ms, String selectKeyId, TableInfo tableInfo) {
        KeyGenerator keyGenerator;
        SQLCmdSqlSource sqlCmdSqlSource = (SQLCmdSqlSource) ms.getSqlSource();
        TableId tableId = TableIds.get(tableInfo.getType(), sqlCmdSqlSource.getDbType());
        switch (tableId.value()) {
            //数据库默认自增
            case AUTO: {
                keyGenerator = MybatisJdbc3KeyGenerator.INSTANCE;
                break;
            }
            //自己输入
            case NONE:
                //自定义自增
            case GENERATOR: {
                keyGenerator = NoKeyGenerator.INSTANCE;
                break;
            }
            //序列
            case SQL: {
                SqlSource sqlSource = new StaticSqlSource(ms.getConfiguration(), tableId.sql());
                ResultMap selectKeyResultMap = new ResultMap.Builder(ms.getConfiguration(), selectKeyId, tableInfo.getSingleIdFieldInfo(true).getFieldInfo().getTypeClass(),
                        Collections.emptyList(), false).build();
                MappedStatement selectKeyMappedStatement = new MappedStatement.Builder(ms.getConfiguration(), selectKeyId, sqlSource, SqlCommandType.SELECT)
                        .keyProperty("id")
                        .resultMaps(Collections.singletonList(selectKeyResultMap))
                        .keyGenerator(NoKeyGenerator.INSTANCE)
                        .useCache(false)
                        .flushCacheRequired(true)
                        .build();
                keyGenerator = new MybatisSelectKeyGenerator(selectKeyMappedStatement, true);
                break;
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
        if (keyGenerator != NoKeyGenerator.INSTANCE) {
            MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
            msMetaObject.setValue("keyGenerator", keyGenerator);
            msMetaObject.setValue("keyProperties", new String[]{"id"});
            msMetaObject.setValue("keyColumns", new String[]{tableInfo.getSingleIdFieldInfo(true).getColumnName()});
        }
        if (!ms.getConfiguration().hasKeyGenerator(selectKeyId)) {
            ms.getConfiguration().addKeyGenerator(selectKeyId, keyGenerator);
        }
    }
}
