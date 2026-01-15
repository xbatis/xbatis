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

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import com.xbatis.core.test.MyDbType;
import db.sql.api.DbType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Table("default_value_test")
@Data
@FieldNameConstants
public class DefaultValue2Test {

    @TableId
    @TableId(dbType = DbType.Name.ORACLE, value = IdAutoType.SQL, sql = "select default_value_test_seq.NEXTVAL FROM dual")
    @TableId(dbType = MyDbType.Name.LIKE_ORACLE, value = IdAutoType.SQL, sql = "select default_value_test_seq.NEXTVAL FROM dual")
    @TableId(dbType = DbType.Name.KING_BASE, value = IdAutoType.SQL, sql = "select default_value_test_seq.NEXTVAL FROM dual")
    private Integer id;

    @TableField(defaultValue = "{BLANK}")
    private String value1;

    @TableField(defaultValue = "{NOW2}", updateDefaultValue = "{NOW2}")
    private Integer value2;

    @TableField(defaultValue = "{NOW2}", updateDefaultValue = "{NOW2}")
    private Integer value4;

    @TableField(defaultValue = "{NOW}")
    private LocalDateTime createTime;

    @TableField(defaultValue = "a1")
    private TestEnum value3;
}
