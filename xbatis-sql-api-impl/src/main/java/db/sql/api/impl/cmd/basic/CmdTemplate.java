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

package db.sql.api.impl.cmd.basic;

import db.sql.api.Cmd;

/**
 * SQL模板类
 * 采用MessageFormat.format格式化模板
 */
public class CmdTemplate extends BaseTemplate<CmdTemplate> {

    public CmdTemplate(String template, Object... params) {
        super(template, params);
    }

    public CmdTemplate(String template, Cmd... params) {
        super(template, params);
    }

    public static CmdTemplate create(String template, Object... params) {
        return new CmdTemplate(template, params);
    }

    public static CmdTemplate create(String template, Cmd... params) {
        return new CmdTemplate(template, params);
    }
}
