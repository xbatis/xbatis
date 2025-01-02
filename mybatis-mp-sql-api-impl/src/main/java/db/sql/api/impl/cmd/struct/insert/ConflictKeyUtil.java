/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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

package db.sql.api.impl.cmd.struct.insert;


import db.sql.api.DbType;
import db.sql.api.impl.cmd.executor.AbstractInsert;

public class ConflictKeyUtil {

    public final static void addDefaultConflictKeys(AbstractInsert insert, DbType dbType) {
        if (insert.getConflictAction() == null) {
            return;
        }
        if (insert.getConflictAction().getConflictKeys() != null) {
            return;
        }

        if (dbType == DbType.ORACLE
                || ((dbType == DbType.PGSQL || dbType == DbType.KING_BASE) && !insert.getConflictAction().isDoNothing())
                || ((dbType == DbType.OPEN_GAUSS || dbType == DbType.SQLITE) && insert.getConflictAction().getConflictUpdate() == null)) {
            String[] conflictKeys = insert.getInsertTable().getTable().getIds();
            if (conflictKeys != null && conflictKeys.length > 0) {
                insert.getConflictAction().conflictKeys(conflictKeys);
            }
        }
    }
}
