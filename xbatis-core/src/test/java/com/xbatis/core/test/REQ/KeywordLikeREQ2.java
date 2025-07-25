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

import cn.xbatis.db.Logic;
import cn.xbatis.db.annotations.Condition;
import cn.xbatis.db.annotations.ConditionTarget;
import cn.xbatis.db.annotations.Conditions;
import com.xbatis.core.test.DO.SysUser;
import lombok.Data;

import static cn.xbatis.db.annotations.Condition.Type.LIKE;

@Data
@ConditionTarget(value = SysUser.class,logic = Logic.OR)
public class KeywordLikeREQ2 {

    @Conditions(
            logic = Logic.OR,
            value = {
                    @Condition(property=SysUser.Fields.userName,value = LIKE),
                    @Condition(property=SysUser.Fields.password,value = LIKE)
            }
    )
    private String keyword;

    private Integer id;
}
