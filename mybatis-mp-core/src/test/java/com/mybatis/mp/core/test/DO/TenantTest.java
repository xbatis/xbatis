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

import cn.mybatis.mp.core.incrementer.IdentifierGeneratorType;
import cn.mybatis.mp.db.IdAutoType;
import cn.mybatis.mp.db.annotations.Table;
import cn.mybatis.mp.db.annotations.TableField;
import cn.mybatis.mp.db.annotations.TableId;
import cn.mybatis.mp.db.annotations.TenantId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Table
@FieldNameConstants
public class TenantTest {

    @TableId(value = IdAutoType.GENERATOR, generatorName = IdentifierGeneratorType.UUID)
    private String id;

    @TenantId
    private Integer tenantId;

    private String name;

    @TableField(update = false)
    private LocalDateTime createTime;
}
