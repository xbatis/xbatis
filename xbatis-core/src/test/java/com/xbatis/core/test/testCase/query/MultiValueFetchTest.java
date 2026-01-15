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
import com.xbatis.core.test.DO.Addr;
import com.xbatis.core.test.DO.FetchAddr;
import com.xbatis.core.test.mapper.FetchAddrMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import com.xbatis.core.test.vo.FetchAddrVo;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiValueFetchTest extends BaseTest {

    @Test
    public void fetchMulti() {
        if (TestDataSource.DB_TYPE != DbType.H2 && TestDataSource.DB_TYPE.getDbModel() != DbModel.MYSQL && TestDataSource.DB_TYPE != DbType.MYSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            FetchAddrMapper mapper = session.getMapper(FetchAddrMapper.class);

            List<FetchAddrVo> list = QueryChain.of(mapper)
                    .returnType(FetchAddrVo.class)
                    .list();

            System.out.println(list);
            assertEquals(list.get(0).getId(), 1);
            assertEquals(list.get(1).getId(), 2);
            assertEquals(list.get(2).getId(), 3);

            assertEquals(list.get(0).getAddrs3(), Arrays.asList(1, 4, 1));
            assertEquals(list.get(1).getAddrs3(), Arrays.asList(5, 1));
            assertEquals(list.get(2).getAddrs3(), Arrays.asList(2));

            assertEquals(list.get(0).getAddrs33(), Arrays.asList(1L, 4L, 1L));
            assertEquals(list.get(1).getAddrs33(), Arrays.asList(5L, 1L));
            assertEquals(list.get(2).getAddrs33(), Arrays.asList(2L));

            assertEquals(3, list.size());
            assertEquals(list.get(0).getFaddrs1(), Arrays.asList("江西", "南昌"));
            assertEquals(list.get(0).getFaddrs2(), Arrays.asList("江西", "南昌", "赣州"));
            assertEquals(list.get(0).getFaddrs3(), Arrays.asList("江西", "章贡区", "江西"));

            assertEquals(list.get(1).getFaddrs1(), Arrays.asList("南昌", "章贡区"));
            assertEquals(list.get(1).getFaddrs2(), Arrays.asList("江西", "瑞金市"));
            assertEquals(list.get(1).getFaddrs3(), Arrays.asList("瑞金市", "江西"));

            assertEquals(list.get(2).getFaddrs1(), Arrays.asList("江西", "兴国县"));
            assertEquals(list.get(2).getFaddrs2(), Arrays.asList("兴国县"));
            assertEquals(list.get(2).getFaddrs3(), Arrays.asList("南昌"));


            //--------


            assertEquals(list.get(0).getFaddrs11(), Arrays.asList(Addr.of(1, "江西"), Addr.of(2, "南昌")));
            assertEquals(list.get(0).getFaddrs21(), Arrays.asList(Addr.of(1, "江西"), Addr.of(2, "南昌"), Addr.of(3, "赣州")));
            assertEquals(list.get(0).getFaddrs31(), Arrays.asList(Addr.of(1, "江西"), Addr.of(4, "章贡区"), Addr.of(1, "江西")));

            assertEquals(list.get(1).getFaddrs11(), Arrays.asList(Addr.of(2, "南昌"), Addr.of(4, "章贡区")));
            assertEquals(list.get(1).getFaddrs21(), Arrays.asList(Addr.of(1, "江西"), Addr.of(5, "瑞金市")));
            assertEquals(list.get(1).getFaddrs31(), Arrays.asList(Addr.of(5, "瑞金市"), Addr.of(1, "江西")));

            assertEquals(list.get(2).getFaddrs11(), Arrays.asList(Addr.of(1, "江西"), Addr.of(6, "兴国县")));
            assertEquals(list.get(2).getFaddrs21(), Arrays.asList(Addr.of(6, "兴国县")));
            assertEquals(list.get(2).getFaddrs31(), Arrays.asList(Addr.of(2, "南昌")));
        }
    }

    @Test
    public void fetchMulti2() {
        if (TestDataSource.DB_TYPE != DbType.H2 && TestDataSource.DB_TYPE.getDbModel() != DbModel.MYSQL && TestDataSource.DB_TYPE != DbType.MYSQL) {
            return;
        }
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            FetchAddrMapper mapper = session.getMapper(FetchAddrMapper.class);

            List<FetchAddr> list = QueryChain.of(mapper)
                    .list();
            mapper.update(list);
            //session.commit();
            list = QueryChain.of(mapper)
                    .list();

            System.out.println(list);
        }
    }
}
