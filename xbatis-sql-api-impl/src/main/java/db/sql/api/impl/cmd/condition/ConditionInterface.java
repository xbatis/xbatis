/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
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

package db.sql.api.impl.cmd.condition;

import db.sql.api.Cmd;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.dbFun.Case;
import db.sql.api.impl.cmd.dbFun.If;
import db.sql.api.impl.cmd.dbFun.Not;

import java.io.Serializable;

public interface ConditionInterface extends ICondition {

    /**
     * 条件取反
     *
     * @return Not
     */
    default Not not() {
        return Methods.not(this);
    }

    /**
     * if
     *
     * @param value
     * @param elseValue
     * @return
     */
    default If if_(Cmd value, Cmd elseValue) {
        return new If(this, value, elseValue);
    }

    default If if_(Cmd value, Serializable elseValue) {
        return new If(this, value, Methods.cmd(elseValue));
    }

    default If if_(Serializable value, Serializable elseValue) {
        return new If(this, value, elseValue);
    }


    /**
     * case then value?
     *
     * @param thenValue
     * @return Case
     */
    default Case caseThen(Serializable thenValue) {
        return this.caseThen(Methods.cmd(thenValue));
    }

    /**
     * case then value?
     *
     * @param thenValue
     * @return Case
     */
    default Case caseThen(Cmd thenValue) {
        return new Case().when(this, thenValue);
    }
}
