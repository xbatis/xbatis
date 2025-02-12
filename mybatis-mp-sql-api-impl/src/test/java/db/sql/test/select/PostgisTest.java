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

import db.sql.api.impl.cmd.executor.Query;
import db.sql.api.impl.cmd.postgis.ST_Point;
import db.sql.test.BaseTest;
import org.junit.jupiter.api.Test;

public class PostgisTest extends BaseTest {

    @Test
    public void testST_DWithin() {
        check("ST_DWithin测试", "select id where ST_DWithin(name,st_srid(point(0.0,0.0),4326),11.0)", new Query()
                .select(userTable().$("id")).where(where -> where.and((q) -> userTable().$("name").ST_DWithin(new ST_Point(0, 0), 11))));
    }

    @Test
    public void testST_Distance() {
        check("ST_Distance测试", "select id, ST_Distance(name,st_srid(point(0.0,0.0),4326))", new Query()
                .select(userTable().$("id"), userTable().$("name").ST_Distance(new ST_Point(0, 0))));
    }

    @Test
    public void testST_Contains() {
        check("ST_Distance测试", "select id, ST_Contains(name,st_srid(point(0.0,0.0),4326))", new Query()
                .select(userTable().$("id"), userTable().$("name").ST_Contains(new ST_Point(0, 0))));
    }
}
