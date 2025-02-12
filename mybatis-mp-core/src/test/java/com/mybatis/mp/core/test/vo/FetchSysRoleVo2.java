/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package com.mybatis.mp.core.test.vo;

import cn.mybatis.mp.db.annotations.Fetch;
import cn.mybatis.mp.db.annotations.ResultEntity;
import com.mybatis.mp.core.test.DO.SysRole;
import com.mybatis.mp.core.test.DO.SysUser;
import lombok.Data;

import java.util.List;

@Data
@ResultEntity(SysRole.class)
public class FetchSysRoleVo2 {

    private Integer id;

    private String name;


    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, targetSelectProperty = "userName", orderBy = "id asc")
    private List<String> sysRoleNames;


    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, targetSelectProperty = "[count({id})]")
    private Integer cnts;

    @Fetch(column = "id", target = SysRole.class, targetProperty = "id", targetSelectProperty = "name")
    private String roleName;

    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, targetSelectProperty = "[count({id})]")
    private Integer cnts2;

    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, targetSelectProperty = "[count({id})]", groupBy = "role_id", forceUseIn = true)
    private Integer cnts3;
}
