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

package cn.xbatis.core.sql.util;


import cn.xbatis.core.db.reflect.*;
import cn.xbatis.db.annotations.ResultEntity;
import cn.xbatis.db.annotations.Table;
import db.sql.api.Cmd;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.struct.query.Returning;

import java.util.ArrayList;
import java.util.List;

public final class ReturningClassUtil {

    private static void buildReturning(CmdFactory cmdFactory, Returning returning, List<ResultFieldInfo> resultFieldInfos, List<Cmd> cmdList) {
        resultFieldInfos.stream().filter(item -> item instanceof ResultTableFieldInfo)
                .forEach(item -> {
                    ResultTableFieldInfo resultTableFieldInfo = (ResultTableFieldInfo) item;
                    Cmd tableField = cmdFactory.field(resultTableFieldInfo.getTableInfo().getType(), resultTableFieldInfo.getTableFieldInfo().getField().getName(), resultTableFieldInfo.getStorey());
                    if (!cmdList.contains(tableField)) {
                        cmdList.add(tableField);
                    }
                });
    }

    private static void buildNestedReturning(CmdFactory cmdFactory, Returning returning, List<NestedResultInfo> nestedResultInfos, List<Cmd> cmdList) {
        nestedResultInfos.forEach(item -> {
            buildReturning(cmdFactory, returning, item.getResultFieldInfos(), cmdList);
            buildNestedReturning(cmdFactory, returning, item.getNestedResultInfos(), cmdList);
        });
    }

    private static List<Cmd> buildReturning(CmdFactory cmdFactory, Returning returning, Class clazz, int storey, List<Cmd> cmdList) {
        if (clazz.isAnnotationPresent(ResultEntity.class)) {
            ResultInfo resultInfo = ResultInfos.get(clazz);
            buildReturning(cmdFactory, returning, resultInfo.getResultFieldInfos(), cmdList);
            buildNestedReturning(cmdFactory, returning, resultInfo.getNestedResultInfos(), cmdList);
        } else if (clazz.isAnnotationPresent(Table.class)) {
            TableInfo tableInfo = Tables.get(clazz);
            for (int i = 0; i < tableInfo.getFieldSize(); i++) {
                TableFieldInfo tableFieldInfo = tableInfo.getTableFieldInfos().get(i);
                if (tableFieldInfo.getTableFieldAnnotation().select() && tableFieldInfo.getTableFieldAnnotation().exists()) {
                    cmdList.add(cmdFactory.field(clazz, tableFieldInfo.getField().getName(), storey));
                }
            }
        }
        return cmdList;
    }

    public static boolean returning(CmdFactory cmdFactory, Returning returning, Class clazz) {
        return returning(cmdFactory, returning, clazz, 1);
    }

    public static boolean returning(CmdFactory cmdFactory, Returning returning, Class clazz, int storey) {
        List<Cmd> list = new ArrayList<>();
        returning.returning(buildReturning(cmdFactory, returning, clazz, storey, list));
        return !list.isEmpty();
    }

    public static void returning(CmdFactory cmdFactory, Returning returning, int storey, Class[] entities) {
        List<Cmd> list = new ArrayList<>();
        for (Class entity : entities) {
            buildReturning(cmdFactory, returning, entity, storey, list);
        }
        returning.returning(list);
    }
}
