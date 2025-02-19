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

import cn.xbatis.db.annotations.NestedResultEntity;
import cn.xbatis.db.annotations.ResultEntityField;
import cn.xbatis.db.annotations.TenantId;
import com.xbatis.core.test.DO.NestedFirst;
import com.xbatis.core.test.DO.NestedSecond;
import com.xbatis.core.test.DO.NestedThird;
import lombok.Data;

@Data
public class NestedSecondVo {

    @TenantId
    private Integer id;

    private Integer nestedOneId;

    private String thName;

    @ResultEntityField(target = NestedFirst.class, property = "thName")
    private String thName2;

    @NestedResultEntity(target = NestedSecond.class)
    private NestedSecond nestedSecond;


    @NestedResultEntity(target = NestedThird.class)
    private NestedThirdVo nestedThirdVo;
}
