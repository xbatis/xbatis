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

import cn.xbatis.db.annotations.LogicDelete;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Table
@FieldNameConstants
public class LogicDeleteTest {

    @TableId
    private Long id;

    private String name;

    private LocalDateTime deleteTime;

    @LogicDelete(beforeValue = "0", afterValue = "{LOGIC_DELETE_VALUE}", deleteTimeField = "deleteTime")
    private Byte deleted;

//    @LogicDelete(  afterValue = "{NOW}" )
//    private LocalDateTime deleteTime;
}
