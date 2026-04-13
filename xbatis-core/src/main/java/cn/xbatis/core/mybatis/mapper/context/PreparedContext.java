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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.dbType.IDbTypeContext;
import cn.xbatis.core.dbType.IDbTypeSetContext;
import cn.xbatis.core.mybatis.executor.XbatisArgsWrapperUtil;
import db.sql.api.Cmd;
import db.sql.api.IDbType;

import java.util.Arrays;
import java.util.List;

public class PreparedContext implements PreparedParameterContext, IDbTypeSetContext, IDbTypeContext {

    private final Object[] originalParams;
    private String sql;
    private List<Object> params;
    private IDbType dbType;

    public PreparedContext(String sql, Object[] params) {
        this.sql = sql;
        this.params = Arrays.asList(params);
        this.originalParams = params;
    }

    public String getSql() {
        if (this.dbType == null) {
            throw new NullPointerException("dbType is not initialized");
        }
        return sql;
    }

    @Override
    public List<Object> getParameters() {
        if (this.dbType == null) {
            throw new NullPointerException("dbType is not initialized");
        }
        return this.params;
    }

    @Override
    public IDbType getDbType() {
        return this.dbType;
    }

    @Override
    public void setDbType(IDbType dbType) {
        if (this.dbType != null) {
            if (this.dbType != dbType) {
                throw new RuntimeException("dbType why not same:" + this.dbType + "," + dbType);
            }
            return;
        }

        this.dbType = dbType;

        boolean existsCmd = params.stream().anyMatch(i -> i instanceof Cmd);
        if (existsCmd) {
            Object[] objs = XbatisArgsWrapperUtil.buildSqlAndArgsWithDbType(this.sql, params, dbType);
            this.sql = (String) objs[0];
            this.params = (List<Object>) objs[1];
        }
    }

    public Object[] getOriginalParams() {
        return originalParams;
    }
}
