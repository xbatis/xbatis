/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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
import com.mybatis.mp.core.test.DO.SysUser;
import lombok.Data;

import java.util.List;

@Data
@ResultEntity(Void.class)
public class FetchSysRoleVo3 {

    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, targetSelectProperty = "userName", orderBy = "id asc")
    private List<String> sysRoleNames;

    @Fetch(column = "id", target = SysUser.class, targetProperty = SysUser.Fields.role_id, orderBy = "[{id} asc,{userName} desc]")
    private List<FetchSysUserForRoleVo> sysRole;
}
