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

package cn.xbatis.core.dbType;

import db.sql.api.DbTypes;
import db.sql.api.IDbType;

public class DefaultDbTypeParser implements DbTypeParser {

    /**
     * 默认实例
     */
    public static final DbTypeParser INSTANCE = new DefaultDbTypeParser();

    protected IDbType getDbType(String jdbcUrl) {
        int start = jdbcUrl.indexOf(':');
        int end = jdbcUrl.indexOf(':', start + 1);
        String dbKey = jdbcUrl.substring(start, end + 1);
        IDbType dbType = DbTypes.getDbTypeByUrlKey(dbKey);
        if (dbType != null) {
            return dbType;
        }

        String lowerDbKey = dbKey.toLowerCase();
        if (!lowerDbKey.equals(dbKey)) {
            dbType = DbTypes.getDbTypeByUrlKey(dbKey);
            if (dbType != null) {
                return dbType;
            }
        }
        return null;
    }

    @Override
    public IDbType getDbTypeByUrl(String jdbcUrl) {
        IDbType dbType = getDbType(jdbcUrl);
        if (dbType != null) {
            return dbType;
        }
        throw new DbTypeUtil.DbTypeParseException("Unrecognized database type:" + jdbcUrl);
    }
}
