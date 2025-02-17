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

package db.sql.api.cmd.struct;


import db.sql.api.Cmd;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;
import db.sql.api.cmd.executor.method.IConditionMethod;
import db.sql.api.cmd.struct.conditionChain.IConditionChain;

public interface IOn<SELF extends IOn<SELF, JOIN, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        JOIN extends IJoin<JOIN, SELF, TABLE, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        TABLE extends ITable<TABLE, TABLE_FIELD>,
        TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>,
        COLUMN extends Cmd,
        V,
        CONDITION_CHAIN extends IConditionChain<CONDITION_CHAIN, TABLE_FIELD, COLUMN, V>>
        extends IConditionMethod<SELF, TABLE_FIELD, COLUMN, V, CONDITION_CHAIN>,
        Cmd {


    JOIN getJoin();
}
