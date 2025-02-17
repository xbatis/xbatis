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

package cn.xbatis.page;

import java.util.List;

public final class PagerField<V> {

    /**
     * 是否count字段
     */
    public static final PagerField<Boolean> IS_EXECUTE_COUNT = new PagerField<>(0, Boolean.class);

    /**
     * 页码
     */
    public static final PagerField<Integer> NUMBER = new PagerField<>(1, Integer.class);

    /**
     * 分页条数
     */
    public static final PagerField<Integer> SIZE = new PagerField<>(2, Integer.class);

    /**
     * 总数
     */
    public static final PagerField<Integer> TOTAL = new PagerField<>(3, Integer.class);

    /**
     * 列表结果
     */
    public static final PagerField<List> RESULTS = new PagerField<>(4, List.class);

    private final Class<V> type;

    private final int code;

    private PagerField(int code, Class<V> type) {
        this.code = code;
        this.type = type;
    }

    public Class<V> getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "PagerField{" +
                "type=" + type +
                ", code=" + code +
                '}';
    }
}
