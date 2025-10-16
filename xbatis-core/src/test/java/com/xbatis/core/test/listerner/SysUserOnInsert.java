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

package com.xbatis.core.test.listerner;

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.ForeignKey;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import cn.xbatis.listener.annotations.OnInsert;
import cn.xbatis.listener.annotations.OnUpdate;
import com.xbatis.core.test.DO.SysRole;
import db.sql.api.DbType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;


@Data
@Table("t_sys_user")
@FieldNameConstants
@OnInsert(SysUserOnInsertListener.class)
@OnUpdate(SysUserOnUpdateListener.class)
public class SysUserOnInsert {

    @TableId
    @TableId(dbType = DbType.KING_BASE, value = IdAutoType.SQL, sql = "select t_sys_user_seq.NEXTVAL FROM dual")
    private Integer id;

    private String userName;

    private String password;

    @ForeignKey(SysRole.class)
    private Integer role_id;

    @TableField(defaultValue = "{NOW}", updateDefaultValue = "{NOW}")
    private LocalDateTime createTime;

}
