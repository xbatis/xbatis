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

package cn.xbatis.db;

import java.util.HashMap;
import java.util.Map;

public final class WhenType {
    public final static String EQ = "eq";
    public final static String NE = "ne";
    public final static String GT = "gt";
    public final static String GTE = "gte";
    public final static String LT = "lt";
    public final static String LTE = "lte";
    public final static String IN = "in";
    public final static String NOT_IN = "notIn";
    public final static String BETWEEN = "between";
    public final static String NOT_BETWEEN = "notBetween";

    public static Map<String, String> WHEN_TYPE_MAP = new HashMap<String, String>() {{
        put(EQ, "eq");
        put(NE, "ne");
        put(GT, "gt");
        put(GTE, "gte");
        put(LT, "lt");
        put(LTE, "lte");
        put(IN, "in");
        put(NOT_IN, "notIn");
        put(BETWEEN, "between");
        put(NOT_BETWEEN, "notBetween");
    }};

}
