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

package cn.xbatis.core.mybatis.mapper.context;


import cn.xbatis.core.dbType.IDbTypeContext;
import cn.xbatis.core.dbType.IDbTypeSetContext;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import db.sql.api.IDbType;
import db.sql.api.impl.cmd.executor.Executor;

import java.util.List;

public interface SQLCmdContext<E extends Executor> extends PreparedParameterContext, IDbTypeContext, IDbTypeSetContext {

    E getExecution();

    IDbType getDbType();

    String sql(IDbType dbType);

    List<Object> getParameters();

    BasicMapper $getBasicMapper();

    void $setBasicMapper(BasicMapper basicMapper);
}
