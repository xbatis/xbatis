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

package com.mybatis.mp.core.test.DO;

import cn.mybatis.mp.db.annotations.ForeignKey;
import cn.mybatis.mp.db.annotations.Table;
import cn.mybatis.mp.db.annotations.TableField;
import cn.mybatis.mp.db.annotations.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Table("t_sys_user")
@FieldNameConstants
public class SysUser extends SysUserBase {

    @TableId
    @TableField("id")
    private Integer id;
    private String password;
    @ForeignKey(SysRole.class)
    private Integer role_id;
    private LocalDateTime create_time;

    public static final class Fields extends SysUserBase.Fields {

    }
}
