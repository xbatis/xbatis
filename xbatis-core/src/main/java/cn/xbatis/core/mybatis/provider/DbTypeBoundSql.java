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

import cn.xbatis.core.dbType.IDbTypeContext;
import db.sql.api.IDbType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.List;

public class DbTypeBoundSql extends BoundSql implements IDbTypeContext {

    private final IDbType dbType;

    public DbTypeBoundSql(IDbType dbType, Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        super(configuration, sql, parameterMappings, parameterObject);
        this.dbType = dbType;
    }

    @Override
    public IDbType getDbType() {
        return dbType;
    }
}
