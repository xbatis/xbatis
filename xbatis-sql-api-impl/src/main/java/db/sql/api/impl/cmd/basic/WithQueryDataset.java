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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.struct.IFrom;
import db.sql.api.cmd.struct.IJoin;
import db.sql.api.impl.cmd.executor.AbstractWithQuery;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;


public class WithQueryDataset extends AbstractDataset<WithQueryDataset, DatasetField> {

    private final AbstractWithQuery withQuery;

    private String alias;

    public WithQueryDataset(AbstractWithQuery withQuery, String alias) {
        this.withQuery = withQuery;
        this.alias = alias;
    }

    public <E> DatasetField $(Getter<E> getter) {
        return this.$(this.withQuery.$().columnName(getter));
    }

    /**
     * 外部字段
     *
     * @param getter
     * @param <E>
     * @return
     */
    public <E> DatasetField $outerField(Getter<E> getter) {
        return this.$outerField(getter, false);
    }

    /**
     * 外部字段
     *
     * @param getter
     * @param depth  是否深度引用，非深度引用只是 别名.getter的对应的列名；如果是深度的匹配（只能针对那些没有包装过的字段）
     * @param <E>
     * @return
     */
    public <E> DatasetField $outerField(Getter<E> getter, boolean depth) {
        if (!depth) {
            return this.$(getter);
        }
        TableField tableField = this.withQuery.$(getter);
        return this.withQuery.$outerField(this, this.withQuery, tableField);
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        if (parent instanceof IFrom || parent instanceof IJoin) {
            sqlBuilder.append(this.withQuery.getAlias()).append(SqlConst.BLANK).append(this.alias);
            return sqlBuilder;
        }
        sqlBuilder.append(this.alias);
        return sqlBuilder;
    }


    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.withQuery);
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public WithQueryDataset as(String alias) {
        this.alias = alias;
        return this;
    }
}
