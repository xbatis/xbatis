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

package cn.xbatis.core.db.reflect;

import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SQLPrinter;
import db.sql.api.tookit.MethodCallNode;
import db.sql.api.tookit.MethodsCallParser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XbatisMethodsCallParser {
    private final static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final Object target;
    private final boolean isStatic;
    private final Map<String, List<Method>> methodMap;
    private final Map<Integer, MethodHandle> methodHandleMap = new HashMap<>();
    private final Function<String, String> nameConverter;


    public XbatisMethodsCallParser(Object target, boolean isStatic, Function<String, String> nameConverter) {
        this.target = target;
        this.isStatic = isStatic;
        this.nameConverter = nameConverter;
        Class functionClass = target instanceof Class ? (Class) target : target.getClass();

        this.methodMap = Arrays.stream(functionClass.getMethods())
                .filter(i -> isStatic ? Modifier.isStatic(i.getModifiers()) : true)
                .collect(Collectors.groupingBy(i -> i.getName() + "." + i.getParameterCount()));

        this.methodMap.putAll(Arrays.stream(functionClass.getMethods())
                .filter(i -> isStatic ? Modifier.isStatic(i.getModifiers()) : true)
                .filter(i -> {
                    if (i.getParameterCount() == 0) {
                        return false;
                    }
                    for (Class type : i.getParameterTypes()) {
                        if (type.isArray()) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.groupingBy(i -> i.getName())));
    }

    public static void main(String[] args) {
        MethodsCallParser methodsCallParser = new MethodsCallParser();
        MethodCallNode methodCallNode = methodsCallParser.parse("sum(count(if(nickname,1,0)))");
        XbatisMethodsCallParser xbatisMethodsCallParser = new XbatisMethodsCallParser(Methods.class, true, name -> {
            if (name.equals("if")) {
                return "if_";
            }
            return name;
        });

        Cmd cmd = (Cmd) xbatisMethodsCallParser.parser(methodCallNode, i -> {
            if (i instanceof String) {
                String str = (String) i;
                if (str.startsWith("'")) {
                    return str.substring(1, str.length() - 1);
                } else {
                    return Methods.column(str);
                }
            }
            return i;
        });

        SQLPrinter.print(DbType.H2, cmd);
    }

    public Object parser(MethodCallNode methodCallNode, Function<Object, Object> argsConverter) {
        return parser(methodCallNode, 0, argsConverter);
    }

    public Object parser(MethodCallNode callNode, int index, Function<Object, Object> argsConverter) {
        if (callNode == null) {
            return null;
        }

        List<Object> args = callNode.getArgs().stream().map(i -> {
            int nextIndex = index + 1;
            if (i instanceof MethodCallNode) {
                return parser((MethodCallNode) i, nextIndex, argsConverter);
            }
            return argsConverter.apply(i);
        }).collect(Collectors.toList());

        return invoke(callNode, args, index);
    }

    private Object invoke(MethodCallNode callNode, List<Object> args, int index) {
        MethodHandle methodHandle = methodHandleMap.get(index);
        if (methodHandle == null) {
            String name = callNode.getFunName();
            if (nameConverter != null) {
                name = nameConverter.apply(name);
            }

            List<Method> methods = methodMap.get(name + "." + args.size());
            Optional<Method> first = null;
            if (methods != null) {
                first = methods.stream().filter(method -> {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length < args.size()) {
                        return false;
                    }

                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        if (!parameterType.isAssignableFrom(args.get(i).getClass())) {
                            return false;
                        }
                    }
                    return true;
                }).findFirst();
            }


            if (first == null || !first.isPresent()) {
                methods = methodMap.get(name);
                if (methods == null) {
                    throw new RuntimeException("can't find method:" + name + " in " + (target instanceof Class ? target : target.getClass()));
                }
                first = methods.stream().filter(method -> {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        if (parameterType.isArray()) {
                            return true;
                        }
                        if (!parameterType.isAssignableFrom(args.get(i).getClass())) {
                            return false;
                        }
                    }
                    return true;
                }).findFirst();
            }

            if (first == null || !first.isPresent()) {
                throw new RuntimeException("can't find method:" + name + " in " + (target instanceof Class ? target : target.getClass()));
            }
            Method method = first.get();
            try {
                methodHandle = LOOKUP.unreflect(method);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            methodHandleMap.put(index, methodHandle);
        }


        try {
            return methodHandle.invokeWithArguments(args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
