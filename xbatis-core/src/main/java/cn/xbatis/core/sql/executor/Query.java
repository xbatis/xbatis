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

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.struct.Where;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Query<T> extends BaseQuery<Query<T>, T> {

    private Map<XmlScriptType, XmlScript> xmlScriptMap;
    private DbType dbType;
    private String mybatisParamNamespace;

    public Query() {
        super();
    }


    public Query(Where where) {
        super(where);
    }

    public static <T> Query<T> create() {
        return new Query();
    }

    public static <T> Query<T> create(Where where) {
        if (where == null) {
            return create();
        }
        return new Query(where);
    }

    public <R> Query<R> returnType(Class<R> returnType) {
        return (Query<R>) super.setReturnType(returnType);
    }

    public <R> Query<R> returnType(Class<R> returnType, Consumer<R> consumer) {
        return (Query<R>) super.setReturnType(returnType, consumer);
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public void setMybatisParamName(String mybatisParamName) {
        if (mybatisParamName != null || !mybatisParamName.isEmpty()) {
            this.mybatisParamNamespace = mybatisParamName + ".";
        }
    }


    private Map<XmlScriptType, XmlScript> getXmlScriptMap() {
        if (xmlScriptMap == null) {
            xmlScriptMap = new HashMap<>();
        }
        return xmlScriptMap;
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getQueryScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.QUERY).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getQueryScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.QUERY, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "queryScriptParams", this, this.dbType);
        }).getSql();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getSelectScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.SELECT).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getSelectScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.SELECT, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "selectScriptParams", this.getSelect(), this.dbType, "SELECT");
        }).getSql();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getFromScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.FROM).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getFromScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.FROM, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "fromScriptParams", this.getFrom(), this.dbType, "FROM");
        }).getSql();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getWhereScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.WHERE).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getWhereScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.WHERE, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "whereScriptParams", this.getWhere(), this.dbType, "WHERE");
        }).getSql();
    }


    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getOrderByScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.ORDER_BY).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getOrderByScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.ORDER_BY, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "orderByScriptParams", this.getOrderBy(), this.dbType);
        }).getSql();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getGroupByScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.GROUP_BY).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getGroupByScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.GROUP_BY, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "groupByScriptParams", this.getGroupBy(), this.dbType);
        }).getSql();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public List<Object> getHavingScriptParams() {
        return getXmlScriptMap().get(XmlScriptType.HAVING).getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getHavingScript() {
        return getXmlScriptMap().computeIfAbsent(XmlScriptType.HAVING, key -> {
            return XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "havingScriptParams", this.getHaving(), this.dbType);
        }).getSql();
    }

    @Override
    public StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        return super.sql(module, parent, context, sqlBuilder);
    }
}
