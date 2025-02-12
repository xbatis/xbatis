/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package db.sql.test.select;

import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.basic.OrderByDirection;
import db.sql.api.impl.cmd.executor.Query;
import db.sql.api.impl.cmd.executor.WithQuery;
import db.sql.test.BaseTest;
import db.sql.test.Entity;
import db.sql.test.Entity2;
import org.junit.jupiter.api.Test;

public class QueryTest extends BaseTest {


    @Test
    public void selectTest() {


        check("select测试", "SELECT id,name", new Query()
                .select(userTable().$("id"), userTable().$("name")));

        check("from测试", "SELECT id,name FROM user", new Query()
                .select(userTable().$("id"), userTable().$("name")).from(userTable()));

        check("join测试", "SELECT t.id,t.name FROM user t inner join role t2 on t.role_id=t2.id", new Query()
                .select(userTable().as("t").$("id"), userTable().as("t").$("name"))
                .from(userTable().as("t"))
                .join(userTable().as("t"), roleTable().as("t2"), on -> {
                    on.conditionChain().eq(userTable().as("t").$("role_id"), roleTable().as("t2").$("id"));
                })
        );


        check("where测试",
                "SELECT id FROM user where id=1 and name !='xx' and id>=2 and id>3" +
                        " and id<=4 and id<5 or name like concat('%' , 'abc' ,'%') and name not like concat('%','abcd') and id between 6 and 7" +
                        " and id not between 8 and 9",
                new Query()
                        .select(userTable().$("id")).from(userTable())
                        .eq(userTable().$("id"), 1)
                        .ne(userTable().$("name"), "xx")
                        .gte(userTable().$("id"), 2)
                        .gt(userTable().$("id"), 3)
                        .lte(userTable().$("id"), 4)
                        .lt(userTable().$("id"), 5)
                        .or()
                        .like(userTable().$("name"), "abc")
                        .and()
                        .notLike(LikeMode.LEFT, userTable().$("name"), "abcd")
                        .between(userTable().$("id"), 6, 7)
                        .notBetween(userTable().$("id"), 8, 9)
        );

        check("别名测试", "SELECT t.id FROM user t where t.id=1 and t.name='xx'", new Query()
                .select(userTable().as("t").$("id")).from(userTable().as("t"))
                .eq(userTable().as("t").$("id"), 1)
                .eq(userTable().as("t").$("name"), "xx")
        );

        check("or测试", "SELECT t.id FROM user t where t.id=1 or t.name='xx'", new Query()
                .select(userTable().as("t").$("id")).from(userTable().as("t"))
                .eq(userTable().as("t").$("id"), 1)
                .or()
                .eq(userTable().as("t").$("name"), "xx")
        );

        check("or内嵌测试", "SELECT t.id FROM user t where t.id=1 or (t.name='xx')", new Query()
                .select(userTable().as("t").$("id")).from(userTable().as("t"))
                .eq(userTable().as("t").$("id"), 1)
                .orNested(conditionChain -> conditionChain.eq(userTable().as("t").$("name"), "xx"))

        );


        check("select 函数测试", "SELECT max(id),count(name)", new Query()
                .select(userTable().$("id").max(), userTable().$("name").count()));


        check("select group having order by函数测试", "select max(id),count(name) from user group by id having count(id)>1 order by id desc", new Query()
                .select(userTable().$("id").max(), userTable().$("name").count()).from(userTable())
                .groupBy(userTable().$("id"))
                .having(having -> having.and(f -> f.field(userTable(), "id").count().gt(1)))
                .orderBy(OrderByDirection.DESC, userTable().$("id"))
        );

        check("select group having order by函数测试", "SELECT max(id),count(name) from user group by id having count(id)>1 order by id desc", new Query()
                .select(userTable().$("id").max(), userTable().$("name").count()).from(userTable())
                .groupBy(userTable().$("id"))
                .having(having -> having.and(f -> f.field(userTable(), "id").count().gt(1)))
                .orderBy(OrderByDirection.DESC, userTable().$("id"))
        );


        check("函数测试",
                "SELECT max(id),min(id),count(name),abs(id),avg(id),round(count(id) , 2)," +
                        "(id - 1),(id - id),(id + 1),(id + id),(id * 1),(id * id),(id / 1),(id / id)," +
                        "concat(id,1,'2',3),concat(id,id)," +
                        "concat_ws('$',id,1,'2',3),concat_ws('$',id,id)," +
                        "concat(count(id),'abc')," +
                        "(case when id>2 then 1 else 2 end)," +
                        "(case when id<3 then 1 when id<2 then 2 else 3 end) as xx" +
                        " from user",

                new Query()
                        .select(
                                userTable().$("id").max(),
                                userTable().$("id").min(),
                                userTable().$("name").count(),
                                userTable().$("id").abs(),
                                userTable().$("id").avg(),
                                userTable().$("id").count().round(2),
                                userTable().$("id").subtract(1),
                                userTable().$("id").subtract(userTable().$("id")),
                                userTable().$("id").plus(1),
                                userTable().$("id").plus(userTable().$("id")),
                                userTable().$("id").multiply(1),
                                userTable().$("id").multiply(userTable().$("id")),
                                userTable().$("id").divide(1),
                                userTable().$("id").divide(userTable().$("id")),
                                userTable().$("id").concat(1, "2", 3),
                                userTable().$("id").concat(userTable().$("id")),
                                userTable().$("id").concatWs("$", 1, "2", 3),
                                userTable().$("id").concatWs("$", userTable().$("id")),
                                userTable().$("id").count().concat("abc"),
                                userTable().$("id").gt(2).if_(1, 2),
                                userTable().$("id").lt(3).caseThen(1).when(
                                        userTable().$("id").lt(2), 2
                                ).else_(
                                        3
                                ).as("xx")
                        ).from(userTable())


        );


        check("实体类join", "SELECT t.id,t2.name from Entity t inner join Entity2 t2 on t.id=t2.id where t.id>t2.id", new Query()
                .select(Entity::getId)
                .select(Entity2::getName)
                .from(Entity.class)
                .join(Entity.class, Entity2.class, on -> on.eq(Entity::getId, Entity2::getId))
                .gt(Entity::getId, Entity2::getId)
        );


        WithQuery withQuery = new WithQuery("a").select(userTable().$("id")).from(userTable());
        check("WITH 测试", "with a as (select id from user) select id,name,a.id", new Query()
                .with(withQuery)
                .select(userTable().$("id"), userTable().$("name"))
                .select(withQuery, "id")
        );


        check("forceIndex测试", "from user t force index(aa) inner join role t2 force index(bb) on id=t2.id", new Query()
                .from(userTable().as("t").forceIndex("aa")).join(userTable(), roleTable().as("t2").forceIndex("bb"), on -> {
                    on.eq(on.getJoin().getMainTable().$("id"), on.getJoin().getSecondTable().$("id"));
                }));
    }
}
