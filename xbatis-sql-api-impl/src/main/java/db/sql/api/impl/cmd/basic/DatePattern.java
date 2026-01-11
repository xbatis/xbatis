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

package db.sql.api.impl.cmd.basic;

import db.sql.api.*;
import db.sql.api.impl.cmd.Methods;

/**
 * 日期格式 Pattern
 */
public interface DatePattern extends Cmd {

    DatePattern HH = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return hourPattern(dbType);
        }
    };

    DatePattern DD = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return dayPattern(dbType);
        }
    };

    DatePattern MM = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return monthPattern(dbType);
        }
    };

    DatePattern YYYY = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return yearPattern(dbType);
        }
    };


    DatePattern MM_DD = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return monthPattern(dbType) + '-' + dayPattern(dbType);
        }
    };

    DatePattern YYYY_MM = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return yearPattern(dbType) + '-' + monthPattern(dbType);
        }
    };

    DatePattern YYYY_MM_DD = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return
                    yearPattern(dbType) + '-' +
                            monthPattern(dbType) + '-' +
                            dayPattern(dbType);

        }
    };
    DatePattern YYYY_MM_DD_HH_MM_SS = new DatePattern() {
        @Override
        public String pattern(IDbType dbType) {
            return
                    yearPattern(dbType) + '-' +
                            monthPattern(dbType) + '-' +
                            dayPattern(dbType) + ' ' +
                            hourPattern(dbType) + ':' +
                            minutePattern(dbType) + ':' +
                            secondPattern(dbType);

        }
    };

    default String yearPattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%Y";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "YYYY";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "YYYY";
        }

        if (dbType == DbType.SQLITE || dbType == DbType.DM) {
            return "%Y";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "YYYY";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "yyyy";
        }

        throw new RuntimeException("Not supported");
    }

    default String monthPattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%m";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "MM";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "MM";
        }

        if (dbType == DbType.SQLITE || dbType == DbType.DM) {
            return "%m";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "MM";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "MM";
        }

        throw new RuntimeException("Not supported");
    }

    default String dayPattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%d";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "DD";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "DD";
        }

        if (dbType == DbType.SQLITE || dbType == DbType.DM) {
            return "%d";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "DD";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "dd";
        }

        throw new RuntimeException("Not supported");
    }

    default String hourPattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%H";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "HH24";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "HH24";
        }

        if (dbType == DbType.SQLITE || dbType == DbType.DM) {
            return "%H";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "HH24";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "HH";
        }

        throw new RuntimeException("Not supported");
    }

    default String minutePattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%i";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "MI";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "MI";
        }

        if (dbType == DbType.DM) {
            return "%i";
        }

        if (dbType == DbType.SQLITE) {
            return "%M";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "MI";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "mm";
        }

        throw new RuntimeException("Not supported");
    }

    default String secondPattern(IDbType dbType) {
        if (dbType.getDbModel() == DbModel.MYSQL || dbType == DbType.MYSQL || dbType == DbType.MARIA_DB) {
            return "%s";
        }

        if (dbType.getDbModel() == DbModel.PGSQL || dbType == DbType.PGSQL || dbType == DbType.GAUSS || dbType == DbType.KING_BASE) {
            return "SS";
        }

        if (dbType.getDbModel() == DbModel.ORACLE || dbType == DbType.ORACLE) {
            return "SS";
        }

        if (dbType == DbType.SQLITE) {
            return "%S";
        }

        if (dbType == DbType.DM) {
            return "%s";
        }

        if (dbType == DbType.H2 || dbType == DbType.DB2) {
            return "SS";
        }

        if (dbType == DbType.SQL_SERVER) {
            return "ss";
        }

        throw new RuntimeException("Not supported");
    }

    String pattern(IDbType dbType);

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
