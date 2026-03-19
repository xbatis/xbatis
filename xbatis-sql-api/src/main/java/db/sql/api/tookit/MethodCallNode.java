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

package db.sql.api.tookit;

import java.lang.reflect.Method;
import java.util.List;

public class MethodCallNode {
    private String funName;

    private List<Object> args;

    private Method method;

    public MethodCallNode(String funName, List<Object> args) {
        this.funName = funName;
        this.args = args;
    }

    public MethodCallNode(String funName, List<Object> args, MethodCallNode next) {
        this(funName, args);
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "MethodCallNode{" +
                "funName='" + funName + '\'' +
                ", args=" + args +
                '}';
    }
}
