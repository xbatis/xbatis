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

package com.xbatis.core.test.REQ;

import cn.xbatis.db.annotations.Condition;
import cn.xbatis.db.annotations.ConditionTarget;
import com.xbatis.core.test.DO.SysUser;
import lombok.Data;

import static cn.xbatis.db.annotations.Condition.Type.*;

@Data
@ConditionTarget(SysUser.class)
public class QueryREQ {

    private Integer id;

    @Condition(value = LIKE)
    private String userName;

    @Condition(property = SysUser.Fields.id, value = GT)
    private Integer gtId;

    @Condition(property = SysUser.Fields.id, value = GTE)
    private Integer gteId;

    @Condition(property = SysUser.Fields.id, value = LT)
    private Integer ltId;

    @Condition(property = SysUser.Fields.id, value = LTE)
    private Integer lteId;

    @Condition(property = SysUser.Fields.id, value = BETWEEN)
    private Integer[] btIds;
}
