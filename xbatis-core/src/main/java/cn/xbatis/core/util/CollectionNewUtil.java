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

package cn.xbatis.core.util;

import java.util.*;

public class CollectionNewUtil {

    public static <T> Collection<T> newInstance(Class<?> type) {
        if (LinkedList.class.isAssignableFrom(type)) {
            return new LinkedList<>();
        }
        if (ArrayList.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        if (HashSet.class.isAssignableFrom(type)) {
            return new HashSet<>();
        }
        if (TreeSet.class.isAssignableFrom(type)) {
            return new TreeSet<>();
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        if (Set.class.isAssignableFrom(type)) {
            return new HashSet<>();
        }
        if (Queue.class.isAssignableFrom(type)) {
            return new LinkedList<>();
        }
        throw new RuntimeException("Not support newInstance for " + type);
    }
}
