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

package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.sql.MybatisCmdFactory;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import cn.xbatis.core.sql.executor.chain.UpdateChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.DO.SysUserScore;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.mapper.SysUserScoreMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbType;
import db.sql.api.cmd.GetterFields;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.DatePattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunTest extends BaseTest {

    @Test
    public void count() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.count())
                    .select(GetterFields.of(SysUser::getId, SysRole::getId), cs -> cs[0].concat(cs[1]).as("item_name"))
                    .from(SysUser.class)
                    .like(SysUser::getUserName, "test", StringUtils::isNotBlank)
                    .eq(SysUser::getUserName, "test", StringUtils::isNotBlank)
                    .and()
                    .and(Methods.TRUE())
                    .or(Methods.FALSE())
                    .returnType(Integer.class)
                    .count();

            assertEquals(Integer.valueOf(2), count, "count");
        }
    }

    @Test
    public void min() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.min())
                    .from(SysUser.class)
                    .like(LikeMode.RIGHT, SysUser::getUserName, "test")
                    .returnType(Integer.class)
                    .get();

            assertEquals(Integer.valueOf(2), count, "min");
        }
    }

    @Test
    public void max() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer count = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.max())
                    .from(SysUser.class)
                    .like(LikeMode.RIGHT, SysUser::getUserName, "test")
                    .returnType(Integer.class)
                    .get();

            assertEquals(Integer.valueOf(3), count, "max");
        }
    }

    @Test
    public void avg() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal avg = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.avg())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1).or().eq(SysUser::getId, 3)
                    .returnType(BigDecimal.class)
                    .get();
            assertEquals(0, new BigDecimal("2").compareTo(avg), "avg");
        }
    }

    @Test
    public void multiply() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal multiply = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.multiply(-1))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(BigDecimal.class)
                    .get();

            assertEquals(new BigDecimal("-1"), multiply, "multiply");
        }
    }

    @Test
    public void divide() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal divide = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.divide(-2))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 2)
                    .returnType(BigDecimal.class)
                    .get();


            assertEquals(0, new BigDecimal("-1").compareTo(divide), "divide");
        }
    }

    @Test
    public void plus() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal divide = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.plus(1))
                    .eq(SysUser::getId, 2)
                    .returnType(BigDecimal.class)
                    .get();

            assertEquals(new BigDecimal("3"), divide, "plus");
        }
    }

    @Test
    public void subtract() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal divide = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.subtract(1))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 3)
                    .returnType(BigDecimal.class)
                    .get();

            assertEquals(new BigDecimal("2"), divide, "subtract");
        }
    }

    @Test
    public void abs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            BigDecimal divide = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.multiply(-2).abs())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 3)
                    .returnType(BigDecimal.class)
                    .get();
            assertEquals(new BigDecimal("6"), divide, "abs");
        }
    }

    @Test
    public void pow() {
        Query query = Query.create().
                select(SysUser::getId, c -> c.pow(2)).
                from(SysUser.class).
                eq(SysUser::getId, 3);

        query.setReturnType(BigDecimal.class);
        check("if_", "SELECT  POW( t.id , 2) FROM t_sys_user t WHERE  t.id = 3", query);
    }


    @Test
    public void concat() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.concat("2", "4"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 3)
                    .returnType(String.class)
                    .get();

            assertEquals("324", str, "concat");
        }
    }

    @Test
    public void concatWs() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.concatWs("a", "2", "3"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 3)
                    .returnType(String.class)
                    .get();
            assertEquals("3a2a3", str, "concatWs");
        }
    }

    @Test
    public void round() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserScoreMapper sysUserScoreMapper = session.getMapper(SysUserScoreMapper.class);
            {
                BigDecimal divide = QueryChain.of(sysUserScoreMapper)
                        .select(SysUserScore::getScore, c -> c.multiply(BigDecimal.valueOf(2.12)).round(1))
                        .from(SysUserScore.class)
                        .eq(SysUserScore::getUserId, 3)
                        .returnType(BigDecimal.class)
                        .get();
                assertEquals(new BigDecimal("5.5").compareTo(divide), 0, "round");
            }
            {
                BigDecimal divide = QueryChain.of(sysUserScoreMapper)
                        .select(SysUserScore::getScore, c -> c.multiply(BigDecimal.valueOf(2.3)).round(1))
                        .from(SysUserScore.class)
                        .eq(SysUserScore::getUserId, 3)
                        .returnType(BigDecimal.class)
                        .get();

                assertEquals(new BigDecimal("6.0").compareTo(divide), 0, "round");
            }
        }
    }

    @Test
    public void if_() {
        Query query = Query.create().
                select(SysUser::getId, c -> c.eq(3).if_("abc", "")).
                from(SysUser.class).
                eq(SysUser::getId, 3);

        query.setReturnType(String.class);
        check("if_", "SELECT  (case when t.id = 3 then 'abc' else '' end) FROM t_sys_user t WHERE  t.id = 3", query);
    }

    @Test
    public void caseWhenThen() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .connect(self -> {
                        MybatisCmdFactory $ = self.$();
                        self.select(SysUser::getId, c -> {
                            return c.eq(1).caseThen(1)
                                    .when(self.$().create(SysUser::getId, c2 -> c2.eq(1)), 3)
                                    .else_(4);
                        });
                    })
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();
            assertEquals("1", str, "caseWhenThen");
        }
    }

    @Test
    public void caseWhenElse() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String str = QueryChain.of(sysUserMapper)
                    .connect(self -> {
                        MybatisCmdFactory $ = self.$();
                        self.select(SysUser::getId, c -> {
                            return c.eq(1)
                                    .caseThen(1)
                                    .when(Methods.eq($.field(SysUser::getId), 2), 3)
                                    .else_(4);
                        });
                    })
                    .from(SysUser.class)
                    .eq(SysUser::getId, 3)
                    .returnType(String.class)
                    .get();
            assertEquals("4", str, "caseWhenElse");
        }
    }


    @Test
    public void currentDate() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Object currentDate = QueryChain.of(sysUserMapper)
                    .select(Methods.currentDate())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(DbType.KING_BASE, () -> {
                                    queryChain.returnType(String.class);
                                })
                                .when(DbType.SQLITE, () -> {
                                    queryChain.returnType(String.class);
                                })
                                .otherwise(() -> {
                                    queryChain.returnType(LocalDate.class);
                                });
                    })
                    .get();
        }
    }

    @Test
    public void currentTime() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Object currentTime = QueryChain.of(sysUserMapper)
                    .select(Methods.currentTime())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(DbType.KING_BASE, () -> {
                                    queryChain.returnType(String.class);
                                })
                                .when(DbType.SQLITE, () -> {
                                    queryChain.returnType(String.class);
                                })
                                .otherwise(() -> {
                                    queryChain.returnType(LocalTime.class);
                                });
                    })
                    .get();

        }
    }

    @Test
    public void currentDateTime() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime currentDate = QueryChain.of(sysUserMapper)
                    .select(Methods.currentDateTime())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();

        }
    }

    @Test
    public void dateFormat() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.dateFormat(DatePattern.YYYY_MM_DD))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();
            assertEquals(date, "2023-10-11");
        }
    }

    @Test
    public void dateFormat2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.dateFormat(DatePattern.YYYY_MM_DD_HH_MM_SS))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();
            assertEquals(date, "2023-10-11 15:16:17");
        }
    }

    @Test
    public void yearTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.year())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(date, 2023);
        }
    }

    @Test
    public void monthTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.month())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(date, 10);
        }
    }

    @Test
    public void dayTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.day())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(date, 11);
        }
    }

    @Test
    public void hourTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.hour())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(date, 15);
        }
    }

    @Test
    public void weekdayTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.weekday())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            if (TestDataSource.DB_TYPE == DbType.SQLITE) {
                assertEquals(date, 3);
            } else {
                assertEquals(date, 4);
            }
        }
    }

    @Test
    public void dateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.date())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();
            assertEquals(date, "2023-10-11");
        }
    }

    //    @Test
//    public void fromUnixTimeTest() {
//        if(TestDataSource.DB_TYPE == DbType.H2){
//            return;
//        }
//        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
//            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
//            LocalDateTime date = QueryChain.of(sysUserMapper)
//                    .select(Methods.fromUnixTime(Methods.cmd(1697008577L)))
//                    .from(SysUser.class)
//                    .eq(SysUser::getId, 1)
//                    .returnType(LocalDateTime.class)
//                    .get();
//            DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//
//            String value=f.format(date);
//
//
//            if("2023-10-11 07:16:17".equals(value)){
//                return;
//            }else  if("2023-10-11 15:16:17".equals(value)){
//                return;
//            }
//             throw new RuntimeException("结果不匹配");
//        }
//    }

    @Test
    public void dateAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.dateAdd(1))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            assertEquals(date, LocalDateTime.parse("2023-10-12 15:16:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void dateSubTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.dateSub(1))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            assertEquals(date, LocalDateTime.parse("2023-10-10 15:16:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }


    @Test
    public void secondAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.secondAdd(1).as("xx"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            UpdateChain.of(sysUserMapper).set(SysUser::getId, 1);
            assertEquals(date, LocalDateTime.parse("2023-10-11 15:16:18", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void minuteAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.minuteAdd(1).as("xx"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            UpdateChain.of(sysUserMapper).set(SysUser::getId, 1);
            assertEquals(date, LocalDateTime.parse("2023-10-11 15:17:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void dateHourAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.hourAdd(1).as("xx"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            UpdateChain.of(sysUserMapper).set(SysUser::getId, 1);
            assertEquals(date, LocalDateTime.parse("2023-10-11 16:16:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void monthAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.monthAdd(1).as("xx"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            UpdateChain.of(sysUserMapper).set(SysUser::getId, 1);
            assertEquals(date, LocalDateTime.parse("2023-11-11 15:16:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void yearAddTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            LocalDateTime date = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.yearAdd(1).as("xx"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(LocalDateTime.class)
                    .get();
            UpdateChain.of(sysUserMapper).set(SysUser::getId, 1);
            assertEquals(date, LocalDateTime.parse("2024-10-11 15:16:17", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @Test
    public void charLengthTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer length = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.concat("好").charLength())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(length, 6);
        }
    }

    @Test
    public void dateDiff() {
        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE == DbType.KING_BASE) {
            //oracle 不支持
            return;
        }


        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            Integer diff = QueryChain.of(sysUserMapper)
                    .select(SysUser::getCreate_time, c -> c.dateDiff("2023-10-10 18:12:11"))
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(Integer.class)
                    .get();
            assertEquals(diff, 1);
        }
    }

    @Test
    public void upper() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String upper = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.upper())
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(upper, "ADMIN");
        }
    }

    @Test
    public void lower() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String lower = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.upper().lower())
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(lower, "admin");
        }
    }

    @Test
    public void trim() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String trim = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.concat("ad   ").trim())
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(trim, "adminad");
        }
    }

    @Test
    public void left() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String left = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.left(2))
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(left, "ad");
        }
    }

    @Test
    public void right() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String right = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.right(2))
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(right, "in");
        }
    }

    @Test
    public void subStr() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String left = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.subStr(2))
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(left, "dmin");
        }
    }

    @Test
    public void subStr2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String left = QueryChain.of(sysUserMapper)
                    .select(SysUser::getUserName, c -> c.subStr(2, 2))
                    .from(SysUser.class)
                    .eq(SysUser::getUserName, "admin")
                    .returnType(String.class)
                    .get();
            assertEquals(left, "dm");
        }
    }


    @Test
    public void jsonExtract() {
        if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName("{\"name\": \"John\", \"age\": 30, \"obj\": { \"title\": \"xx\" } }");
            sysUserMapper.update(sysUser);

            String xx = QueryChain.of(sysUserMapper)
                    .disableAutoSelect()
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(new DbType[]{DbType.MYSQL, DbType.MARIA_DB}, () -> {
                            queryChain.select(SysUser::getUserName, c -> c.mysql().jsonExtract("$.obj.title"));
                        });
                    })
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();

            if (TestDataSource.DB_TYPE == DbType.ORACLE) {
                assertEquals(xx, "\"xx\"");
            } else {
                assertEquals(xx, "\"xx\"");
            }

        }
    }

    @Test
    public void jsonContainsPath() {
        if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName("{\"name\": \"John\", \"age\": 30, \"obj\": { \"title\": \"xx\" } }");
            sysUserMapper.update(sysUser);

            Boolean exists = QueryChain.of(sysUserMapper)
                    .disableAutoSelect()
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(new DbType[]{DbType.MYSQL, DbType.MARIA_DB}, () -> {
                            queryChain.select(SysUser::getUserName, c -> c.mysql().jsonContainsPath("$.obj.title"));
                        });
                    })
                    .eq(SysUser::getId, 1)
                    .returnType(Boolean.class)
                    .get();
            assertEquals(exists, true);


        }
    }

    @Test
    public void jsonContains() {
        if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.getById(1);
            sysUser.setUserName("{\"name\": \"John\", \"age\": 30, \"obj\": { \"title\": \"\\\"xx\" } }");
            sysUserMapper.update(sysUser);

            Boolean exists = QueryChain.of(sysUserMapper)
                    .disableAutoSelect()
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(new DbType[]{DbType.MYSQL, DbType.MARIA_DB}, () -> {
                            queryChain.select(SysUser::getUserName, c -> c.mysql().jsonContains("\"xx", "$.obj.title"));
                        });
                    })
                    .eq(SysUser::getId, 1)
                    .returnType(Boolean.class)
                    .get();
            assertEquals(exists, true);

            exists = QueryChain.of(sysUserMapper)
                    .disableAutoSelect()
                    .dbAdapt((queryChain, selector) -> {
                        selector.when(new DbType[]{DbType.MYSQL, DbType.MARIA_DB}, () -> {
                            queryChain.select(SysUser::getUserName, c -> c.mysql().jsonContains(30, "$.age"));
                        });
                    })
                    .eq(SysUser::getId, 1)
                    .returnType(Boolean.class)
                    .get();
            assertEquals(exists, true);
        }
    }

    @Test
    public void md5Test() {
        if (TestDataSource.DB_TYPE != DbType.MYSQL && TestDataSource.DB_TYPE != DbType.MARIA_DB) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            String md5 = QueryChain.of(sysUserMapper)
                    .select(SysUser::getId, c -> c.mysql().md5())
                    .from(SysUser.class)
                    .eq(SysUser::getId, 1)
                    .returnType(String.class)
                    .get();

            assertEquals("c4ca4238a0b923820dcc509a6f75849b", md5);
        }
    }
}
