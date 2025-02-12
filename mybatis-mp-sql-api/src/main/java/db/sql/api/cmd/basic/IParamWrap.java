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

package db.sql.api.cmd.basic;

import db.sql.api.cmd.LikeMode;

public interface IParamWrap {

    default Object paramWrap(Object value) {
        return value;
    }

    /**
     * like 参数包裹
     *
     * @param param
     * @param likeMode
     * @param isNotLike
     * @return 假如是单个对象 说明 like 模式未变 假如返回是数组2位，第1位应为LikeMode 第2位位参数值
     */
    default Object likeParamWrap(LikeMode likeMode, Object param, boolean isNotLike) {
        return param;
    }
}
