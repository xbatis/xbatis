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

package com.xbatis.core.test.model;

import cn.xbatis.db.Model;
import com.xbatis.core.test.DO.DefaultValue2Test;
import com.xbatis.core.test.DO.TestEnum;
import lombok.Data;

import java.time.LocalDateTime;


@Data

public class DefaultValue2ModelTest implements Model<DefaultValue2Test> {

    private Integer id;


    private String value1;


    private Integer value2;


    private Integer value4;


    private LocalDateTime createTime;


    private TestEnum value3;
}
