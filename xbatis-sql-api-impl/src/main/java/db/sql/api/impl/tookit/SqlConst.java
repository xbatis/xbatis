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

package db.sql.api.impl.tookit;

import db.sql.api.DbType;

public final class SqlConst {

    public static final String S_EMPTY = "";

    public static final char[] EMPTY = S_EMPTY.toCharArray();

    public static final String S_BLANK = " ";

    public static final char[] BLANK = S_BLANK.toCharArray();

    public static final char[] LIMIT = " LIMIT ".toCharArray();

    public static final char[] OFFSET = " OFFSET ".toCharArray();

    public static final char[] ROWS_FETCH_NEXT = " ROWS FETCH NEXT ".toCharArray();

    public static final char[] ROWS_ONLY = " ROWS ONLY".toCharArray();

    public static final char[] DISTINCT = " DISTINCT ".toCharArray();

    public static final char[] DISTINCT_ON = " DISTINCT ON".toCharArray();

    public static final char[] NULL = " NULL ".toCharArray();

    public static final char[] UNION = " UNION ".toCharArray();

    public static final char[] UNION_ALL = " UNION ALL ".toCharArray();
    public static final char[] WITH = " WITH ".toCharArray();
    public static final String ALL = "*";
    public static final char[] DOT = ".".toCharArray();
    public static final char[] DELIMITER = " , ".toCharArray();

    public static final char[] CONCAT_SPLIT_SYMBOL = " || ".toCharArray();
    public static final char[] INTERVAL = "INTERVAL ".toCharArray();
    public static final char[] AS = " AS ".toCharArray();
    public static final char[] SINGLE_QUOT = "'".toCharArray();
    public static final char[] IS = " IS ".toCharArray();
    public static final char[] IS_NOT = " IS NOT ".toCharArray();
    public static final char[] CREATE_TABLE = "CREATE TABLE ".toCharArray();
    public static final char[] SELECT = " SELECT ".toCharArray();
    public static final char[] FROM = " FROM ".toCharArray();
    public static final char[] DELETE = "DELETE ".toCharArray();
    public static final char[] UPDATE = "UPDATE ".toCharArray();
    public static final char[] ALTER_TABLE = "ALTER TABLE ".toCharArray();
    public static final char[] INSERT = "INSERT ".toCharArray();
    public static final char[] INSERT_INTO = "INSERT INTO ".toCharArray();
    public static final char[] INSERT_IGNORE_INTO = "INSERT IGNORE INTO ".toCharArray();
    public static final char[] INTO = " INTO ".toCharArray();
    public static final char[] VALUES = " VALUES ".toCharArray();
    public static final char[] SET = " SET ".toCharArray();
    public static final char[] JOIN = " JOIN ".toCharArray();
    public static final char[] INNER_JOIN = " INNER JOIN ".toCharArray();
    public static final char[] LEFT_JOIN = " LEFT JOIN ".toCharArray();
    public static final char[] RIGHT_JOIN = " RIGHT JOIN ".toCharArray();
    public static final char[] ON = " ON ".toCharArray();
    public static final char[] WHERE = " WHERE ".toCharArray();

    public static final char[] RETURNING = " RETURNING ".toCharArray();
    public static final char[] EXISTS = " EXISTS ".toCharArray();
    public static final char[] NOT_EXISTS = " NOT EXISTS ".toCharArray();
    public static final char[] AND = " AND ".toCharArray();
    public static final char[] IN = " IN ".toCharArray();

    public static final char[] NOT_IN = " NOT IN ".toCharArray();
    public static final char[] OR = " OR ".toCharArray();
    public static final char[] EQ = " = ".toCharArray();
    public static final char[] NE = " != ".toCharArray();
    public static final char[] LT = " < ".toCharArray();
    public static final char[] GT = " > ".toCharArray();
    public static final char[] LTE = " <= ".toCharArray();
    public static final char[] GTE = " >= ".toCharArray();
    public static final char[] BETWEEN = " BETWEEN ".toCharArray();
    public static final char[] NOT_BETWEEN = " NOT BETWEEN ".toCharArray();
    public static final char[] LIKE = " LIKE ".toCharArray();
    public static final char[] NOT_LIKE = " NOT LIKE ".toCharArray();
    public static final char[] I_LIKE = " ILIKE ".toCharArray();
    public static final char[] NOT_I_LIKE = " NOT ILIKE ".toCharArray();
    public static final char[] BRACKET_LEFT = "(".toCharArray();
    public static final char[] BRACKET_RIGHT = ")".toCharArray();
    public static final char[] CONCAT = " CONCAT".toCharArray();
    public static final char[] CONCAT_WS = " CONCAT_WS".toCharArray();
    public static final char[] IF = " IF".toCharArray();
    public static final char[] IFNULL = " IFNULL".toCharArray();
    public static final char[] CASE = " CASE ".toCharArray();
    public static final char[] WHEN = " WHEN ".toCharArray();
    public static final char[] THEN = " THEN ".toCharArray();
    public static final char[] ELSE = " ELSE ".toCharArray();
    public static final char[] END = " END ".toCharArray();
    public static final char[] VAGUE_SYMBOL = "'%'".toCharArray();
    public static final char[] MAX = " MAX".toCharArray();
    public static final char[] MIN = " MIN".toCharArray();
    public static final char[] AVG = " AVG".toCharArray();
    public static final char[] ABS = " ABS".toCharArray();
    public static final char[] SUM = " SUM".toCharArray();
    public static final char[] CEIL = " CEIL".toCharArray();
    public static final char[] FLOOR = " FLOOR".toCharArray();
    public static final char[] RAND = " RAND".toCharArray();
    public static final char[] TRUNCATE = " TRUNCATE".toCharArray();
    public static final char[] SQRT = " SQRT".toCharArray();
    public static final char[] SIGN = " SIGN".toCharArray();
    public static final char[] PI = " PI".toCharArray();
    public static final char[] DIVIDE = " / ".toCharArray();
    public static final char[] MULTIPLY = " * ".toCharArray();
    public static final char[] SUBTRACT = " - ".toCharArray();
    public static final char[] PLUS = " + ".toCharArray();
    public static final char[] ROUND = " ROUND".toCharArray();
    public static final char[] POW = " POW".toCharArray();
    public static final char[] MOD = " MOD".toCharArray();
    public static final char[] EXP = " EXP".toCharArray();
    public static final char[] LOG = " LOG".toCharArray();
    public static final char[] LOG2 = " LOG2".toCharArray();
    public static final char[] LOG10 = " LOG10".toCharArray();
    public static final char[] RADIANS = " RADIANS".toCharArray();
    public static final char[] DEGREES = " DEGREES".toCharArray();
    public static final char[] SIN = " SIN".toCharArray();
    public static final char[] ASIN = " ASIN".toCharArray();
    public static final char[] COS = " COS".toCharArray();
    public static final char[] ACOS = " ACOS".toCharArray();
    public static final char[] TAN = " TAN".toCharArray();
    public static final char[] ATAN = " ATAN".toCharArray();
    public static final char[] COT = " COT".toCharArray();
    public static final char[] CHAR_LENGTH = " CHAR_LENGTH".toCharArray();
    public static final char[] LENGTH = " LENGTH".toCharArray();
    public static final char[] UPPER = " UPPER".toCharArray();
    public static final char[] LOWER = " LOWER".toCharArray();
    public static final char[] LEFT = " LEFT".toCharArray();
    public static final char[] SUBSTR = " SUBSTR".toCharArray();
    public static final char[] RIGHT = " RIGHT".toCharArray();
    public static final char[] LPAD = " LPAD".toCharArray();
    public static final char[] RPAD = " RPAD".toCharArray();
    public static final char[] TRIM = " TRIM".toCharArray();
    public static final char[] LTRIM = " LTRIM".toCharArray();
    public static final char[] RTRIM = " RTRIM".toCharArray();
    public static final char[] REPEAT = " REPEAT".toCharArray();
    public static final char[] REPLACE = " REPLACE".toCharArray();
    public static final char[] REVERSE = " REVERSE".toCharArray();
    public static final char[] INSTR = " INSTR".toCharArray();
    public static final char[] STRCMP = " STRCMP".toCharArray();
    public static final char[] FILED = " FILED".toCharArray();
    public static final char[] FIND_IN_SET = " FIND_IN_SET".toCharArray();
    public static final char[] CURRENT_DATE = " CURRENT_DATE".toCharArray();
    public static final char[] UNIX_TIMESTAMP = " UNIX_TIMESTAMP".toCharArray();
    public static final char[] FROM_UNIXTIME = " FROM_UNIXTIME".toCharArray();
    public static final char[] MONTH = " MONTH".toCharArray();
    public static final char[] DATE = " DATE".toCharArray();

    public static final char[] DAY = " DAY".toCharArray();
    public static final char[] WEEKDAY = " WEEKDAY".toCharArray();
    public static final char[] HOUR = " HOUR".toCharArray();
    public static final char[] DATE_DIFF = " DATEDIFF".toCharArray();
    public static final char[] DATE_ADD = " DATE_ADD".toCharArray();
    public static final char[] DATE_SUB = " DATE_SUB".toCharArray();
    public static final char[] MD5 = " MD5".toCharArray();
    public static final char[] INET_ATON = " INET_ATON".toCharArray();
    public static final char[] INET_NTOA = " INET_NTOA".toCharArray();
    public static final char[] COUNT = " COUNT".toCharArray();
    public static final char[] GROUP_BY = " GROUP BY ".toCharArray();
    public static final char[] HAVING = " HAVING ".toCharArray();
    public static final char[] ORDER_BY = " ORDER BY ".toCharArray();
    public static final char[] ASC = " ASC ".toCharArray();
    public static final char[] DESC = " DESC ".toCharArray();
    public static final char[] FOR_UPDATE = " FOR UPDATE".toCharArray();
    public static final char[] FOR_UPDATE_NO_WAIT = " FOR UPDATE NOWAIT".toCharArray();
    public static final char[] DOUBLE_QUOT = "\"".toCharArray();

    public static final char[] CAST_TEXT = "::TEXT".toCharArray();

    public static final char[] SELF_FROM_DUAL = "SELECT * FROM DUAL".toCharArray();

    public static final char[] FROM_DUAL = " FROM DUAL".toCharArray();

    public static final char[] RECURSIVE = " RECURSIVE ".toCharArray();

    public static final char[] GROUP_CONCAT = " GROUP_CONCAT".toCharArray();

    public static final char[] JSON_EXTRACT = " JSON_EXTRACT".toCharArray();

    public static final char[] JSON_CONTAINS_PATH = " JSON_CONTAINS_PATH".toCharArray();

    public static final char[] JSON_CONTAINS = " JSON_CONTAINS".toCharArray();

    public static final char[] JSON_QUOTE = " JSON_QUOTE".toCharArray();

    public static final char[] ST_DWithin = " ST_DWithin".toCharArray();

    public static final char[] ST_DISTANCE = " ST_Distance".toCharArray();

    public static final char[] ST_CONTAINS = " ST_Contains".toCharArray();

    public static final char[] ROWNUM = " ROWNUM ".toCharArray();

    public static String FORCE_INDEX(DbType dbType, String indexName) {
        switch (dbType) {
            case SQL_SERVER: {
                return " WITH(INDEX(" + indexName + "))";
            }

            case PGSQL:
            case OPEN_GAUSS:
            case KING_BASE: {
                return " USE INDEX(" + indexName + ")";
            }

            default: {
                return " FORCE INDEX(" + indexName + ")";
            }
        }
    }

    public static char[] AS(DbType dbType) {
        switch (dbType) {
            case ORACLE: {
                return BLANK;
            }
            default: {
                return AS;
            }
        }
    }

    public static String CURRENT_DATE(DbType dbType) {
        switch (dbType) {
            case DB2: {
                return " CURRENT_DATE";
            }
            case SQL_SERVER: {
                return " CAST(GETDATE() AS date)";
            }
            case KING_BASE: {
                return " CURRENT_DATE";
            }
            case ORACLE: {
                return " TO_CHAR(CURRENT_DATE,'YYYY-MM-DD')";
            }
            case OPEN_GAUSS:
            case PGSQL: {
                return " CURRENT_DATE";
            }
            case SQLITE: {
                return " DATE('NOW')";
            }
        }
        return " CURRENT_DATE()";
    }

    public static String CURRENT_TIME(DbType dbType) {
        switch (dbType) {
            case DB2: {
                return " CURRENT_TIME";
            }
            case SQL_SERVER: {
                return " CAST(GETDATE() AS time)";
            }
            case KING_BASE: {
                return " CURRENT_TIME";
            }
            case ORACLE: {
                return " TO_CHAR(CURRENT_DATE,'HH24:MI:SS')";
            }
            case OPEN_GAUSS:
            case PGSQL: {
                return " LOCALTIME";
            }
            case SQLITE: {
                return " TIME('NOW')";
            }
        }
        return " CURRENT_TIME()";
    }

    public static String CURRENT_DATE_TIME(DbType dbType) {
        switch (dbType) {
            case DB2: {
                return " CURRENT_TIMESTAMP";
            }
            case SQL_SERVER: {
                return " GETDATE()";
            }
            case KING_BASE: {
                return " CURRENT_TIMESTAMP";
            }
            case ORACLE: {
                return " CURRENT_DATE";
            }
            case OPEN_GAUSS:
            case PGSQL: {
                return " LOCALTIMESTAMP";
            }
            case SQLITE: {
                return "DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')";
            }
        }
        return " CURRENT_TIMESTAMP()";
    }

    public static String DATE_FORMAT(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return " TO_CHAR";
            }
            case SQL_SERVER: {
                return " FORMAT";
            }

            case SQLITE: {
                return " STRFTIME";
            }

            case DM:
            case MARIA_DB:
            case MYSQL: {
                return " DATE_FORMAT";
            }
        }
        return " TO_CHAR";
    }

    public static String YEAR(DbType dbType) {
        switch (dbType) {
            case ORACLE:
            case OPEN_GAUSS:
            case PGSQL: {
                return " EXTRACT (YEAR FROM ";
            }
            case SQLITE: {
                return "STRFTIME";
            }
        }
        return " YEAR";
    }

    public static String MONTH(DbType dbType) {
        switch (dbType) {
            case ORACLE:
            case OPEN_GAUSS:
            case PGSQL: {
                return " EXTRACT (MONTH FROM ";
            }
            case SQLITE: {
                return "STRFTIME";
            }
        }
        return " MONTH";
    }

    public static String DAY(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case ORACLE:
            case OPEN_GAUSS:
            case PGSQL: {
                return " EXTRACT (DAY FROM ";
            }
            case SQLITE: {
                return "STRFTIME";
            }
        }
        return " DAY";
    }

    public static String HOUR(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " DATEPART(HOUR,";
            }
            case KING_BASE:
            case ORACLE:
            case OPEN_GAUSS:
            case PGSQL: {
                return " EXTRACT(HOUR FROM ";
            }
            case SQLITE: {
                return "STRFTIME";
            }
        }
        return " HOUR";
    }

    public static String WEEKDAY(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " DATEPART(WEEKDAY,";
            }
            case KING_BASE:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE: {
                return " TO_CHAR";
            }
            case SQLITE: {
                return "STRFTIME";
            }
        }
        return " DAYOFWEEK";
    }

    public static String UNIX_TIMESTAMP(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " DATEPART(WEEKDAY,";
            }
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE: {
                return " TO_TIMESTAMP";
            }
        }
        return " UNIX_TIMESTAMP";
    }

    public static String CHAR_LENGTH(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " LEN";
            }
            case SQLITE:
            case ORACLE: {
                return " LENGTH";
            }
        }
        return " CHAR_LENGTH";
    }

    public static String TRUNCATE(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " ROUND";
            }
            case ORACLE: {
                return " TRUNC";
            }
        }
        return " TRUNCATE";
    }

    public static String SUBSTR(DbType dbType) {
        switch (dbType) {
            case SQL_SERVER: {
                return " SUBSTRING";
            }
            default: {
                return " SUBSTR";
            }
        }
    }

}
