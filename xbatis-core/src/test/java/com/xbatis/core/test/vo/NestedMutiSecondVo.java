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
import cn.xbatis.db.annotations.TenantId;
import com.xbatis.core.test.DO.NestedMutiSecond;
import com.xbatis.core.test.DO.NestedMutiThird;
import lombok.Data;

import java.util.List;

@Data
public class NestedMutiSecondVo {

    @TenantId
    private Integer id;

    private Integer nestedOneId;

    private String thName;

    @NestedResultEntity(target = NestedMutiSecond.class)
    private NestedMutiSecond nestedSecond;


    @NestedResultEntity(target = NestedMutiThird.class)
    private List<NestedMutiThirdVo> nestedThirdVo;
}
