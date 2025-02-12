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

package cn.mybatis.mp.core.mybatis.mapping;

import cn.mybatis.mp.core.db.reflect.FieldInfo;
import cn.mybatis.mp.core.mybatis.configuration.MybatisConfiguration;
import cn.mybatis.mp.core.mybatis.executor.MybatisIdUtil;
import cn.mybatis.mp.core.mybatis.typeHandler.GenericTypeHandler;
import cn.mybatis.mp.core.mybatis.typeHandler.MybatisTypeHandlerUtil;
import cn.mybatis.mp.core.util.FieldUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResultMapWrapper {

    public static void replaceResultMap(MappedStatement ms) {
        if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }
        MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
        msMetaObject.setValue("resultMaps", replaceResultMap((MybatisConfiguration) ms.getConfiguration(), ms.getResultMaps()));
    }

    public static List<ResultMap> replaceResultMap(MybatisConfiguration configuration, List<ResultMap> sourceResultMap) {
        return sourceResultMap.stream().map(item -> {
            if (!item.getResultMappings().isEmpty()) {
                return replaceTypeHandler(configuration, item);
            }
            String resultMapId = "mp-" + MybatisIdUtil.convertIdPath(item.getType().getName());
            if (configuration.hasResultMap(resultMapId)) {
                return configuration.getResultMap(resultMapId);
            }
            ResultMap newResultMap = ResultMapUtils.getResultMap(configuration, item.getType());
            if (Objects.nonNull(newResultMap)) {
                return newResultMap;
            }
            return item;
        }).collect(Collectors.toList());
    }

    private static ResultMap replaceTypeHandler(MybatisConfiguration configuration, ResultMap resultMap) {
        if (Map.class.isAssignableFrom(resultMap.getType())) {
            return resultMap;
        }

        MetaObject msMetaObject = configuration.newMetaObject(resultMap);
        msMetaObject.setValue("resultMappings", replaceTypeHandler(configuration, resultMap, resultMap.getResultMappings()));
        msMetaObject.setValue("propertyResultMappings", replaceTypeHandler(configuration, resultMap, resultMap.getPropertyResultMappings()));
        msMetaObject.setValue("idResultMappings", replaceTypeHandler(configuration, resultMap, resultMap.getIdResultMappings()));
        return resultMap;
    }

    private static List<ResultMapping> replaceTypeHandler(MybatisConfiguration configuration, ResultMap resultMap, List<ResultMapping> resultMappings) {
        return resultMappings.stream().map(item -> {
            ResultMapping newItem = item;
            TypeHandler<?> typeHandler = item.getTypeHandler();
            if (typeHandler instanceof GenericTypeHandler) {


                Optional<Field> fieldOptional = FieldUtil.getResultMappingFields(resultMap.getType())
                        .stream().filter(i -> i.getName().equals(item.getProperty()))
                        .findFirst();

                if (!fieldOptional.isPresent()) {
                    throw new RuntimeException("class " + resultMap.getType() + " can't found property " + item.getProperty());
                }


                FieldInfo fieldInfo = new FieldInfo(resultMap.getType(), fieldOptional.get());

                //有泛型 需要替换mybatis-mp的实例
                typeHandler = MybatisTypeHandlerUtil.getTypeHandler(configuration, fieldInfo, (Class<? extends TypeHandler<?>>) typeHandler.getClass(), item.getJdbcType());
                newItem = new ResultMapping.Builder(configuration, item.getProperty(), item.getColumn(), typeHandler)
                        .javaType(fieldInfo.getTypeClass())
                        .jdbcType(item.getJdbcType())
                        .nestedResultMapId(item.getNestedResultMapId())
                        .nestedQueryId(item.getNestedQueryId())
                        .notNullColumns(item.getNotNullColumns())
                        .columnPrefix(item.getColumnPrefix())
                        .flags(item.getFlags())
                        .composites(item.getComposites())
                        .resultSet(item.getResultSet())
                        .foreignColumn(item.getForeignColumn())
                        .lazy(item.isLazy())
                        .build();
            }

            return newItem;
        }).collect(Collectors.toList());
    }

}
