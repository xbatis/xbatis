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

package cn.xbatis.core.mybatis.provider;


import cn.xbatis.core.mybatis.mapper.context.PreparedContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.Collections;

public class PreparedSQLSqlSource implements SqlSource {

    private final Configuration configuration;

    public PreparedSQLSqlSource(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        PreparedContext preparedContext = (PreparedContext) parameterObject;
        return new BoundSql(this.configuration, preparedContext.getSql(), Collections.singletonList(new ParameterMapping
                .Builder(configuration, "name", Object.class)
                .build()), parameterObject);
    }
}
