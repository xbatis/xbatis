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

package com.mybatis.mp.core.test.vo;

import cn.mybatis.mp.db.annotations.PutValue;
import cn.mybatis.mp.db.annotations.ResultEntity;
import com.mybatis.mp.core.test.DO.SysUser;
import com.mybatis.mp.core.test.GetPutValueFactory;
import com.mybatis.mp.core.test.GetPutValueFactory2;
import lombok.Data;

@Data
@ResultEntity(SysUser.class)
public class PutValueVo {

    private Integer id;

    @PutValue(source = SysUser.class, property = "id", factory = GetPutValueFactory.class, method = "getPutValue1")
    private String enumName;

    @PutValue(source = SysUser.class, property = "id", factory = GetPutValueFactory.class, method = "getPutValue1", defaultValue = "NULL")
    private String defaultEnumName;

    @PutValue(source = SysUser.class, property = "id", factory = GetPutValueFactory2.class, method = "getPutValue2")
    private String enumName2;

    @PutValue(source = SysUser.class, property = "id", factory = GetPutValueFactory2.class, method = "getPutValue2", defaultValue = "NULL")
    private String defaultEnumName2;
}

