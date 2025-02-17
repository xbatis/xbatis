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
import cn.xbatis.db.annotations.ResultEntity;
import cn.xbatis.db.annotations.ResultEntityField;
import cn.xbatis.db.annotations.ResultField;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import lombok.Data;

@Data
@ResultEntity(SysUser.class)
public class SysUserRoleVo {

    private Integer id;

    private String userName;

    @ResultEntityField(target = SysUser.class, property = "password")
    private String pwd;

    @ResultEntityField(target = SysRole.class)
    private String name;

    @ResultField("maxId")
    private Integer maxId;

    @NestedResultEntity(target = SysRole.class)
    private NestedSysRoleVo role;
}
