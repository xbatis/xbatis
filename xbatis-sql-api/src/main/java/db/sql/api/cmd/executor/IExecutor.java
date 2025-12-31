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
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.ITable;
import db.sql.api.cmd.basic.ITableField;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.tookit.CmdUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface IExecutor<T extends IExecutor,
        TABLE extends ITable<TABLE, TABLE_FIELD>,
        TABLE_FIELD extends ITableField<TABLE_FIELD, TABLE>
        >

        extends Cmd {

    Map<Class<? extends Cmd>, Integer> cmdSorts();

    List<Cmd> cmds();

    void selectorExecute(DbType dbType);

    /**
     * 内联，用于获取自身
     *
     * @param consumer
     * @return 自己
     */
    default T connect(Consumer<T> consumer) {
        return this.connect(true, consumer);
    }

    /**
     * 内联，用于获取自身
     *
     * @param consumer
     * @return 自己
     */
    default T connect(boolean when, Consumer<T> consumer) {
        if (!when) {
            return (T) this;
        }
        consumer.accept((T) this);
        return (T) this;
    }

    default Comparator<Cmd> comparator() {
        return (o1, o2) -> {
            Integer n1 = cmdSorts().get(o1.getClass());
            Integer n2 = cmdSorts().get(o2.getClass());
            if (n1 == null && n2 == null) {
                return 0;
            }
            if (n1 == null) {
                return 1;
            }
            if (n2 == null) {
                return -1;
            }
            return n1.compareTo(n2);
        };
    }


    default List<Cmd> sortedCmds() {
        List<Cmd> cmdList = cmds();
        if (cmdList == null || cmdList.isEmpty()) {
            return cmdList;
        }
        Comparator<Cmd> comparator = comparator();
        cmdList = cmdList.stream().sorted(comparator).collect(Collectors.toList());
        return cmdList;
    }


    @Override
    default StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        this.selectorExecute(context.getDbType());
        List<Cmd> cmdList = cmds();
        if (cmdList == null || cmdList.isEmpty()) {
            return sqlBuilder;
        }
        List<Cmd> sortedCmds = this.sortedCmds();
        if (sortedCmds == null || sortedCmds.isEmpty()) {
            return sqlBuilder;
        }
        return CmdUtils.join(this, this, context, sqlBuilder, sortedCmds);
    }

    default List<SQLListener> getSQLListeners() {
        return Collections.emptyList();
    }
}
