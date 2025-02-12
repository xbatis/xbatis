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
import cn.mybatis.mp.db.annotations.NestedResultEntity;
import com.mybatis.mp.core.test.DO.NestedThird;
import com.mybatis.mp.core.test.DO.SysRole;
import com.mybatis.mp.core.test.DO.SysUser;
import lombok.Data;

@Data
public class NestedThirdVo {

    private Integer id;

    private Integer nestedSecondId;

    private String thName;

    @NestedResultEntity(target = NestedThird.class)
    private NestedThird nestedThird;

    @Fetch(source = NestedThird.class, property = "id", target = SysRole.class, targetProperty = "id")
    private SysRoleVo sysRole;

    @Fetch(source = NestedThird.class, property = "id", target = SysUser.class, targetProperty = "id")
    private SysUser sysUser;
}
