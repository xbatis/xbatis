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

package com.mybatis.mp.core.test.testCase;

import cn.mybatis.mp.db.annotations.TableSplitter;

public class SplitTableTestSplitter implements TableSplitter {

    @Override
    public boolean support(Class<?> type) {
        return type == Integer.class || type == int.class;
    }

    @Override
    public String split(String sourceTableName, Object splitValue) {
        Integer value = (Integer) splitValue;
        String index = "";
        if (value == 1) {
            index = "1";
        } else if (value == 2 || value == 3) {
            index = "2";
        } else {
            index = 3 + "";
        }
        return sourceTableName + "_" + index;
    }
}
