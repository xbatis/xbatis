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


import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.cmd.NoAfterDelimiter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class CmdUtils {

    public static StringBuilder join(SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList) {
        return join(null, null, context, builder, cmdList);
    }

    public static StringBuilder join(Cmd module, Cmd user, SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList) {
        return join(module, user, context, builder, cmdList, (Consumer) null);
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList, char delimiter) {
        return join(module, parent, context, builder, cmdList, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList, char[] delimiter) {
        return join(module, parent, context, builder, cmdList, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList, String delimiter) {
        return join(module, parent, context, builder, cmdList, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Collection<? extends Cmd> cmdList, Consumer<StringBuilder> delimiter) {
        if (cmdList == null || cmdList.isEmpty()) {
            return builder;
        }
        Iterator<? extends Cmd> iterator = cmdList.iterator();

        while (true) {
            Cmd cmd = iterator.next();
            builder = cmd.sql(module, parent, context, builder);
            if (!iterator.hasNext()) {
                break;
            }

            if (delimiter != null) {
                if (!(cmd instanceof NoAfterDelimiter)) {
                    if (delimiter != null) {
                        delimiter.accept(builder);
                    }
                }
            }
        }
        return builder;
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Cmd[] cmds, char delimiter) {
        return join(module, parent, context, builder, cmds, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Cmd[] cmds, char[] delimiter) {
        return join(module, parent, context, builder, cmds, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Cmd[] cmds, String delimiter) {
        return join(module, parent, context, builder, cmds, s -> s.append(delimiter));
    }

    public static StringBuilder join(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder builder, Cmd[] cmds, Consumer<StringBuilder> delimiter) {
        if (cmds == null || cmds.length < 1) {
            return builder;
        }
        int length = cmds.length;
        for (int i = 0; i < length; i++) {
            if (i != 0 && delimiter != null) {
                if (!(cmds[i - 1] instanceof NoAfterDelimiter)) {
                    if (delimiter != null) {
                        delimiter.accept(builder);
                    }
                }
            }
            builder = cmds[i].sql(module, parent, context, builder);
        }
        return builder;
    }

    public static StringBuilder join(StringBuilder builder, String[] strs, char delimiter) {
        if (strs == null || strs.length < 1) {
            return builder;
        }
        int length = strs.length;
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                builder.append(delimiter);
            }
            builder.append(strs[i]);
        }
        return builder;
    }

    @SafeVarargs
    public static boolean contain(Cmd cmd, Object... params) {
        if (Objects.isNull(params)) {
            return false;
        }
        for (Object param : params) {
            if (contain(cmd, param)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contain(Cmd cmd, Object another) {
        if (Objects.isNull(another)) {
            return false;
        }
        if (cmd == another) {
            return true;
        }
        if (another instanceof Cmd) {
            return contain(cmd, (Cmd) another);
        } else if (another instanceof List) {
            return contain(cmd, (List) another);
        } else if (another instanceof Object[]) {
            return contain(cmd, (Object[]) another);
        }
        return false;
    }

    public static boolean contain(Cmd cmd, Cmd another) {
        if (Objects.isNull(another)) {
            return false;
        }
        if (cmd == another) {
            return true;
        }
        return another.contain(cmd);
    }

    public static boolean contain(Cmd cmd, List<?> another) {
        if (Objects.isNull(another)) {
            return false;
        }
        for (Object param : another) {
            if (contain(cmd, param)) {
                return true;
            }
        }

        return false;
    }
}
