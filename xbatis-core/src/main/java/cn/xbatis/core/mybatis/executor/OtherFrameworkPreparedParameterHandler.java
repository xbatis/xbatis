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
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class OtherFrameworkPreparedParameterHandler implements ParameterHandler {

    private final Map<String, Object> parameterObject;

    private final MybatisConfiguration configuration;

    private final BoundSql boundSql;

    public OtherFrameworkPreparedParameterHandler(MybatisConfiguration configuration, BoundSql boundSql, Map<String, Object> parameterObject) {
        this.configuration = configuration;
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        int index = ParameterHandleUtil.setParameters(configuration, ps, (Object[]) parameterObject.get("parameters"));
        this.pageHelperSetParameters(ps, index);
    }

    private void pageHelperSetParameters(PreparedStatement ps, int index) throws SQLException {
        boolean hasPageHelperFirstParam = false;
        boolean hasPageHelperSecondParam = false;
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            if (PageHelper.PAGE_PARAMETER_FIRST.equals(parameterMapping.getProperty())) {
                hasPageHelperFirstParam = true;
            }
            if (PageHelper.PAGE_PARAMETER_SECOND.equals(parameterMapping.getProperty())) {
                hasPageHelperSecondParam = true;
            }
        }
        if (hasPageHelperFirstParam && parameterObject.containsKey(PageHelper.PAGE_PARAMETER_FIRST)) {
            ps.setObject(++index, parameterObject.get(PageHelper.PAGE_PARAMETER_FIRST));
        }
        if (hasPageHelperSecondParam && parameterObject.containsKey(PageHelper.PAGE_PARAMETER_SECOND)) {
            ps.setObject(++index, parameterObject.get(PageHelper.PAGE_PARAMETER_SECOND));
        }
    }
}
