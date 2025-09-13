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

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedParameterHandler implements ParameterHandler {

    private final PreparedParameterContext parameterContext;

    private final MybatisConfiguration configuration;

    public PreparedParameterHandler(MybatisConfiguration configuration, PreparedParameterContext parameterContext) {
        this.configuration = configuration;
        this.parameterContext = parameterContext;
    }

    @Override
    public Object getParameterObject() {
        return parameterContext;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        ParameterHandleUtil.setParameters(configuration, ps, parameterContext.getParameters());
    }
}
