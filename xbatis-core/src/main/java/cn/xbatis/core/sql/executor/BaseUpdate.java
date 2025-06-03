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
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.Cmd;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.listener.SQLListener;
import db.sql.api.impl.cmd.executor.AbstractUpdate;
import db.sql.api.impl.cmd.struct.Where;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class BaseUpdate<T extends BaseUpdate<T>> extends AbstractUpdate<T, MybatisCmdFactory> implements Timeoutable<T> {

    protected Integer timeout;

    public BaseUpdate() {
        this(new MybatisCmdFactory());
    }

    public BaseUpdate(MybatisCmdFactory mybatisCmdFactory) {
        super(mybatisCmdFactory);
    }

    public BaseUpdate(Where where) {
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

    /**
     * 追加非null，非空的字段值的条件
     *
     * @param object 对象类上必须有实体类注解或@ConditionTarget
     * @return Q
     * @see cn.xbatis.db.annotations.ConditionTarget @ConditionTarget 条件目标注解
     * @see cn.xbatis.db.annotations.Condition @Condition条件注解
     */
    public T where(Object object) {
        return WhereUtil.where((T) this, object);
    }

    @Override
    public T returning(Class entity, int storey) {
        ReturningClassUtil.returning(this.$(), this.$returning(), entity, storey);
        return (T) this;
    }

    @Override
    public List<SQLListener> getSQLListeners() {
        return XbatisGlobalConfig.getSQLListeners();
    }

    private <T2> SubQuery buildExistsSubQuery(Class<T2> entity, BiConsumer<T, SubQuery> consumer) {
        SubQuery subQuery = this.$().createSubQuery();
        consumer.accept((T) this, subQuery);
        if (subQuery.getSelect() == null || subQuery.getSelect().getSelectField().isEmpty()) {
            subQuery.select1();
        }
        if (subQuery.getFrom() == null) {
            subQuery.from(entity);
        }
        return subQuery;
    }

    public <T2> T exists(Class<T2> entity, BiConsumer<T, SubQuery> consumer) {
        return this.exists(true, entity, consumer);
    }

    public <T2> T exists(boolean when, Class<T2> entity, BiConsumer<T, SubQuery> consumer) {
        if (!when) {
            return (T) this;
        }
        return (T) this.exists(this.buildExistsSubQuery(entity, consumer));
    }


    public <T2> T notExists(Class<T2> entity, BiConsumer<T, SubQuery> consumer) {
        return this.notExists(true, entity, consumer);
    }

    public <T2> T notExists(boolean when, Class<T2> entity, BiConsumer<T, SubQuery> consumer) {
        if (!when) {
            return (T) this;
        }
        return (T) this.notExists(this.buildExistsSubQuery(entity, consumer));
    }

    /**************以下为去除警告************/
    @Override
    @SafeVarargs
    public final T from(IDataset<?, ?>... tables) {
        return super.from(tables);
    }
    /**************以上为去除警告************/
}
