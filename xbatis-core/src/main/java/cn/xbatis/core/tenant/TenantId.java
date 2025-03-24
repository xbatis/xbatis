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

package cn.xbatis.core.tenant;

import java.io.Serializable;
import java.util.List;

public class TenantId implements Serializable {

    private final Serializable[] values;

    public TenantId(Serializable... values) {
        this.values = values;
    }

    public <T extends Serializable> TenantId(List<T> values) {
        if (values != null && !values.isEmpty()) {
            this.values = values.toArray(new Serializable[0]);
        } else {
            this.values = null;
        }
    }

    public Serializable[] getValues() {
        return values;
    }

    public final boolean isMultiValue() {
        return this.values != null && this.values.length > 1;
    }
}
