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

package db.sql.test;

import db.sql.api.Cmd;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.tookit.SQLPrinter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTest {
    protected Table userTable() {
        return new Table("user");
    }

    protected Table roleTable() {
        return new Table("role");
    }

    private String trim(String sql) {
        return sql.replaceAll("  ", " ")
                .replaceAll(" ,", ",")
                .replaceAll(", ", ",")
                .replaceAll(" =", "=")
                .replaceAll("= ", "=")


                .replaceAll("\\( ", "(")

                .replaceAll(" \\)", ")")

                .replaceAll("> ", ">")
                .replaceAll(" >", ">")
                .replaceAll("< ", "<")
                .replaceAll(" <", "<")

                .toLowerCase().trim();
    }

    public void check(String message, String targetSql, Cmd cmd) {
        String sql1 = trim(targetSql);
        String sql2 = trim(SQLPrinter.sql(cmd));
        System.out.println("sql1:  " + sql1);
        System.out.println("sql2:  " + sql2);
        assertEquals(sql1, sql2, message);
    }
}
