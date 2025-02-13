/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.sql.executor;

import cn.mybatis.mp.core.sql.MybatisCmdFactory;
import db.sql.api.DbType;

import java.util.List;

public final class Where extends db.sql.api.impl.cmd.struct.Where {

    private DbType dbType;
    private String mybatisParamNamespace;
    private XmlScript whereScript;

    public Where() {
        super(new MybatisCmdFactory().createConditionFactory());
    }

    public static Where create() {
        return new Where();
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
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
     * @return
     */
    public List<Object> getWhereScriptParams() {
        return whereScript.getScriptParams();
    }

    /**
     * 只给 xml 生成动态sql 用
     *
     * @return
     */
    public String getWhereScript() {
        if (whereScript != null) {
            return whereScript.getSql();
        }
        this.whereScript = XmlScriptUtil.buildXmlScript(this.mybatisParamNamespace, "whereScriptParams", this, this.dbType, "WHERE");
        return whereScript.getSql();
    }
}
