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

package com.xbatis.core.test.vo;

import cn.xbatis.db.FetchPropertyType;
import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.ResultEntity;
import com.xbatis.core.test.DO.Addr;
import com.xbatis.core.test.DO.FetchAddr;
import lombok.Data;

import java.util.List;

@Data
@ResultEntity(FetchAddr.class)
public class FetchAddrVo extends FetchAddr {

    @Fetch(property = FetchAddr.Fields.addrs1,
            propertyType = FetchPropertyType.MULTI,
            target = Addr.class,
            targetProperty = Addr.Fields.id,
            targetSelectProperty = Addr.Fields.name
    )
    private List<String> faddrs1;

    @Fetch(property = FetchAddr.Fields.addrs2,
            propertyType = FetchPropertyType.MULTI,
            target = Addr.class,
            targetProperty = Addr.Fields.id,
            targetSelectProperty = Addr.Fields.name
    )
    private List<String> faddrs2;

    @Fetch(property = FetchAddr.Fields.addrs3,
            propertyType = FetchPropertyType.ARRAY,
            target = Addr.class,
            targetProperty = Addr.Fields.id,
            targetSelectProperty = Addr.Fields.name
    )
    private List<String> faddrs3;


    @Fetch(property = FetchAddr.Fields.addrs1,
            propertyType = FetchPropertyType.MULTI,
            target = Addr.class,
            targetProperty = Addr.Fields.id
    )
    private List<Addr> faddrs11;

    @Fetch(property = FetchAddr.Fields.addrs2,
            propertyType = FetchPropertyType.MULTI,
            target = Addr.class,
            targetProperty = Addr.Fields.id
    )
    private List<Addr> faddrs21;

    @Fetch(property = FetchAddr.Fields.addrs3,
            propertyType = FetchPropertyType.ARRAY,
            target = Addr.class,
            targetProperty = Addr.Fields.id
    )
    private List<Addr> faddrs31;

}
