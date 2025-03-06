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

package db.sql.api.impl.cmd.dbFun.mysql;

import db.sql.api.Cmd;
import db.sql.api.impl.cmd.Methods;

import java.io.Serializable;

public class MysqlFunctions {

    private final Cmd key;

    public MysqlFunctions(Cmd key) {
        this.key = key;
    }

    public JsonExtract jsonExtract(String... paths) {
        return Methods.mysqlJsonExtract(this.key, paths);
    }

    public JsonContainsPath jsonContainsPath(String... paths) {
        return Methods.mysqlJsonContainsPath(this.key, paths);
    }

    public JsonContainsPath jsonContainsPath(boolean allMatch, String... paths) {
        return Methods.mysqlJsonContainsPath(this.key, allMatch, paths);
    }

    public JsonContains jsonContains(Serializable containValue) {
        return Methods.mysqlJsonContains(this.key, containValue);
    }

    public JsonContains jsonContains(Serializable containValue, String path) {
        return Methods.mysqlJsonContains(this.key, containValue, path);
    }

    public FindInSet findInSet(String str) {
        return Methods.mysqlFindInSet(this.key, str);
    }

    public FindInSet findInSet(Number value) {
        return Methods.mysqlFindInSet(this.key, value);
    }

    public Field filed(Object... values) {
        return Methods.mysqlFiled(key, values);
    }

    public FromUnixTime fromUnixTime() {
        return Methods.mysqlFromUnixTime(this.key);
    }

    public Md5 md5() {
        return Methods.mysqlMd5(this.key);
    }
}
