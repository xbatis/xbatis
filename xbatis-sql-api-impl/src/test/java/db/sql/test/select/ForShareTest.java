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

package db.sql.test.select;

import db.sql.api.impl.cmd.executor.Query;
import db.sql.test.BaseTest;
import org.junit.jupiter.api.Test;

public class ForShareTest extends BaseTest {

    @Test
    public void forShare() {
        check("forShare测试", "SELECT id FOR SHARE", new Query()
                .select(userTable().$("id")).forShare());
    }

    @Test
    public void forShareNoWait() {
        check("forShareNoWait测试", "SELECT id FOR SHARE NOWAIT", new Query()
                .select(userTable().$("id")).forShareNoWait());
    }

    @Test
    public void forShareSkipLocked() {
        check("forShareSkipLocked测试", "SELECT id FOR SHARE SKIP LOCKED", new Query()
                .select(userTable().$("id")).forShareSkipLocked());
    }

    @Test
    public void forShareOptions() {
        check("forShareOptions测试", "SELECT id FOR SHARE WAIT 3", new Query()
                .select(userTable().$("id")).forShare("WAIT 3"));
    }

}
