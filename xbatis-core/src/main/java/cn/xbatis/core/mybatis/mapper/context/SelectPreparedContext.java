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

import cn.xbatis.core.sql.executor.Where;
import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SqlConst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectPreparedContext<T> extends PreparedContext {

    private Class<T> returnType;

    private String sql;

    private Object[] parameters;

    public SelectPreparedContext(Class<T> returnType, String sql, Object[] params) {
        super(sql, params);
        this.returnType = returnType;
    }

    public Class<T> getReturnType() {
        return returnType;
    }

    public void initWithDbType(DbType dbType) {
        if (sql == null) {
            List<Object> args = new ArrayList<>();
            Object[] params = super.getParameters();

            boolean existsCmd = Arrays.stream(params).anyMatch(i -> i instanceof Cmd);
            if (existsCmd) {
                String[] sqls = super.getSql().split("\\?");
                if (sqls.length != params.length && sqls.length != params.length + 1) {
                    throw new IllegalArgumentException("The number of parameters does not match");
                }

                StringBuilder sql = new StringBuilder();
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    sql.append(sqls[i]);
                    if (param instanceof Cmd) {
                        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(dbType, SQLMode.PREPARED) {
                            @Override
                            public String addParam(Object value) {
                                args.add(value);
                                return super.addParam(value);
                            }
                        };
                        StringBuilder cmdSql = ((Cmd) param).sql(null, null, sqlBuilderContext, new StringBuilder());
                        if (param instanceof Where) {
                            Where where = (Where) param;
                            if (where.hasContent()) {
                                sql.append(cmdSql.toString().replaceFirst(new String(SqlConst.WHERE), ""));
                            } else {
                                Methods.TRUE().sql(null, null, sqlBuilderContext, sql);
                            }
                        } else {
                            ((Cmd) param).sql(null, null, sqlBuilderContext, sql);
                        }
                    } else {
                        sql.append("?");
                        args.add(param);
                    }
                }

                // 补充最后面的sql
                if (sqls.length == params.length + 1) {
                    sql.append(sqls[sqls.length - 1]);
                }

                this.parameters = args.toArray();
                this.sql = sql.toString();
            } else {
                this.parameters = super.getParameters();
                this.sql = super.getSql();
            }
        }
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public Object[] getParameters() {
        if (parameters == null) {
            return super.getParameters();
        }
        return parameters;
    }
}
