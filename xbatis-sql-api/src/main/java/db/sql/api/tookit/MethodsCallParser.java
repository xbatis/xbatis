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


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 正确的递归下降解析器
 * 支持：函数嵌套、字符串、数字、标识符
 */
public class MethodsCallParser {
    private String input;
    private int pos;
    private char currentChar;

    public MethodsCallParser() {

    }

    public static void main(String[] args2) {
        MethodsCallParser aa = new MethodsCallParser();

        System.out.println(aa.parse("sum(ifNull(count(name),1))"));
    }

    /**
     * 解析入口
     */
    public MethodCallNode parse(String input) {
        this.input = input;
        this.pos = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
        return (MethodCallNode) parseExpression();
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

        return new MethodCallNode(name, args);
    }
}
