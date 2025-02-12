/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.Methods;

/**
 * 日期格式 Pattern
 */
public interface DatePattern extends Cmd {

    DatePattern HH = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return hourPattern(dbType);
        }
    };

    DatePattern DD = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return dayPattern(dbType);
        }
    };

    DatePattern MM = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return monthPattern(dbType);
        }
    };

    DatePattern YYYY = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return yearPattern(dbType);
        }
    };


    DatePattern MM_DD = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return monthPattern(dbType) + '-' + dayPattern(dbType);
        }
    };

    DatePattern YYYY_MM = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return yearPattern(dbType) + '-' + monthPattern(dbType);
        }
    };

    DatePattern YYYY_MM_DD = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return
                    yearPattern(dbType) + '-' +
                            monthPattern(dbType) + '-' +
                            dayPattern(dbType);

        }
    };
    DatePattern YYYY_MM_DD_HH_MM_SS = new DatePattern() {
        @Override
        public String pattern(DbType dbType) {
            return
                    yearPattern(dbType) + '-' +
                            monthPattern(dbType) + '-' +
                            dayPattern(dbType) + ' ' +
                            hourPattern(dbType) + ':' +
                            minutePattern(dbType) + ':' +
                            secondPattern(dbType);

        }
    };

    default String yearPattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "YYYY";
            }

            case SQLITE:
            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%Y";
            }

            case SQL_SERVER: {
                return "yyyy";
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    default String monthPattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case SQL_SERVER:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "MM";
            }

            case SQLITE:
            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%m";
            }

            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    default String dayPattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "DD";
            }

            case SQLITE:
            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%d";
            }

            case SQL_SERVER: {
                return "dd";
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    default String hourPattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "HH24";
            }

            case SQLITE:
            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%H";
            }

            case SQL_SERVER: {
                return "HH";
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    default String minutePattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "MI";
            }

            case SQLITE: {
                return "%M";
            }

            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%i";
            }

            case SQL_SERVER: {
                return "mm";
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    default String secondPattern(DbType dbType) {
        switch (dbType) {
            case KING_BASE:
            case H2:
            case OPEN_GAUSS:
            case PGSQL:
            case ORACLE:
            case DB2: {
                return "SS";
            }

            case SQLITE: {
                return "%S";
            }

            case DM:
            case MARIA_DB:
            case MYSQL: {
                return "%s";
            }

            case SQL_SERVER: {
                return "ss";
            }
            default: {
                throw new RuntimeException("Not supported");
            }
        }
    }

    String pattern(DbType dbType);

    @Override
    default StringBuilder sql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        Cmd pattern = Methods.cmd(pattern(context.getDbType()));
        sqlBuilder = pattern.sql(module, parent, context, sqlBuilder);
        return sqlBuilder;
    }

    @Override
    default boolean contain(Cmd cmd) {
        return false;
    }
}
