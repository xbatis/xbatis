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

package cn.xbatis.core.mybatis.executor.resultset;

import cn.xbatis.core.db.reflect.FetchInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FetchEntityObject extends FetchObject {

    private final List<FetchInfo> fetchInfos;

    public FetchEntityObject(Object matchValue, Object value, List<FetchInfo> fetchInfos, String cacheKey) {
        super(matchValue, value, cacheKey);
        this.fetchInfos = fetchInfos;
    }

    public List<FetchInfo> getFetchInfos() {
        return fetchInfos;
    }
}
