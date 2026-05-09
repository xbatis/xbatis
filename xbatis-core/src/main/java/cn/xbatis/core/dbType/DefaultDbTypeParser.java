/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

    public static void main(String[] args) {
        System.out.println(new DefaultDbTypeParser().getType("jdbc:mysql://localhost:3306/test"));
    }

    protected String getType(String jdbcUrl) {
        int startIndex = -1;
        int endIndex = -1;
        char[] chars = jdbcUrl.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ':') {
                if (startIndex != -1 && i + 3 <= chars.length && chars[i + 1] == '/' && chars[i + 2] == '/') {
                    endIndex = i;
                    break;
                } else {
                    startIndex = i;
                }
            }
        }
        if (startIndex != -1 && endIndex != -1) {
            char[] dbKeyChars = new char[endIndex - startIndex - 1];
            for (int i = startIndex + 1, j = 0; i < endIndex; i++, j++) {
                char c = chars[i];
                dbKeyChars[j] = Character.isUpperCase(c) ? Character.toLowerCase(c) : c;
            }
            return new String(dbKeyChars);
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

    protected IDbType getDbType(String jdbcUrl) {
        String dbKey = getType(jdbcUrl);
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
}
