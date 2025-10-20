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

package db.sql.api.cmd.executor;

import db.sql.api.Cmd;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;
import db.sql.api.cmd.executor.method.*;
import db.sql.api.cmd.struct.IFrom;
import db.sql.api.cmd.struct.IJoin;
import db.sql.api.cmd.struct.IOn;
import db.sql.api.cmd.struct.IWhere;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;
import db.sql.api.cmd.struct.delete.IDeleteTable;
import db.sql.api.cmd.struct.query.IReturning;

public interface IDelete<SELF extends IDelete,
        TABLE extends ITable<TABLE, TABLE_FIELD>,
        TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd,
        V,
        CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>,
        DELETE_TABLE extends IDeleteTable,
        FROM extends IFrom,
        JOIN extends IJoin<JOIN, ON, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        ON extends IOn<ON, JOIN, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        WHERE extends IWhere<WHERE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        RETURNING extends IReturning<RETURNING>
        >

        extends IDeleteMethod<SELF>,
        IFromMethod<SELF, TABLE, TABLE_FIELD>,
        IJoinMethod<SELF, JOIN, ON>,
        IWhereMethod<SELF, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        IReturningMethod<SELF, TABLE, TABLE_FIELD, COLUMN>,
        IExecutor<SELF, TABLE, TABLE_FIELD> {

    DELETE_TABLE $delete(IDataset... tables);

    FROM $from(IDataset table);

    WHERE $where();

    default WHERE where() {
        return this.$where();
    }

    RETURNING $returning();

    @Override
    default SELF delete(IDataset<?, ?>... tables) {
        $delete(tables);
        return (SELF) this;
    }

    @Override
    default SELF from(IDataset<?, ?>... tables) {
        for (IDataset<?, ?> table : tables) {
            $from(table);
        }
        return (SELF) this;
    }

    @Override
    default CONDITION_CHAIN conditionChain() {
        return $where().conditionChain();
    }
}
