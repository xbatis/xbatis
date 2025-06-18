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

import cn.xbatis.db.Model;
import cn.xbatis.listener.annotations.OnInsert;
import cn.xbatis.listener.annotations.OnUpdate;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;


@Data
@FieldNameConstants
@OnInsert(SysUserModelOnInsertListener.class)
@OnUpdate(SysUserModelOnUpdateListener.class)
public class SysUserModelOnInsert implements Model<SysUserOnInsert> {


    private Integer id;

    private String userName;

    private String password;


    private Integer role_id;


    private LocalDateTime createTime;

}
