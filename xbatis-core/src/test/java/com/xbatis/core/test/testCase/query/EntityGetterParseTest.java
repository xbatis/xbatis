/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package com.xbatis.core.test.testCase.query;


import cn.xbatis.core.util.EntityUtil;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.testCase.BaseTest;
import db.sql.api.Getter;
import db.sql.api.tookit.LambdaUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityGetterParseTest extends BaseTest {

    @Test
    public void parse() {
        Getter<SysUser> getter = EntityUtil.createGetter(SysUser.class, "id");
        LambdaUtil.LambdaFieldInfo<SysUser> userLambdaFieldInfo = LambdaUtil.getFieldInfo(getter);
        SysUser sysUser = new SysUser();
        sysUser.setId(1123);
        assertEquals(getter.apply(sysUser), 1123);
        assertEquals(userLambdaFieldInfo.getType(), SysUser.class);
        assertEquals(userLambdaFieldInfo.getName(), "id");
    }
}
