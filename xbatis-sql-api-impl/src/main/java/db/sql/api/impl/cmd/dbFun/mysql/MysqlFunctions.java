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
import db.sql.api.impl.tookit.Objects;

import java.io.Serializable;

public class MysqlFunctions {

    private final Cmd key;

    public MysqlFunctions(Cmd key) {
        this.key = key;
    }

    public JsonExtract jsonExtract(String... paths) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonExtract(this.key, paths);
    }

    public JsonExtract jsonExtract(Cmd column, String... paths) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set distinct on key ?");
        }
        return Methods.mysqlJsonExtract(column, paths);
    }

    public JsonContainsPath jsonContainsPath(String... paths) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContainsPath(this.key, paths);
    }

    public JsonContainsPath jsonContainsPath(Cmd column, String... paths) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContainsPath(column, paths);
    }

    public JsonContainsPath jsonContainsPath(boolean allMatch, String... paths) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContainsPath(this.key, allMatch, paths);
    }

    public JsonContainsPath jsonContainsPath(Cmd column, boolean allMatch, String... paths) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContainsPath(column, allMatch, paths);
    }

    public JsonContains jsonContains(Serializable containValue) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContains(this.key, containValue);
    }

    public JsonContains jsonContains(Cmd column, Serializable containValue) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContains(column, containValue);
    }

    public JsonContains jsonContains(Serializable containValue, String path) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContains(this.key, containValue, path);
    }

    public JsonContains jsonContains(Cmd column, Serializable containValue, String path) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set json column ?");
        }
        return Methods.mysqlJsonContains(column, containValue, path);
    }

    public FindInSet findInSet(String str) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set findInSet column ?");
        }
        return Methods.mysqlFindInSet(this.key, str);
    }

    public FindInSet findInSet(Cmd column, String str) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set findInSet column ?");
        }
        return Methods.mysqlFindInSet(column, str);
    }

    public FindInSet findInSet(Number value) {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set findInSet column ?");
        }
        return Methods.mysqlFindInSet(this.key, value);
    }

    public FindInSet findInSet(Cmd column, Number value) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set findInSet column ?");
        }
        return Methods.mysqlFindInSet(column, value);
    }

    public Field filed(Object... values) {
        return Methods.mysqlFiled(key, values);
    }

    public FromUnixTime fromUnixTime() {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set fromUnixTime column ?");
        }
        return Methods.mysqlFromUnixTime(this.key);
    }

    public FromUnixTime fromUnixTime(Cmd column) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set fromUnixTime column ?");
        }
        return Methods.mysqlFromUnixTime(column);
    }

    public Md5 md5() {
        if (Objects.isNull(this.key)) {
            throw new RuntimeException("Need set md5 column ?");
        }
        return Methods.mysqlMd5(this.key);
    }

    public Md5 md5(Cmd column) {
        if (Objects.isNull(column)) {
            throw new RuntimeException("Need set md5 column ?");
        }
        return Methods.mysqlMd5(column);
    }
}
