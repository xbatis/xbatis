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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.sql.executor.Query;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.DbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.JoinMode;
import db.sql.api.impl.tookit.SQLOptimizeUtils;
import db.sql.api.impl.tookit.SQLPrinter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountFromQueryTest extends BaseTest {

    private String getCountSql(Query query) {
        return getCountSql(query, true);
    }

    private String getCountSql(Query query, boolean optimize) {
        //创建构建SQL的上下文 数据库:MYSQL SQL模式 打印
        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(DbType.MYSQL, SQLMode.PRINT);
        String sql = SQLPrinter.sql(query);
        if (!optimize && query.getOptimizeOptions().isOptimizeJoin()) {
            query.getOptimizeOptions().optimizeJoin(optimize);
        }
        String str = SQLOptimizeUtils.getCountSqlFromQuery(query, sqlBuilderContext, query.getOptimizeOptions()).toString();
        assertEquals(sql, SQLPrinter.sql(query), "sql count优化破坏了原来有query");
        return str;
    }

    @Test
    public void simpleOrderBy() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 1)
                                .orderBy(SysUser::getId)
                                .limit(1)
                        , false)
        );

        check("关闭 order by 优化后的count SQL",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 order by t.id asc limit 1 offset 0) t",
                getCountSql(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 1)
                                .optimizeOptions(optimizeOptions -> optimizeOptions.optimizeOrderBy(false))
                                .orderBy(SysUser::getId)
                                .limit(1)
                        , false)
        );

        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );


    }


    @Test
    public void simpleOrderByLimit() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }

    @Test
    public void simpleDistinctOrderBy() {
        check("distinct order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void simpleDistinctOrderByLimit() {
        check("distinct order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }

    @Test
    public void leftJoinOrderBy() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void leftJoinOrderBy2() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );

        check("关闭 left 优化后的count SQL",
                "select count(*) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .optimizeOptions(optimizeOptions -> optimizeOptions.optimizeJoin(false))
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinOrderBy() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinOrderByLimit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 1)
                )
        );
    }

    @Test
    public void distinctLeftJoinOrderBy2() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinOrderBy2Limit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }


    @Test
    public void rightJoinOrderBy() {
        check("right join order by 优化后的count SQL",
                "select count(*) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .rightJoin(SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void rightJoinOrderBy2() {
        check("right join order by 优化后的count SQL",
                "select count(*) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctRightJoinOrderBy() {
        check("right join order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctRightJoinOrderByLimit() {
        check("right join order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 1)
                )
        );
    }

    @Test
    public void distinctRightJoinOrderBy2() {
        check("right join order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctRightJoinOrderBy2Limit() {
        check("right join order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }


    @Test
    public void leftJoin2OrderBy() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void leftJoin2OrderBy2() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoin2OrderBy() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoin2OrderByLimit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 1)
                )
        );
    }

    @Test
    public void distinctLeftJoin2OrderBy2() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoin2OrderBy2Limit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }


    @Test
    public void leftJoinRIGHTOrderBy() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void leftJoinRIGHTOrderBy2() {
        check("order by 优化后的count SQL",
                "select count(*) from t_sys_user t right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinRIGHTOrderBy() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinRIGHTOrderByLimit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name) from t_sys_user t right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 1)
                )
        );
    }

    @Test
    public void distinctLeftJoinRIGHTOrderBy2() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );
    }

    @Test
    public void distinctLeftJoinRIGHTOrderBy2Limit() {
        check("order by 优化后的count SQL",
                "select count(distinct t.id,t.user_name,t2.id) from t_sys_user t left join sys_role t2 on t2.id=t.role_id right join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .select(SysRole::getId)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.RIGHT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 2)
                )
        );
    }


    @Test
    public void unionOrderBy() {
        check("unionOrderBy",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId)
                        )
                )
        );

        check("unionOrderBy",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId)
                        ), false
                )
        );
    }


    @Test
    public void unionOrderByLimit() {
        check("unionOrderBy",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 order by t.id asc limit 3 offset 0 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 3)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId)
                        )
                )
        );
    }


    @Test
    public void optimizeCountSqlTest() {

        check("多个union count limit",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 order by t.id asc limit 1 offset 0 union select t.id,t.user_name from t_sys_user t where t.id=2 union select t.id,t.user_name from t_sys_user t where t.id=2 order by t.id asc limit 2 offset 0) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .limit(0, 1)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId))
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId)
                                .limit(0, 2)
                        )
                )
        );


        check("union count 不优化",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId))
                )
        );

        check("left join count优化",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );

        check("inner join left join count优化",
                "select count(*) from t_sys_user t inner join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.INNER, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );

        check("inner join left join count优化",
                "select count(distinct t.id,t.user_name) from t_sys_user t inner join sys_role t3 on t3.id=t.role_id where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.INNER, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );


        check("order by count优化",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );

        check("order by count优化",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );


        check("多个left join count优化",
                "select count(*) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .join(JoinMode.LEFT, SysUser.class, 1, SysRole.class, 2)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );

        check("join count不优化",
                "select count(*) from t_sys_user t left join sys_role t2 on t2.id=t.role_id where t.id=1 and t2.id=0",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .eq(SysRole::getId, 0)
                        .orderBy(SysUser::getId)
                )
        );


        check("right join count不优化",
                "select count(*) from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1 and t2.id=0",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .eq(SysRole::getId, 0)
                        .orderBy(SysUser::getId)
                )
        );


        check("distinct count 优化",
                "select count(distinct t.id,t.user_name) from t_sys_user t where t.id=1",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                )
        );


        check("group by select 多字段 优化",
                "select count(*) from (select 1 as x$1 from t_sys_user t where t.id=1 group by t.id) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .groupBy(SysUser::getId)
                        .orderBy(SysUser::getId)
                )
        );

        check("group by count 优化join",
                "select count(*) from (select 1 as x$1 from t_sys_user t where t.id=1 group by t.id) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.LEFT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .groupBy(SysUser::getId)
                        .orderBy(SysUser::getId)
                )
        );

        check("group by count 不优化join",
                "select count(*) from (select 1 as x$1 from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1 group by t.id) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .groupBy(SysUser::getId)
                        .orderBy(SysUser::getId)
                )
        );

        check("group by distinct count 不优化join",
                "select count(*) from (select distinct t.id,t.user_name from t_sys_user t right join sys_role t2 on t2.id=t.role_id where t.id=1 group by t.id) t",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .join(JoinMode.RIGHT, SysUser.class, SysRole.class)
                        .eq(SysUser::getId, 1)
                        .groupBy(SysUser::getId)
                        .orderBy(SysUser::getId)
                )
        );


        check("多个union count 不优化",
                "select count(*) from (select t.id,t.user_name from t_sys_user t where t.id=1 union select t.id,t.user_name from t_sys_user t where t.id=2 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId))
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId))
                )
        );


        check("union distinct count 不优化",
                "select count(*) from (select distinct t.id,t.user_name from t_sys_user t where t.id=1 union select t.id,t.user_name from t_sys_user t where t.id=2) t",
                getCountSql(Query.create()
                        .selectDistinct()
                        .select(SysUser::getId, SysUser::getUserName)
                        .from(SysUser.class)
                        .eq(SysUser::getId, 1)
                        .orderBy(SysUser::getId)
                        .union(Query.create()
                                .select(SysUser::getId, SysUser::getUserName)
                                .from(SysUser.class)
                                .eq(SysUser::getId, 2)
                                .orderBy(SysUser::getId))
                )
        );
    }
}
