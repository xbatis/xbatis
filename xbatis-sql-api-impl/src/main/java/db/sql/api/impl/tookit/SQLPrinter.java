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

package db.sql.api.impl.tookit;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;

public class SQLPrinter {
    public static String sql(Cmd cmd) {
        return sql(DbType.MYSQL, cmd);
    }

    public static String sql(DbType dbType, Cmd cmd) {
        //创建构建SQL的上下文 数据库:MYSQL SQL模式 打印
        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(dbType, SQLMode.PRINT);
        return cmd.sql(cmd, cmd, sqlBuilderContext, new StringBuilder()).toString();
    }

    public static String preparedSQL(DbType dbType, Cmd cmd) {
        //创建构建SQL的上下文 数据库:MYSQL SQL模式 打印
        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(dbType, SQLMode.PREPARED);
        return cmd.sql(cmd, cmd, sqlBuilderContext, new StringBuilder()).toString();
    }

    public static void print(Cmd cmd) {
        print(DbType.MYSQL, cmd);
    }

    public static void print(DbType dbType, Cmd cmd) {
        System.out.println(sql(dbType, cmd));
    }
}
