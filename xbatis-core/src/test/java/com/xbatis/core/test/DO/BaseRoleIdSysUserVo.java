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

package com.xbatis.core.test.DO;

import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.NestedResultEntity;
import cn.xbatis.db.annotations.ResultEntity;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ResultEntity(BaseIDSysUser.class)
public class BaseRoleIdSysUserVo extends BaseRoleId<String> {

    @TableId
    private Long id;

    private String password;

    private LocalDateTime create_time;

    @Fetch(source = BaseIDSysUser.class, property = BaseIDSysUser.Fields.id, target = BaseIDSysUser.class, targetProperty = BaseIDSysUser.Fields.id)
    private BaseIDSysUser baseIDSysUser;

    @NestedResultEntity(target = BaseIDSysUser.class)
    private BaseIDSysUser baseIDSysUser2;


    @Fetch(source = BaseIDSysUser.class, property = BaseIDSysUser.Fields.id, target = BaseIDSysUser.class, targetProperty = BaseIDSysUser.Fields.id)
    private List<BaseIDSysUser> baseIDSysUsers;

    @Fetch(source = BaseIDSysUser.class, property = BaseIDSysUser.Fields.id, target = BaseIDSysUser.class, targetProperty = BaseIDSysUser.Fields.id)
    private BaseRoleIdSysUserVo2 baseRoleIdSysUserVo;

    @NestedResultEntity(target = BaseIDSysUser.class)
    private BaseRoleIdSysUserVo2 baseRoleIdSysUserVo2;
}
