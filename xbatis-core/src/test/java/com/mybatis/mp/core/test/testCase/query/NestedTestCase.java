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


import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.*;
import com.xbatis.core.test.mapper.NestedFirstMapper;
import com.xbatis.core.test.mapper.NestedMutiFirstMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.vo.NestedFirstVo;
import com.xbatis.core.test.vo.NestedMutiFirstVo;
import db.sql.api.cmd.JoinMode;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NestedTestCase extends BaseTest {

    @Test
    public void nestedTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            NestedFirstMapper nestedFirstMapper = session.getMapper(NestedFirstMapper.class);
            List<NestedFirstVo> list = QueryChain.of(nestedFirstMapper)
                    .select(NestedFirstVo.class)
                    .from(NestedFirst.class)
                    .join(NestedFirst.class, NestedSecond.class, on -> on.eq(NestedFirst::getId, NestedSecond::getNestedOneId))
                    .join(NestedSecond.class, NestedThird.class, on -> on.eq(NestedSecond::getId, NestedThird::getNestedSecondId))
                    .returnType(NestedFirstVo.class)
                    .list();


            list.stream().forEach(item -> {

                assertEquals(item.getId(), item.getNestedFirst().getId());
                assertEquals(item.getThName(), item.getNestedFirst().getThName());

                assertEquals(item.getNestedSecondVo().getId(), item.getNestedSecondVo().getNestedSecond().getId());
                assertEquals(item.getNestedSecondVo().getThName(), item.getNestedSecondVo().getNestedSecond().getThName());
                assertEquals(item.getNestedSecondVo().getThName2(), item.getThName());
                assertEquals(item.getNestedSecondVo().getNestedOneId(), item.getNestedSecondVo().getNestedSecond().getNestedOneId());

                assertEquals(item.getNestedSecondVo().getNestedThirdVo().getId(), item.getNestedSecondVo().getNestedThirdVo().getNestedThird().getId());
                assertEquals(item.getNestedSecondVo().getNestedThirdVo().getThName(), item.getNestedSecondVo().getNestedThirdVo().getNestedThird().getThName());
                assertEquals(item.getNestedSecondVo().getNestedThirdVo().getNestedSecondId(), item.getNestedSecondVo().getNestedThirdVo().getNestedThird().getNestedSecondId());

                assertNotNull(item.getNestedSecondVo().getNestedThirdVo().getSysUser());
                assertNotNull(item.getNestedSecondVo().getNestedThirdVo().getSysRole());
            });
            NestedFirstVo nestedFirstVo = list.get(0);

            assertEquals(nestedFirstVo.getId(), 1);
            assertEquals(nestedFirstVo.getThName(), "嵌套A");

            assertEquals(nestedFirstVo.getNestedSecondVo().getId(), 1);
            assertEquals(nestedFirstVo.getNestedSecondVo().getThName(), "嵌套AA");
            assertEquals(nestedFirstVo.getNestedSecondVo().getThName2(), "嵌套A");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedOneId(), 1);

            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getId(), 1);
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getThName(), "嵌套AAA");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getNestedSecondId(), 1);


            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getSysUser().getUserName(), "admin");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getSysRole().getName(), "测试");

            nestedFirstVo = list.get(1);

            assertEquals(nestedFirstVo.getId(), 2);
            assertEquals(nestedFirstVo.getThName(), "嵌套B");

            assertEquals(nestedFirstVo.getNestedSecondVo().getId(), 2);
            assertEquals(nestedFirstVo.getNestedSecondVo().getThName(), "嵌套BA");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedOneId(), 2);

            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getId(), 2);
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getThName(), "嵌套BAA");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getNestedSecondId(), 2);


            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getSysUser().getUserName(), "test1");
            assertEquals(nestedFirstVo.getNestedSecondVo().getNestedThirdVo().getSysRole().getName(), "运维");

            System.out.println(list);

        }
    }


    @Test
    public void nestedMutiTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            NestedMutiFirstMapper nestedFirstMapper = session.getMapper(NestedMutiFirstMapper.class);
            List<NestedMutiFirstVo> list = QueryChain.of(nestedFirstMapper)
                    .select(NestedMutiFirstVo.class)
                    .from(NestedMutiFirst.class)
                    .join(NestedMutiFirst.class, NestedMutiSecond.class, on -> on.eq(NestedMutiFirst::getId, NestedMutiSecond::getNestedOneId))
                    .join(JoinMode.LEFT, NestedMutiSecond.class, NestedMutiThird.class, on -> on.eq(NestedMutiSecond::getId, NestedMutiThird::getNestedSecondId))
                    .orderBy(NestedMutiFirst::getId)
                    .orderBy(NestedMutiSecond::getId)
                    .orderBy(NestedMutiThird::getId)
                    .returnType(NestedMutiFirstVo.class)
                    .list();

            list.stream().forEach(item -> {
                assertEquals(item.getId(), item.getNestedFirst().getId());
                assertEquals(item.getThName(), item.getNestedFirst().getThName());

                item.getNestedSecondVo().stream().forEach(item2 -> {

                    assertEquals(item2.getId(), item2.getNestedSecond().getId());
                    assertEquals(item2.getThName(), item2.getNestedSecond().getThName());
                    assertEquals(item2.getNestedOneId(), item2.getNestedSecond().getNestedOneId());

                    item2.getNestedThirdVo().stream().forEach(item3 -> {
                        assertEquals(item3.getId(), item3.getNestedThird().getId());
                        assertEquals(item3.getThName(), item3.getNestedThird().getThName());
                        assertEquals(item3.getNestedSecondId(), item3.getNestedThird().getNestedSecondId());

                        assertNotNull(item3.getSysUser());
                        assertNotNull(item3.getSysRole());
                    });
                });


            });

            assertEquals(list.size(), 2);

            NestedMutiFirstVo nestedFirstVo = list.get(0);
            assertEquals(nestedFirstVo.getNestedSecondVo().size(), 2);
            assertEquals(nestedFirstVo.getId(), 1);
            assertEquals(nestedFirstVo.getThName(), "嵌套A");

            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getId(), 1);
            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getThName(), "嵌套AA");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getNestedOneId(), 1);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getId(), 2);
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getThName(), "嵌套AB");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedOneId(), 1);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getNestedThirdVo().size(), 0);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().size(), 2);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(0).getId(), 1);
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(0).getThName(), "嵌套BAA");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(0).getNestedSecondId(), 2);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(1).getId(), 2);
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(1).getThName(), "嵌套BAB");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(1).getNestedSecondId(), 2);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(0).getSysUser().getUserName(), "admin");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(0).getSysRole().getName(), "测试");

            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(1).getSysUser().getUserName(), "test1");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(1).getNestedThirdVo().get(1).getSysRole().getName(), "运维");


            nestedFirstVo = list.get(1);

            assertEquals(nestedFirstVo.getId(), 2);
            assertEquals(nestedFirstVo.getThName(), "嵌套B");
            assertEquals(nestedFirstVo.getNestedSecondVo().size(), 1);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getId(), 3);
            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getThName(), "嵌套BA");
            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getNestedOneId(), 2);

            assertEquals(nestedFirstVo.getNestedSecondVo().get(0).getNestedThirdVo().size(), 0);

            System.out.println(list);

        }
    }

}
