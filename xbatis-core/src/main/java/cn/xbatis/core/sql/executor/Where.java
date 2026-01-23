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

import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.util.WhereUtil;
import db.sql.api.IDbType;

import java.util.List;

public final class Where extends db.sql.api.impl.cmd.struct.Where {

    private IDbType dbType;
    private String mybatisParamNamespace;
    private XmlScript whereScript;

    public Where() {
        super(new MybatisCmdFactory().createConditionFactory());
    }

    public static Where create() {
        return new Where();
    }

    public static Where create(Object object) {
        return new Where().where(object);
    }

    /**
     * 追加非null，非空的字段值的条件
     *
     * @param object 对象类上必须有实体类注解或@ConditionTarget
     * @return Q
     * @see cn.xbatis.db.annotations.ConditionTarget @ConditionTarget 条件目标注解
     * @see cn.xbatis.db.annotations.Condition @Condition条件注解
     */
    public Where where(Object object) {
        WhereUtil.where(this, object);
        return this;
    }

    public IDbType getDbType() {
        return dbType;
    }

    public void setDbType(IDbType dbType) {
        this.dbType = dbType;
    }

    public void setMybatisParamName(String mybatisParamName) {
        if (mybatisParamName != null || !mybatisParamName.isEmpty()) {
            this.mybatisParamNamespace = mybatisParamName + ".";
        }
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return 用于xml 的where部分的 参数
     */
    public List<Object> getWhereScriptParams() {
        return whereScript.getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return 用于xml 的where部分的 sql
     */
    public String getWhereScript() {
        if (whereScript != null) {
            return whereScript.getSql();
        }
        this.whereScript = XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "whereScriptParams", this, this.dbType, "WHERE");
        return whereScript.getSql();
    }
}
