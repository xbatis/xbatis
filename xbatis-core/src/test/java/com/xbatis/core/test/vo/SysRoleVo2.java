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

import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.PutEnumValue;
import cn.xbatis.db.annotations.PutValue;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.GetPutValueFactory;
import lombok.Data;

@Data
public class SysRoleVo2 {

    private Integer id;

    private String name;

    @Fetch(property = "id", targetProperty = "id", target = SysRole.class)
    private SysRole sysRole;

    @PutEnumValue(source = SysUser.class, property = "id", target = PutValueEnum.class)
    private String idName;

    @PutValue(source = SysUser.class, property = "id", factory = GetPutValueFactory.class, method = "getPutValue1")
    private String idName2;

    @PutEnumValue(property = "id", target = PutValueEnum.class)
    private String idName3;

    @PutValue(property = "id", factory = GetPutValueFactory.class, method = "getPutValue1")
    private String idName4;
}
