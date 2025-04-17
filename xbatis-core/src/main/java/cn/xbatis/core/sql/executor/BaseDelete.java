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
import cn.xbatis.core.sql.util.ReturningClassUtil;
import db.sql.api.Cmd;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.cmd.executor.AbstractDelete;
import db.sql.api.impl.cmd.struct.Where;

import java.util.List;
import java.util.Map;

public abstract class BaseDelete<T extends BaseDelete<T>> extends AbstractDelete<T, MybatisCmdFactory> implements Timeoutable<T> {

    protected Integer timeout;

    public BaseDelete() {
        super(new MybatisCmdFactory());
    }

    public BaseDelete(Where where) {
        super(where);
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
    protected void initCmdSorts(Map<Class<? extends Cmd>, Integer> cmdSorts) {
        super.initCmdSorts(cmdSorts);
        cmdSorts.put(cn.xbatis.core.sql.executor.Where.class, cmdSorts.get(Where.class));
    }

    @Override
    public List<SQLListener> getSQLListeners() {
        return XbatisGlobalConfig.getSQLListeners();
    }

    @Override
    public T returning(Class entity, int storey) {
        ReturningClassUtil.returning(this.$(), this.$returning(), entity, storey);
        return (T) this;
    }

    /**************以下为去除警告************/

    @Override
    @SafeVarargs
    public final T delete(IDataset<?, ?>... tables) {
        return super.delete(tables);
    }

    @Override
    @SafeVarargs
    public final T from(IDataset<?, ?>... tables) {
        return super.from(tables);
    }

    /**************以上为去除警告************/
}
