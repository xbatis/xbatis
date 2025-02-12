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

package db.sql.api.cmd;

public enum JoinMode {

    LEFT(" LEFT JOIN "), RIGHT(" RIGHT JOIN "), INNER(" INNER JOIN "), FULL(" FULL OUTER JOIN ");

    private final String sql;

    JoinMode(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

}
