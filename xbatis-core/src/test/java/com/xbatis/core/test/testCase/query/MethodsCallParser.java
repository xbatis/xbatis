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

package com.xbatis.core.test.testCase.query;


import db.sql.api.impl.cmd.Methods;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 正确的递归下降解析器
 * 支持：函数嵌套、字符串、数字、标识符
 */
public class MethodsCallParser {

    private final static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final Function<List<Object>, List<Object>> argsConverter;
    private final Function<String, String> functionNameConverter;
    private final Object target;
    private final boolean isStatic;
    private final Map<String, List<Method>> methodMap;
    private String input;
    private int pos;
    private char currentChar;

    public MethodsCallParser(Object target, boolean isStatic, Function<String, String> functionNameConverter, Function<List<Object>, List<Object>> argsConverter) {
        this.target = target;
        this.isStatic = isStatic;
        this.functionNameConverter = functionNameConverter;
        this.argsConverter = argsConverter;

        Class functionClass = target instanceof Class ? (Class) target : target.getClass();
        this.methodMap = Arrays.stream(functionClass.getMethods())
                .filter(i -> isStatic ? Modifier.isStatic(i.getModifiers()) : true)
                .collect(Collectors.groupingBy(i -> i.getName() + "." + i.getParameterCount()));
    }

    // ========== 使用示例 ==========
    public static void main(String[] args2) {
        MethodsCallParser parser = new MethodsCallParser(Methods.class, true, (name) -> {
            if (name.equals("if")) {
                return "if_";
            }
            return name;
        }, (args) -> {
            return args.stream().map(i -> {
                if (i instanceof String) {
                    String str = (String) i;
                    if (str.startsWith("'")) {
                        return str.substring(1, str.length() - 1);
                    } else {
                        return Methods.column(str);
                    }
                }
                return i;
            }).collect(Collectors.toList());
        });

        // 测试用例
        String[] testCases = {
                "sum(ifNull(id,NULL()))",
                "if(eq(id,NULL()),1,2)"
        };

        System.out.println("========== 基本测试 ==========");
        for (String test : testCases) {
            try {
                Object result = parser.parse(test);
                System.out.printf("%-40s -> %s%n", test, result);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("%-40s -> 错误: %s%n", test, e.getMessage());
            }
        }
    }

    /**
     * 解析入口
     */
    public Object parse(String input) {
        this.input = input;
        this.pos = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
        return parseExpression();
    }

    /**
     * 解析表达式
     */
    private Object parseExpression() {
        skipWhitespace();

        if (pos >= input.length()) {
            return "";
        }

        // 解析最左侧的元素
        Object left = parsePrimary();
        skipWhitespace();

        // 这里可以扩展处理运算符，比如 + - * / 等
        // 目前只处理单个表达式

        return left;
    }

    /**
     * 解析基本元素：函数调用、标识符、数字、字符串
     */
    private Object parsePrimary() {
        skipWhitespace();

        if (pos >= input.length()) {
            return "";
        }

        char c = currentChar;

        // 函数调用或标识符
        if (Character.isLetter(c)) {
            String name = parseIdentifier();
            skipWhitespace();

            // 如果后面跟着 '('，则是函数调用
            if (pos < input.length() && currentChar == '(') {
                return parseFunction(name);
            } else {
                return name; // 普通标识符
            }
        }

        // 数字
        if (Character.isDigit(c) || c == '.') {
            return parseNumber();
        }

        // 字符串 (单引号)
        if (c == '\'') {
            return parseString();
        }

        // 括号表达式
        if (c == '(') {
            advance(); // 跳过 '('
            Object expr = parseExpression();
            skipWhitespace();

            if (pos >= input.length() || currentChar != ')') {
                throw new RuntimeException("缺少右括号，位置: " + pos);
            }
            advance(); // 跳过 ')'
            return "(" + expr + ")";
        }

        throw new RuntimeException("意外的字符: '" + c + "' 在位置 " + pos);
    }

    /**
     * 解析函数调用
     */
    private Object parseFunction(String name) {
        // 跳过 '('
        advance();

        List<Object> args = new ArrayList<>();

        // 解析参数列表
        while (pos < input.length() && currentChar != ')') {
            skipWhitespace();

            // 解析一个参数
            Object arg = parseExpression();
            args.add(arg);

            skipWhitespace();

            // 如果是逗号，继续解析下一个参数
            if (pos < input.length() && currentChar == ',') {
                advance(); // 跳过 ','
                // 继续循环
            } else if (pos < input.length() && currentChar == ')') {
                // 参数结束，跳出循环
                break;
            }
        }

        // 检查右括号
        if (pos >= input.length() || currentChar != ')') {
            throw new RuntimeException("函数 '" + name + "' 缺少右括号");
        }
        advance(); // 跳过 ')'

        final Object[] finalArgs = this.argsConverter.apply(args).toArray(new Object[0]);
        name = functionNameConverter.apply(name);
        Optional<Method> first = methodMap.get(name + "." + finalArgs.length).stream().filter(method -> {
            for (int i = 0; i < finalArgs.length; i++) {
                if (!method.getParameterTypes()[i].isAssignableFrom(finalArgs[i].getClass())) {
                    return false;
                }
            }
            return true;
        }).findFirst();

        try {
            Method method = first.get();
            MethodHandle methodHandle = LOOKUP.unreflect(method);
            if (target instanceof Class) {
                return methodHandle.invokeWithArguments(finalArgs);
            }
            if (Modifier.isStatic(method.getModifiers())) {
                return methodHandle.invokeWithArguments(target.getClass(), finalArgs);
            }
            return methodHandle.invokeWithArguments(finalArgs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析标识符（函数名、字段名）
     */
    private String parseIdentifier() {
        int start = pos;
        while (pos < input.length() &&
                (Character.isLetterOrDigit(currentChar) || currentChar == '_' || currentChar == '.')) {
            advance();
        }
        return input.substring(start, pos);
    }

    /**
     * 解析数字
     */
    private Object parseNumber() {
        int start = pos;
        boolean hasDot = false;

        while (pos < input.length()) {
            char c = currentChar;
            if (Character.isDigit(c)) {
                advance();
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                advance();
            } else {
                break;
            }
        }


        String value = input.substring(start, pos);
        if (value.contains(".")) {
            return new BigDecimal(value);
        }
        return Integer.valueOf(value);
    }

    /**
     * 解析字符串（支持转义）
     */
    private String parseString() {
        int start = pos;
        advance(); // 跳过开头的引号

        while (pos < input.length()) {
            char c = currentChar;
            advance();

            if (c == '\\') {
                // 转义字符，跳过下一个字符
                if (pos < input.length()) {
                    advance();
                }
            } else if (c == '\'') {
                // 字符串结束
                break;
            }
        }

        return input.substring(start, pos);
    }

    /**
     * 跳过空白字符
     */
    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    /**
     * 前进一个字符
     */
    private void advance() {
        pos++;
        currentChar = pos < input.length() ? input.charAt(pos) : '\0';
    }
}
