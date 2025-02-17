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

package db.sql.test.update;


import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.executor.Update;
import db.sql.test.BaseTest;
import org.junit.jupiter.api.Test;

public class UpdateTest extends BaseTest {

    @Test
    void updateTest() {
        check("update 简单", "update user set name='xx' where id=1",
                new Update().update(userTable()).set(userTable().$("name"), "xx").eq(userTable().$("id"), 1)
        );

        check("update 别名", "update user t set t.name='xx' where t.id=1",
                new Update().update(userTable().as("t")).set(userTable().as("t").$("name"), "xx").eq(userTable().as("t").$("id"), 1)
        );


        Table userTable = userTable();

        check("update", "update user set id=2 where id=1",
                new Update().update(userTable).set(userTable.$("id"), 2).eq(userTable().$("id"), 1)
        );
        Table userTable2 = userTable();
        check("update", " update user t,user t2 set t2.id=2 where t.id=t2.id",
                new Update().update(userTable.as("t"), userTable2.as("t2")).set(userTable2.$("id"), 2).eq(userTable.$("id"), userTable2.$("id"))
        );

        check("update", "update user t inner join user t2 on t.id=t2.id set t2.id=2",
                new Update().update(userTable).set(userTable2.$("id"), 2).join(userTable, userTable2, on -> on.eq(on.getJoin().getMainTable().$("id"), userTable2.$("id")))
        );

        userTable = userTable();
        check("update", "update user set id=2 where id=1",
                new Update().update(userTable).set(userTable.$("id"), 2).eq(userTable.$("id"), 1)
        );


    }
}
