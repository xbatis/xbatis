/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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

package db.sql.api.impl.cmd.executor;

import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.struct.Where;

public class Update extends AbstractUpdate<Update, CmdFactory> {
    public Update() {
        super(new CmdFactory());
    }

    public Update(CmdFactory cmdFactory) {
        super(cmdFactory);
    }

    public Update(Where where) {
        super(where);
    }
}
