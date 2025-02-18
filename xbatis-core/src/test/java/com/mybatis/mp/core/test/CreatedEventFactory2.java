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

package com.xbatis.core.test;

import com.xbatis.core.test.vo.CreatedEventFetchTestVo;
import com.xbatis.core.test.vo.CreatedEventNestedTestVo;
import com.xbatis.core.test.vo.CreatedEventTestVo;

import java.util.Map;

public class CreatedEventFactory2 {

    public static void onCreatedEvent(Map<String, Object> cache, CreatedEventTestVo onCreatedEventTestVo) {
        System.out.println(cache + ">>>>>>>>>>>>>>>onCreatedEvent1.");
        cache.put("onCreatedEvent1", "11");
        onCreatedEventTestVo.setSourcePut("CreatedEventTestVo");
    }


    public static void onCreatedEvent(Map<String, Object> cache, CreatedEventNestedTestVo onCreatedEventNestedTestVo) {
        System.out.println(cache + ">>>>>>>>>>>>>>>onCreatedEvent22.");
        cache.put("onCreatedEvent2", "22");
        onCreatedEventNestedTestVo.setSourcePut("CreatedEventNestedTestVo");
    }

    public static void onCreatedEvent(CreatedEventFetchTestVo onCreatedEventFetchTestVo) {
        onCreatedEventFetchTestVo.setSourcePut("CreatedEventFetchTestVo");
    }
}
