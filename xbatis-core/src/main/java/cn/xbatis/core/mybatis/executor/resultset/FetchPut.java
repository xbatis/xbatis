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
import lombok.Getter;

import java.util.*;

@Getter
public class FetchPut {

    private Object rowValue;

    private String cacheKey;

    private Object matchValue;

    private FetchInfo fetchInfo;

    private List<Object> values=new ArrayList<>();

    private Set<Object> matchSet;

    public FetchPut(Object rowValue,Object matchValue, FetchInfo fetchInfo, String cacheKey) {
        this.rowValue = rowValue;
        this.matchValue = matchValue;
        this.fetchInfo = fetchInfo;
        this.cacheKey = cacheKey;

        if (this.matchValue instanceof Collection){
            Collection collection = (Collection) this.matchValue;
            this.matchSet = new HashSet<>();
            this.matchSet.addAll(collection);
            for(Object o : collection){
                this.values.add(null);
            }
        } else {
            this.values = new ArrayList<>();
        }
    }

    public boolean putValue(Object onValue,Object fetchQueryValue) {
        if (onValue == null){
            return false;
        }
        boolean match;
        if (this.matchSet != null){
            match = this.matchSet.contains(onValue);
            if (match) {
                int i = -1;
                for (Object o:(Collection) this.matchValue){
                    i++;
                    if (Objects.equals(o, onValue)) {
                        this.values.set(i, fetchQueryValue);
                    }
                }
            }
        } else {
            match= Objects.equals(this.matchValue, onValue.toString());
            if (match){
                this.values.add(fetchQueryValue);
            }
        }
        return match;
    }

    public List<Object> getValues() {
        return values;
    }


    public String getCacheKey() {
        return cacheKey;
    }
}
