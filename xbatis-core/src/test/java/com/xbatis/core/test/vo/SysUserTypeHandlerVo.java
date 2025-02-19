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

import cn.xbatis.db.annotations.NestedResultEntityField;
import cn.xbatis.db.annotations.ResultField;
import cn.xbatis.db.annotations.TypeHandler;
import com.xbatis.core.test.typeHandler.PhoneTypeHandler;
import lombok.Data;

@Data
public class SysUserTypeHandlerVo {

    private Integer id;

    @TypeHandler(PhoneTypeHandler.class)
    private String userName;

    @TypeHandler(PhoneTypeHandler.class)
    @NestedResultEntityField("password")
    private String pwd;

    @TypeHandler(PhoneTypeHandler.class)
    @ResultField("kk")
    private String kkName;

}
