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

package cn.xbatis.core.mybatis.executor;

import cn.xbatis.core.mybatis.configuration.MybatisConfiguration;
import cn.xbatis.core.mybatis.mapper.context.MybatisLikeQueryParameter;
import cn.xbatis.core.mybatis.mapper.context.MybatisParameter;
import cn.xbatis.core.mybatis.typeHandler.LikeQuerySupport;
import cn.xbatis.core.mybatis.typeHandler.MybatisTypeHandlerUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public class ParameterHandleUtil {

    public static int setParameters(MybatisConfiguration configuration, PreparedStatement ps, Object[] params) throws SQLException {
        int index = 0;
        for (Object value : params) {
            index = setParameters(configuration, ps, index, value, null, null);
        }
        return index;
    }

    public static int setParameters(MybatisConfiguration configuration, PreparedStatement ps, Collection<Object> params) throws SQLException {
        int index = 0;
        for (Object value : params) {
            index = setParameters(configuration, ps, index, value, null, null);
        }
        return index;
    }

    public static int setParameters(MybatisConfiguration configuration, PreparedStatement ps, int index, Object value, TypeHandler typeHandler, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(value)) {
            ps.setNull(++index, Types.NULL);
            return index;
        }

        if (value instanceof Supplier) {
            return setParameters(configuration, ps, index, ((Supplier<?>) value).get(), typeHandler, jdbcType);
        } else if (value instanceof MybatisLikeQueryParameter) {
            MybatisLikeQueryParameter parameter = (MybatisLikeQueryParameter) value;
            Object realValue = parameter.getValue();
            if (realValue == null) {
                ps.setNull(++index, Types.NULL);
                return index;
            }

            if (typeHandler == null) {
                typeHandler = MybatisTypeHandlerUtil.getTypeHandler(configuration, realValue.getClass(), parameter.getTypeHandlerClass());
            }

            if (typeHandler instanceof LikeQuerySupport) {
                LikeQuerySupport querySupport = (LikeQuerySupport) typeHandler;
                querySupport.setLikeParameter(parameter.getLikeMode(), parameter.isNotLike(), ps, ++index, realValue, jdbcType);
                return index;
            }
            return setParameters(configuration, ps, index, realValue, typeHandler, parameter.getJdbcType());
        } else if (value instanceof MybatisParameter) {
            MybatisParameter parameter = (MybatisParameter) value;
            Object realValue = parameter.getValue();
            if (realValue == null) {
                ps.setNull(++index, Types.NULL);
                return index;
            }
            if (parameter.getTypeHandler() != null) {
                parameter.getTypeHandler().setParameter(ps, ++index, realValue, jdbcType);
                return index;
            }
            if (parameter.getTypeHandlerClass() == null || parameter.getTypeHandlerClass() == UnknownTypeHandler.class) {
                return setParameters(configuration, ps, index, realValue, null, parameter.getJdbcType());
            }
            if (typeHandler == null) {
                typeHandler = MybatisTypeHandlerUtil.getTypeHandler(configuration, realValue.getClass(), parameter.getTypeHandlerClass());
            }
            return setParameters(configuration, ps, index, parameter.getValue(), typeHandler, parameter.getJdbcType());
        }

        if (typeHandler == null) {
            typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(value.getClass());
        }

        if (typeHandler == null) {
            ps.setObject(++index, value);
            return index;
        }

        if (jdbcType == JdbcType.UNDEFINED) {
            jdbcType = null;
        }

        typeHandler.setParameter(ps, ++index, value, jdbcType);
        return index;
    }
}
