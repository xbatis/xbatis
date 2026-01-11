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
import db.sql.api.IDbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;

import java.util.ArrayList;
import java.util.List;

public class XmlScriptUtil {
    public static final XmlScript buildXmlScript(String paramName, String scriptParamName, Cmd cmd, IDbType dbType) {
        return buildXmlScript(paramName, scriptParamName, cmd, dbType, null);
    }

    public static final XmlScript buildXmlScript(String paramName, String scriptParamName, Cmd cmd, IDbType dbType, String ignoreTag) {
        if (cmd == null) {
            return new XmlScript("", null);
        }
        List<Object> scriptParams = new ArrayList<>();
        String sql;
        SqlBuilderContext sqlBuilderContext = new SqlBuilderContext(dbType, SQLMode.PREPARED) {
            @Override
            public String addParam(Object value) {
                scriptParams.add(value);
                if (paramName == null) {
                    return "#{" + scriptParamName + "[" + (scriptParams.size() - 1) + "]}";
                }
                return "#{" + paramName + scriptParamName + "[" + (scriptParams.size() - 1) + "]}";
            }
        };

        sql = cmd.sql(cmd, cmd, sqlBuilderContext, new StringBuilder()).toString();
        if (ignoreTag != null) {
            sql = sql.replaceFirst(ignoreTag, "");
        }
        XmlScript xmlScript = new XmlScript(sql, scriptParams);
        return xmlScript;
    }
}
