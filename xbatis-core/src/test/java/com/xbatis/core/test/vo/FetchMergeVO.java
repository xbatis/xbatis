/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.ResultEntity;
import com.xbatis.core.test.DO.FetchMerge;
import com.xbatis.core.test.DO.SysRole;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@ResultEntity(FetchMerge.class)
public class FetchMergeVO extends FetchMerge {

    @Fetch(mergeGroup = "role", property = Fields.roleId1, target = SysRole.class, targetProperty = SysRole.Fields.id, targetSelectProperty = SysRole.Fields.name)
    private List<String> roleNames;

    @Fetch(mergeGroup = "role", property = Fields.roleId1, target = SysRole.class, targetProperty = SysRole.Fields.id, targetSelectProperty = SysRole.Fields.name)
    private String roleName1;

    @Fetch(mergeGroup = "role", property = Fields.roleId1, target = SysRole.class, targetProperty = SysRole.Fields.id, targetSelectProperty = SysRole.Fields.id)
    private Integer roleid1;

    @Fetch(mergeGroup = "role", property = Fields.roleId2, target = SysRole.class, targetProperty = SysRole.Fields.id, targetSelectProperty = SysRole.Fields.name)
    private String roleName2;

    @Fetch(mergeGroup = "role", property = Fields.roleId3, target = SysRole.class, targetProperty = SysRole.Fields.id, targetSelectProperty = SysRole.Fields.name)
    private String roleName3;

    @Fetch(mergeGroup = "role2", property = Fields.roleId1, target = SysRole.class, targetProperty = SysRole.Fields.id)
    private SysRole role1;

    @Fetch(mergeGroup = "role2", property = Fields.roleId2, target = SysRole.class, targetProperty = SysRole.Fields.id)
    private SysRole role2;

    @Fetch(mergeGroup = "role2", property = Fields.roleId3, target = SysRole.class, targetProperty = SysRole.Fields.id)
    private SysRole role3;


}
