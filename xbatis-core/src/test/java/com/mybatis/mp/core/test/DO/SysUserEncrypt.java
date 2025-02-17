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

import cn.xbatis.db.annotations.ForeignKey;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import com.xbatis.core.test.typeHandler.EncryptTypeHandler;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Table("t_sys_user")
@FieldNameConstants
public class SysUserEncrypt {

    @TableId
    @TableField("id")
    private Integer id;

    @TableField(typeHandler = EncryptTypeHandler.class)
    private String userName;

    private String password;

    @ForeignKey(SysRole.class)
    private Integer role_id;

    private LocalDateTime create_time;
}
