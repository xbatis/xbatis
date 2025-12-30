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

import cn.xbatis.core.mybatis.typeHandler.LongStringTypeHandler;
import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import db.sql.api.DbType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Table
@FieldNameConstants
public class IdTest {


    @TableId
    @TableId(dbType = DbType.H2, value = IdAutoType.AUTO)
    @TableId(dbType = DbType.SQL_SERVER, value = IdAutoType.AUTO)
    @TableId(dbType = DbType.GAUSS, value = IdAutoType.SQL, sql = "select nextval('id_test_id_seq')")
    @TableId(dbType = DbType.PGSQL, value = IdAutoType.SQL, sql = "select nextval('id_test_id_seq')")
    @TableId(dbType = DbType.ORACLE, value = IdAutoType.SQL, sql = "select id_test_seq.NEXTVAL FROM dual")
    @TableId(dbType = DbType.KING_BASE, value = IdAutoType.SQL, sql = "select id_test_seq.NEXTVAL FROM dual")
    @TableField(typeHandler = LongStringTypeHandler.class)
    private String id;

    private LocalDateTime createTime;

}
