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

package cn.mybatis.mp.core.db.reflect;

import cn.mybatis.mp.db.annotations.TableField;

/**
 * 默认 配置
 */
public final class Default {

    @TableField
    private int ___defaultField;

    public static TableField defaultTableFieldAnnotation() {
        try {
            return Default.class.getDeclaredField("___defaultField").getAnnotation(TableField.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
