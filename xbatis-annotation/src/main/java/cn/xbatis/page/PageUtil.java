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

public class PageUtil {

    /**
     * 获取分页 offset
     *
     * @param number 页码
     * @param size   分页条数
     * @return
     */
    public static final int getOffset(int number, int size) {
        return (number - 1) * size;
    }

    /**
     * 获取总页数
     *
     * @param size
     * @param total
     * @return
     */
    public static int getTotalPage(Integer size, Integer total) {
        if (size == null) {
            if (total == null || total == 0) {
                return 0;
            }
            return 1;
        }

        if (total == null || total < 0) {
            return 0;
        }
        return total / size + (total % size == 0 ? 0 : 1);
    }
}
