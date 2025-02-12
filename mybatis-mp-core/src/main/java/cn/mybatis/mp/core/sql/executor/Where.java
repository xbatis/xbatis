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
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;

import java.util.ArrayList;
import java.util.List;

public final class Where extends db.sql.api.impl.cmd.struct.Where {

    private DbType dbType;
    private String whereScript;
    private String mybatisParamNamespace;
    private List<Object> scriptParams;

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

    public List<Object> getScriptParams() {
        return scriptParams;
    }

    public String getWhereScript() {
        if (whereScript != null) {
            return whereScript;
        }
        scriptParams = new ArrayList<>();

        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(this.dbType, SQLMode.PREPARED) {
            @Override
            public String addParam(Object value) {
                scriptParams.add(value);
                if (mybatisParamNamespace == null) {
                    return "#{scriptParams[" + (scriptParams.size() - 1) + "]}";
                }
                return "#{" + mybatisParamNamespace + "scriptParams[" + (scriptParams.size() - 1) + "]}";
            }
        };
        whereScript = this.sql(null, null, sqlBuilderContext, new StringBuilder()).toString();
        whereScript = whereScript.replaceFirst("WHERE", "");
        return whereScript;
    }
}
