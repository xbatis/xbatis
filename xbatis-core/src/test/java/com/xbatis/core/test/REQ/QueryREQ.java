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

import cn.xbatis.core.sql.ObjectConditionLifeCycle;
import cn.xbatis.db.Logic;
import cn.xbatis.db.annotations.*;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

import static cn.xbatis.db.annotations.Condition.Type.*;

@Data
@ConditionTarget(value = SysUser.class)
@ConditionGroup(value = {QueryREQ.Fields.id, QueryREQ.Fields.id1}, logic = Logic.OR)
@ConditionGroup(value = {QueryREQ.Fields.id1, QueryREQ.Fields.id2, QueryREQ.Fields.keyword}, logic = Logic.OR)
@FieldNameConstants
public class QueryREQ implements ObjectConditionLifeCycle {

    private Integer id;

    @Condition(target = SysRole.class, property = SysRole.Fields.id)
    private Integer roleId;

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

    @Condition(property = SysUser.Fields.id, value = LTE)
    private Integer id1;

    @Condition(property = SysUser.Fields.id, value = GT)
    private Integer id2;

    @Condition(property = SysUser.Fields.create_time, value = BETWEEN, toEndDayTime = true)
    private LocalDate[] rangeTimes;

    @Condition(property = SysUser.Fields.id, value = EQ)
    private Integer defaultId;

    @Ignore
    private String rangeType;

    @Conditions(
            logic = Logic.OR,
            value = {
                    @Condition(property = SysUser.Fields.userName, value = LIKE),
                    @Condition(property = SysUser.Fields.password, value = LIKE)
            }
    )
    private String keyword;

    @Conditions(
            logic = Logic.AND,
            value = {
                    @Condition(property = SysUser.Fields.userName, value = NOT_NULL),
                    @Condition(property = SysUser.Fields.userName, value = NOT_BLANK)
            }
    )
    private boolean idNotNullAndBlank = true;

    @Override
    public void beforeBuildCondition() {
        System.out.println("在构建条件前执行");
//        this.rangeType = "TODAY";
//        if (StringUtils.isNotBlank(rangeType) && this.rangeTimes == null) {
//            this.rangeTimes = XbatisConfig.getDynamicValue(this.getClass(), LocalDate[].class, this.rangeType);
//            XbatisConfig.setDynamicValue("{TODAY}",(clazz,type)->{
//                return LocalDate.now();
//            });
//        }
    }
}
