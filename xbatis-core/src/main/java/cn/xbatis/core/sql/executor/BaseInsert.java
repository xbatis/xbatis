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

package cn.xbatis.core.sql.executor;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.executor.statement.Timeoutable;
import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.TableSplitUtil;
import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.basic.IConflictAction;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.cmd.basic.TableField;
import db.sql.api.impl.cmd.executor.AbstractInsert;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseInsert<T extends BaseInsert<T>> extends AbstractInsert<T, MybatisCmdFactory> implements Timeoutable<T> {

    protected Integer timeout;

    public BaseInsert() {
        super(new MybatisCmdFactory());
    }

    @Override
    public T timeout(Integer timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    @Override
    @SafeVarargs
    public final <T2> T fields(Getter<T2>... fields) {
        return super.fields(fields);
    }

    @Override
    public List<SQLListener> getSQLListeners() {
        return XbatisGlobalConfig.getSQLListeners();
    }

    @Override
    public <T1> T onConflict(Consumer<IConflictAction<T1>> action) {
        return super.onConflict(action);
    }

    /**************以下为去除警告************/

    @Override
    @SafeVarargs
    public final T fields(TableField... fields) {
        return super.fields(fields);
    }

    /**************以上为去除警告************/

    private void splitTableHandle(MpTable table) {
        if (!TableSplitUtil.isNeedSplitHandle(table)) {
            //无需处理分表操作 返回
            return;
        }

        List<TableField> insertFields = getInsertFields().getFields();
        int splitTableKeyIndex = -1;
        for (int i = 0; i < insertFields.size(); i++) {
            TableField item = insertFields.get(i);
            MpTableField tableField = (MpTableField) item;
            if (tableField.getTableFieldInfo().isTableSplitKey()) {
                splitTableKeyIndex = i;
            }
        }

        if (splitTableKeyIndex == -1) {
            throw new RuntimeException("Not found the split field in insert fields");
        }

        if (getInsertValues() == null) {
            List<Cmd> selectValues = getInsertSelect().getSelectQuery().getSelect().getSelectField();
            TableSplitUtil.splitHandle(table, selectValues.get(splitTableKeyIndex));
        } else {
            List<List<Cmd>> insertValuesList = getInsertValues().getValues();
            for (List<Cmd> cmdList : insertValuesList) {
                TableSplitUtil.splitHandle(table, cmdList.get(splitTableKeyIndex));
                if (!TableSplitUtil.isNeedSplitHandle(table)) {
                    break;
                }
            }
        }
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        this.selectorExecute(context.getDbType());
        if (getInsertTable().getTable() instanceof MpTable) {
            MpTable table = (MpTable) getInsertTable().getTable();
            this.splitTableHandle(table);
        }

        return super.sql(module, parent, context, sqlBuilder);
    }
}
