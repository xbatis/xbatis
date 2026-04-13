/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package db.sql.api.impl.cmd.executor;

import db.sql.api.cmd.ICmdFactory;
import db.sql.api.impl.cmd.CmdFactory;

/**
 * 子查询
 */
public class SubQuery extends AbstractSubQuery<SubQuery, CmdFactory> {

    public SubQuery() {
        this(new CmdFactory(ICmdFactory.SUB_QUERY_TABLE_AS_PREFIX, 1, true));
    }

    public SubQuery(String alias) {
        this(new CmdFactory(ICmdFactory.SUB_QUERY_TABLE_AS_PREFIX, 1, true));
        this.alias = alias;
    }

    public SubQuery(CmdFactory cmdFactory) {
        super(cmdFactory);
    }

    public SubQuery(String alias, CmdFactory cmdFactory) {
        this(cmdFactory);
        this.alias = alias;
    }
}
