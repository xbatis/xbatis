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

package db.sql.test.insert;

import db.sql.api.impl.cmd.executor.Insert;
import db.sql.test.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class InsertTest extends BaseTest {

    @Test
    void insertTest() {
        check("insert常规插入", "insert into user (id,name) values (1,'2')"
                , new Insert().insert(userTable())
                        .fields(userTable().$("id"), userTable().$("name"))
                        .values(Arrays.asList(1, "2"))
        );

        check("insert批量插入", "insert into user (id,name) values (1,'2'),(2,'3')"
                , new Insert().insert(userTable())
                        .fields(userTable().$("id"), userTable().$("name"))
                        .values(Arrays.asList(1, "2"))
                        .values(Arrays.asList(2, "3"))
        );
    }
}
