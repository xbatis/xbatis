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

package cn.xbatis.core.mybatis.executor;

import cn.xbatis.core.mybatis.mapping.ResultMapWrapper;
import cn.xbatis.core.mybatis.provider.MybatisSQLProvider;
import cn.xbatis.core.mybatis.provider.PreparedSQLProvider;
import cn.xbatis.core.mybatis.provider.PreparedSQLSqlSource;
import cn.xbatis.core.mybatis.provider.SQLCmdSqlSource;
import cn.xbatis.core.util.PagingUtil;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;

public final class MappedStatementUtil {

    public static MappedStatement wrap(MappedStatement ms) {
        PagingUtil.handleMappedStatement(ms);
        ResultMapWrapper.replaceResultMap(ms);
        if (ms.getSqlSource() instanceof ProviderSqlSource) {
            ProviderSqlSource providerSqlSource = (ProviderSqlSource) ms.getSqlSource();
            MetaObject sqlSourceMetaObject = ms.getConfiguration().newMetaObject(providerSqlSource);
            Class<?> providerType = (Class<?>) sqlSourceMetaObject.getValue("providerType");
            if (MybatisSQLProvider.class.isAssignableFrom(providerType)) {
                Method providerMethod = (Method) sqlSourceMetaObject.getValue("providerMethod");
                SQLCmdSqlSource sqlSource = new SQLCmdSqlSource(ms.getConfiguration(), providerMethod);
                MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
                msMetaObject.setValue("sqlSource", sqlSource);
            } else if (PreparedSQLProvider.class.isAssignableFrom(providerType)) {
                PreparedSQLSqlSource sqlSource = new PreparedSQLSqlSource(ms.getConfiguration());
                MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
                msMetaObject.setValue("sqlSource", sqlSource);
            }
        }
        return ms;
    }
}
