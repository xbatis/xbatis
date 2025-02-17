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

package db.sql.api.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.tookit.CmdUtils;

import java.util.Objects;

public class SQLCmdAll implements Cmd {

    public final static SQLCmdAll INSTANCE = new SQLCmdAll();

    private IDataset<?, ?> dataset;

    public SQLCmdAll() {

    }

    public SQLCmdAll(IDataset<?, ?> dataset) {
        this.dataset = dataset;
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (Objects.isNull(dataset)) {
            sqlBuilder.append(" * ");
        } else {
            String name;
            if (dataset instanceof ITable) {
                ITable table = (ITable) dataset;
                name = Objects.isNull(table.getAlias()) ? table.getName() : table.getAlias();
            } else {
                name = dataset.getAlias();
            }
            sqlBuilder.append(name).append(".* ");
        }
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, dataset);
    }
}
