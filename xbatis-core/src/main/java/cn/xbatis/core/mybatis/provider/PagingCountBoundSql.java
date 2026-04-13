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

package cn.xbatis.core.mybatis.provider;

import db.sql.api.IDbType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;


public class PagingCountBoundSql extends DbTypeBoundSql {

    private final String sql;

    private final BoundSql delegate;

    public PagingCountBoundSql(IDbType dbType, Configuration configuration, String sql, BoundSql delegate) {
        super(dbType, configuration, null, null, null);
        this.sql = sql;
        this.delegate = delegate;
    }


    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public List<ParameterMapping> getParameterMappings() {
        return delegate.getParameterMappings();
    }

    @Override
    public Object getParameterObject() {
        return delegate.getParameterObject();
    }

    @Override
    public boolean hasAdditionalParameter(String name) {
        return delegate.hasAdditionalParameter(name);
    }

    @Override
    public void setAdditionalParameter(String name, Object value) {
        delegate.setAdditionalParameter(name, value);
    }

    @Override
    public Object getAdditionalParameter(String name) {
        return delegate.getAdditionalParameter(name);
    }

    @Override
    public Map<String, Object> getAdditionalParameters() {
        return delegate.getAdditionalParameters();
    }
}
