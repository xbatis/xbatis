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
import cn.xbatis.db.annotations.ResultField;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ResultEntity(SysRole.class)
public class OneToManyVo {

    private Integer id;

    private String name;

    private LocalDateTime createTime;

    @ResultField(value = "otherName")
    private String asName;

    @NestedResultEntity(target = SysUser.class)
    private List<SysUser> sysUserList;
}
