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


import db.sql.api.DbType;
import db.sql.api.SQLMode;
import db.sql.api.SqlBuilderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MybatisSqlBuilderContext extends SqlBuilderContext {

    private final List<Object> paramList = new ArrayList<>();

    private Object[] params;

    public MybatisSqlBuilderContext(DbType dbType, SQLMode sqlMode) {
        super(dbType, sqlMode);
    }

    @Override
    public String addParam(Object value) {
        paramList.add(value);
        return "?";
    }

    public Object[] getParams() {
        if (Objects.isNull(params)) {
            params = paramList.toArray();
        }
        return params;
    }
}
