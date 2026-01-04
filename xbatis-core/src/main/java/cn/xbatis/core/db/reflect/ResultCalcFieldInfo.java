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

package cn.xbatis.core.db.reflect;

import cn.xbatis.db.annotations.ResultCalcField;
import db.sql.api.Cmd;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.CmdTemplate;
import db.sql.api.impl.tookit.SqlUtil;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

public class ResultCalcFieldInfo extends ResultFieldInfo {

    private final TableInfo tableInfo;

    private final TableFieldInfo[] tableFieldInfos;

    private final int storey;

    private final Class type;

    private final String sql;

    public ResultCalcFieldInfo(Class type, int storey, TableInfo tableInfo, TableFieldInfo[] tableFieldInfos, Field field, ResultCalcField resultCalcField, String sql) {
        super(true, type, field, SqlUtil.getAsName(type, field), getTypeHandler(field, resultCalcField), resultCalcField.jdbcType());
        this.type = type;
        this.tableInfo = tableInfo;
        this.tableFieldInfos = tableFieldInfos;
        this.storey = storey;
        this.sql = sql;
    }

    static Class<? extends TypeHandler<?>> getTypeHandler(Field field, ResultCalcField resultCalcField) {
        if (field.isAnnotationPresent(cn.xbatis.db.annotations.TypeHandler.class)) {
            cn.xbatis.db.annotations.TypeHandler th = field.getAnnotation(cn.xbatis.db.annotations.TypeHandler.class);
            return th.value();
        } else {
            return resultCalcField.typeHandler();
        }
    }

    public int getStorey() {
        return storey;
    }

    public Class getType() {
        return type;
    }

    public TableFieldInfo[] getTableFieldInfos() {
        return tableFieldInfos;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public Cmd getCmd(CmdFactory cmdFactory) {
        if (tableFieldInfos.length == 0) {
            return Methods.column(this.sql).as(getMappingColumnName());
        }
        Cmd[] cmds = new Cmd[tableFieldInfos.length];
        for (int i = 0; i < cmds.length; i++) {
            TableFieldInfo tableFieldInfo = tableFieldInfos[i];
            cmds[i] = cmdFactory.field(tableInfo.getType(), tableFieldInfo.getField().getName());
        }
        return CmdTemplate.create(true, this.sql, cmds).as(getMappingColumnName());
    }
}
