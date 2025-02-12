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

package cn.mybatis.mp.page;

public interface IPager<T> {

    /**
     * 获得字段的值
     * 只需要 实现 PagerField.IS_EXECUTE_COUNT、PagerField.NUMBER、PagerField.SIZE 3个字段GET即可
     *
     * @param field 字段，具体看PageField
     * @return 字段的值
     */
    <V> V get(PagerField<V> field);

    /**
     * 设置字段的值
     * 只需要 实现 PagerField.TOTAL、PagerField.RESULTS 2个字段SET即可
     *
     * @param field 字段，具体看PageField
     * @param value 值
     */
    <V> void set(PagerField<V> field, V value);
}
