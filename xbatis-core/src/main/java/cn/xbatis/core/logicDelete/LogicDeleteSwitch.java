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

package cn.xbatis.core.logicDelete;

/**
 * 使用方式：
 * <pre>
 * try (LogicDeleteSwitch ignored = LogicDeleteSwitch.with(false)) {
 *    logicDeleteTestMapper.getById(1);
 * }
 * </pre>
 */
public final class LogicDeleteSwitch implements AutoCloseable {

    private final static ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<>();

    private LogicDeleteSwitch() {

    }

    /**
     * 获得开关状态
     *
     * @return 状态
     */
    public static Boolean getState() {
        return THREAD_LOCAL.get();
    }

    /**
     * 设置开关
     *
     * @param state 状态
     * @return LogicDeleteSwitch
     */
    public static LogicDeleteSwitch with(boolean state) {
        LogicDeleteSwitch logicDeleteSwitch = new LogicDeleteSwitch();
        THREAD_LOCAL.set(state);
        return logicDeleteSwitch;
    }

    /**
     * 清理临时状态
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }

    @Override
    public void close() {
        clear();
    }
}
