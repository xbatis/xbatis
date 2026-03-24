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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.dbType.IDbTypeContext;
import cn.xbatis.core.dbType.IDbTypeInitContext;
import db.sql.api.Cmd;
import db.sql.api.IDbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.SqlConst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreparedContext implements PreparedParameterContext, IDbTypeInitContext, IDbTypeContext {

    private String sql;

    private List<Object> params;

    private IDbType dbType;

    public PreparedContext(String sql, Object[] params) {
        this.sql = sql;
        this.params = Arrays.asList(params);
    }

    private static Object[] buildSqlAndArgsWithDbType(String originalSql, List<Object> originalArgs, IDbType dbType) {
        int paramSize = originalArgs.size();
        String[] sqls = originalSql.split("\\?");
        if (sqls.length != paramSize && sqls.length != paramSize + 1) {
            throw new IllegalArgumentException("The number of parameters does not match");
        }
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        int i = -1;
        for (Object param : originalArgs) {
            i++;
            sql.append(sqls[i]);
            if (param instanceof Cmd) {
                SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(dbType, SQLMode.PREPARED) {
                    @Override
                    public String addParam(Object value) {
                        args.add(value);
                        return super.addParam(value);
                    }
                };
                StringBuilder cmdSql;
                if (param instanceof Where) {
                    Where where = (Where) param;
                    if (where != null && where.hasContent()) {
                        cmdSql = ((Cmd) param).sql(null, null, sqlBuilderContext, new StringBuilder());
                        sql.append(cmdSql.toString().replaceFirst(new String(SqlConst.WHERE), ""));
                    } else {
                        Methods.TRUE().sql(null, null, sqlBuilderContext, sql);
                    }
                } else {
                    if (param != null) {
                        ((Cmd) param).sql(null, null, sqlBuilderContext, sql);
                    }
                }
            } else {
                if (param != null) {
                    sql.append("?");
                    args.add(param);
                }
            }
        }

        // 补充最后面的sql
        if (sqls.length == paramSize + 1) {
            sql.append(sqls[sqls.length - 1]);
        }

        return new Object[]{sql.toString(), args};
    }


    @Override
    public void init(IDbType dbType) {
        if (this.dbType != null) {
            if (this.dbType != dbType) {
                throw new RuntimeException("dbType why not same:" + this.dbType + "," + dbType);
            }
            return;
        }

        this.dbType = dbType;

        boolean existsCmd = params.stream().anyMatch(i -> i instanceof Cmd);
        if (existsCmd) {
            Object[] objs = buildSqlAndArgsWithDbType(this.sql, params, dbType);
            this.sql = (String) objs[0];
            this.params = (List<Object>) objs[1];
        }
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


}
