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

package db.sql.api.impl.cmd.executor;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.tookit.CmdUtils;

import java.util.*;
import java.util.function.BiConsumer;


public abstract class BaseExecutor<SELF extends BaseExecutor<SELF, CMD_FACTORY>, CMD_FACTORY extends CmdFactory> implements Executor<SELF, CMD_FACTORY> {

    protected final List<Cmd> cmds = new ArrayList<>(6);

    private final Map<Class<? extends Cmd>, Integer> cmdSorts = new HashMap<>();

    private List<Selector> selectors;

    private boolean isExecuteSelector = false;

    public BaseExecutor() {
        this.initCmdSorts(cmdSorts);
    }

    private Selector createSelector() {
        if (Objects.isNull(this.selectors)) {
            this.selectors = new ArrayList<>(2);
        }
        DbSelector dbSelector = new DbSelector();
        this.selectors.add(dbSelector);
        return dbSelector;
    }

    @Override
    public SELF dbAdapt(BiConsumer<SELF, Selector> consumer) {
        SELF self = (SELF) this;
        consumer.accept(self, this.createSelector());
        return (SELF) this;
    }

    @Override
    public void selectorExecute(DbType dbType) {
        if (this.isExecuteSelector) {
            return;
        }
        this.isExecuteSelector = true;
        if (Objects.isNull(this.selectors)) {
            return;
        }
        selectors.forEach(dbSelector -> dbSelector.dbExecute(dbType));
    }

    @Override
    public List<Cmd> cmds() {
        return cmds;
    }

    protected void append(Cmd cmd) {
        cmds.add(cmd);
    }

    @Override
    public Map<Class<? extends Cmd>, Integer> cmdSorts() {
        return cmdSorts;
    }

    abstract void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts);

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.cmds);
    }
}
