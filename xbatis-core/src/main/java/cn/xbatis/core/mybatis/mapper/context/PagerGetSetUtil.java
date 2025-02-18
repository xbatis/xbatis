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

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.page.PagerField;

import java.util.List;

public final class PagerGetSetUtil {

    public static <V> V get(Pager pager, PagerField<V> field) {
        if (PagerField.IS_EXECUTE_COUNT == field) {
            return (V) pager.isExecuteCount();
        }
        if (PagerField.NUMBER == field) {
            return (V) pager.getNumber();
        }
        if (PagerField.SIZE == field) {
            return (V) pager.getSize();
        }
        throw new RuntimeException("not support field: " + field);
    }

    public static <V> void set(Pager pager, PagerField<V> field, V value) {
        if (PagerField.TOTAL == field) {
            pager.setTotal((Integer) value);
            return;
        }
        if (PagerField.RESULTS == field) {
            pager.setResults((List) value);
            return;
        }
        throw new RuntimeException("not support field: " + field);
    }


}
