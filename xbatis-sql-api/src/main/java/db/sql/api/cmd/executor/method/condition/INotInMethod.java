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

package db.sql.api.cmd.executor.method.condition;

import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.executor.method.condition.compare.INotInGetterCompare;

import java.io.Serializable;
import java.util.Collection;

public interface INotInMethod<RV, COLUMN> extends INotInGetterCompare<RV> {

    RV notIn(COLUMN column, IQuery query);

    RV notIn(COLUMN column, Serializable... values);

    RV notIn(COLUMN column, Collection<? extends Serializable> values);
}
