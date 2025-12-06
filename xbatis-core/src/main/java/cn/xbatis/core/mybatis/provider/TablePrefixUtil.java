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

package cn.xbatis.core.mybatis.provider;


import cn.xbatis.core.db.reflect.ResultInfos;
import cn.xbatis.db.annotations.ResultEntity;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.Table;

import java.util.Map;
import java.util.Objects;

public final class TablePrefixUtil {

    private TablePrefixUtil() {
    }

    public static void prefixMapping(CmdFactory cmdFactory, Class returnType) {
        if (Objects.isNull(returnType)) {
            return;
        }
        if (!returnType.isAnnotationPresent(ResultEntity.class)) {
            return;
        }

        Map<Class, Map<Integer, String>> entityPrefixMap = ResultInfos.get(returnType).getTablePrefixes();
        if (Objects.nonNull(entityPrefixMap)) {
            for (Map.Entry<Class, Map<Integer, String>> entry : entityPrefixMap.entrySet()) {
                Class<?> entityType = entry.getKey();
                entry.getValue().forEach((storey, prefix) -> {
                    if (storey == -1) {
                        for (int i = 0; i < 5; i++) {
                            Table table = cmdFactory.cacheTable(entityType, i);
                            if (Objects.nonNull(table) && Objects.isNull(table.getPrefix())) {
                                table.setPrefix(prefix);
                                break;
                            }
                        }
                    } else {
                        Table table = cmdFactory.cacheTable(entityType, storey);
                        if (Objects.nonNull(table) && Objects.isNull(table.getPrefix())) {
                            table.setPrefix(prefix);
                        }
                    }
                });
            }
        }
    }
}
