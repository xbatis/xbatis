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

package db.sql.api.impl.cmd;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.CmdConvert;
import db.sql.api.cmd.GetterField;
import db.sql.api.cmd.GetterFields;
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.basic.IParamWrap;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.impl.cmd.basic.*;
import db.sql.api.impl.cmd.condition.*;
import db.sql.api.impl.cmd.dbFun.*;
import db.sql.api.impl.cmd.dbFun.mysql.*;
import db.sql.api.impl.cmd.dbFun.postgres.DistinctOn;
import db.sql.api.impl.cmd.dbFun.postgres.PostgresFunctions;
import db.sql.api.impl.cmd.postgis.ST_Contains;
import db.sql.api.impl.cmd.postgis.ST_DWithin;
import db.sql.api.impl.cmd.postgis.ST_Distance;
import db.sql.api.impl.cmd.postgis.ST_Point;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 数据库方法集合
 */
public final class Methods {

    /**
     * 对应SQL中的NULL
     *
     * @return
     */
    public static NULL NULL() {
        return NULL.NULL;
    }

    /**
     * 对应SQL中的true;创建一个始终TRUE条件
     * 方便开发者使用
     *
     * @return
     */
    public static TRUE TRUE() {
        return TRUE.TRUE;
    }

    /**
     * 对应SQL中的true;创建一个始终TRUE条件
     * 方便开发者使用
     *
     * @return
     */
    public static FALSE FALSE() {
        return FALSE.FALSE;
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param column   列
     * @param point    另外一个对象
     * @param distance 距离
     * @return
     */
    public static ST_DWithin ST_DWithin(Cmd column, Cmd point, double distance) {
        return ST_DWithin(column, point, distance, null);
    }

    /**
     * 创建普通sql模板
     *
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static CmdTemplate tpl(String template, Object... params) {
        return CmdTemplate.create(template, params);
    }

    /**
     * 创建普通sql模板
     *
     * @param wrapping 会对单引号进行包裹，因为使用的是MessageFormat,单引号是特殊字符；开启后无需关注单引号问题
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static CmdTemplate tpl(boolean wrapping, String template, Object... params) {
        return CmdTemplate.create(wrapping, template, params);
    }

    /**
     * 创建函数sql模板
     *
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static FunTemplate fTpl(String template, Object... params) {
        return FunTemplate.create(template, params);
    }

    /**
     * 创建函数sql模板
     *
     * @param wrapping 会对单引号进行包裹，因为使用的是MessageFormat,单引号是特殊字符；开启后无需关注单引号问题
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static FunTemplate fTpl(boolean wrapping, String template, Object... params) {
        return FunTemplate.create(wrapping, template, params);
    }

    /**
     * 创建条件sql模板
     *
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static ConditionTemplate cTpl(String template, Object... params) {
        return ConditionTemplate.create(template, params);
    }

    /**
     * 创建条件sql模板
     *
     * @param wrapping 会对单引号进行包裹，因为使用的是MessageFormat,单引号是特殊字符；开启后无需关注单引号问题
     * @param template 模板
     * @param params   参数
     * @return
     */
    @SafeVarargs
    public static ConditionTemplate cTpl(boolean wrapping, String template, Object... params) {
        return ConditionTemplate.create(wrapping, template, params);
    }

    /**
     * 参数包装并转成CMD对象
     *
     * @param userType 使用者的类型
     * @param column   列
     * @param param    参数
     * @return Cmd
     */
    public static Cmd paramWrapAndConvertToCmd(Class userType, Cmd column, Object param) {
        if (java.util.Objects.isNull(param)) {
            return null;
        }
        if (param instanceof Cmd) {
            return (Cmd) param;
        }

        if (!(column instanceof IParamWrap)) {
            return new BasicValue(param);
        }

        IParamWrap paramWrap = (IParamWrap) column;
        return new BasicValue(paramWrap.paramWrap(userType, param));
    }

    /**
     * LIKE 参数包装
     *
     * @param column    列
     * @param param     参数
     * @param mode      like方式
     * @param isNotLike 是否为NOT LIKE
     * @return param包装后的值
     */
    public static Object likeParamWrap(Cmd column, Object param, LikeMode mode, boolean isNotLike) {
        if (java.util.Objects.isNull(param)) {
            return null;
        }
        if (param instanceof Cmd) {
            return param;
        }

        if (!(column instanceof IParamWrap)) {
            return param;
        }
        IParamWrap paramWrap = (IParamWrap) column;
        return paramWrap.likeParamWrap(mode, param, isNotLike);
    }

    /**
     * 将指定列名转成 Column
     *
     * @param column 列名
     * @return Column
     */
    public static Column column(String column) {
        Objects.requireNonNull(column);
        return new Column(column);
    }

    /**
     * 将指定列名转成 Column
     *
     * @param dataset 表/子查询
     * @param column  列名
     * @return Column
     */
    public static Column column(IDataset dataset, String column) {
        Objects.requireNonNull(column);
        return new Column(dataset, column);
    }

    /**
     * 将指定列名转成 Column
     *
     * @param column getter列名,指的是你as(XX.getXXX)后的列名
     * @return Column
     */
    public static <T> Column column(Getter<T> column) {
        return new Column(SqlUtil.getAsName(column));
    }

    /**
     * 将value转成Cmd对象；普通值将转成为BasicValue
     *
     * @param value 值
     * @return Cmd
     */
    public static Cmd cmd(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Cmd) {
            return (Cmd) value;
        } else if (value instanceof CmdConvert) {
            return ((CmdConvert) value).convert();
        }
        return new BasicValue(value);
    }

    /**
     * 将普通值转换为BasicValue
     *
     * @param value 值
     * @return BasicValue
     */
    public static BasicValue value(Serializable value) {
        Objects.requireNonNull(value);
        if (value instanceof CmdConvert) {
            throw new RuntimeException("please use Methods.cmd instead");
        }
        return new BasicValue(value);
    }

    /**
     * plus加法
     *
     * @param column 列
     * @param value  值
     * @return Plus
     */
    public static Plus plus(Cmd column, Number value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Plus(column, value);
    }

    /**
     * plus加法
     *
     * @param column 列
     * @param value  值
     * @return Plus
     */
    public static Plus plus(Cmd column, Cmd value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Plus(column, value);
    }

    /**
     * subtract加法
     *
     * @param column 列
     * @param value  值
     * @return Subtract
     */
    public static Subtract subtract(Cmd column, Number value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Subtract(column, value);
    }

    /**
     * subtract减法
     *
     * @param column 列
     * @param value  值
     * @return Subtract
     */
    public static Subtract subtract(Cmd column, Cmd value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Subtract(column, value);
    }

    /**
     * multiply乘法
     *
     * @param column 列
     * @param value  值
     * @return Multiply
     */
    public static Multiply multiply(Cmd column, Number value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Multiply(column, value);
    }

    /**
     * divide除法
     *
     * @param column 列
     * @param value  值
     * @return Divide
     */
    public static Divide divide(Cmd column, Cmd value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Divide(column, value);
    }

    /**
     * multiply乘法
     *
     * @param column 列
     * @param value  值
     * @return Divide
     */
    public static Divide divide(Cmd column, Number value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Divide(column, value);
    }

    /**
     * multiply乘法
     *
     * @param column 列
     * @param value  值
     * @return Multiply
     */
    public static Multiply multiply(Cmd column, Cmd value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Multiply(column, value);
    }

    /**
     * count条数 函数
     *
     * @param column 列
     * @return Count
     */
    public static Count count(Cmd column) {
        Objects.requireNonNull(column);
        return new Count(column);
    }

    /**
     * count(1) 条数 函数
     *
     * @return Count1
     */
    public static Count1 count1() {
        return new Count1();
    }

    /**
     * count(*) 条数 函数
     *
     * @return CountAll
     */
    public static CountAll countAll() {
        return new CountAll();
    }

    /**
     * count条数 函数
     *
     * @param column   列
     * @param distinct 是否去重
     * @return Count
     */
    public static Count count(Cmd column, boolean distinct) {
        Objects.requireNonNull(column);
        return new Count(column, distinct);
    }

    /**
     * sum求和 函数
     *
     * @param column 列
     * @return Sum
     */
    public static Sum sum(Cmd column) {
        Objects.requireNonNull(column);
        return new Sum(column);
    }

    /**
     * min最小 函数
     *
     * @param column 列
     * @return Min
     */
    public static Min min(Cmd column) {
        Objects.requireNonNull(column);
        return new Min(column);
    }

    /**
     * max最大 函数
     *
     * @param column 列
     * @return Max
     */
    public static Max max(Cmd column) {
        Objects.requireNonNull(column);
        return new Max(column);
    }

    /**
     * avg平局值 函数
     *
     * @param column 列
     * @return Avg
     */
    public static Avg avg(Cmd column) {
        Objects.requireNonNull(column);
        return new Avg(column);
    }

    /**
     * abs绝对值 函数
     *
     * @param column 列
     * @return Abs
     */
    public static Abs abs(Cmd column) {
        Objects.requireNonNull(column);
        return new Abs(column);
    }

    /**
     * pow平方 函数
     *
     * @param column 列
     * @param n      次数
     * @return Pow
     */
    public static Pow pow(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new Pow(column, n);
    }

    /**
     * round四舍五入 取整数位 函数
     *
     * @param column 列
     * @return
     */
    public static Round round(Cmd column) {
        return round(column, 0);
    }

    /**
     * round四舍五入 函数
     *
     * @param column    列
     * @param precision 精度
     * @return
     */
    public static Round round(Cmd column, int precision) {
        Objects.requireNonNull(column);
        return new Round(column, precision);
    }

    /**
     * ceil返回大于或等于 x 的最小整数（向上取整） 函数
     *
     * @param column 列
     * @return
     */
    public static Ceil ceil(Cmd column) {
        Objects.requireNonNull(column);
        return new Ceil(column);
    }

    /**
     * floor返回小于或等于 x 的最大整数（向下取整） 函数
     *
     * @param column 列
     * @return
     */
    public static Floor floor(Cmd column) {
        Objects.requireNonNull(column);
        return new Floor(column);
    }

    /**
     * 0~1 的随机数 函数
     *
     * @return Rand
     */
    public static Rand rand() {
        return new Rand();
    }

    /**
     * 0~1 的随机数 函数
     *
     * @param n 随机因子
     * @return Rand
     */
    public static Rand rand(Number n) {
        return new Rand(n);
    }

    /**
     * 获取 column的符号，负数为-1 0为0、正数为1
     *
     * @param column 列
     * @return Sign
     */
    public static Sign sign(Cmd column) {
        Objects.requireNonNull(column);
        return new Sign(column);
    }

    /**
     * pi 返回圆周率 函数
     *
     * @return Pi
     */
    public static Pi pi() {
        return new Pi();
    }

    /**
     * 返回数值 column 整数位 函数
     *
     * @param column 列
     * @return Truncate
     */
    public static Truncate truncate(Cmd column) {
        Objects.requireNonNull(column);
        return truncate(column, 0);
    }

    /**
     * 返回数值 column 保留到小数点后 precision 位的值 函数
     *
     * @param column    列
     * @param precision 精度
     * @return Truncate
     */
    public static Truncate truncate(Cmd column, int precision) {
        Objects.requireNonNull(column);
        return new Truncate(column, precision);
    }

    /**
     * sqrt 平方根 函数
     *
     * @param column 列
     * @return Sqrt
     */
    public static Sqrt sqrt(Cmd column) {
        Objects.requireNonNull(column);
        return new Sqrt(column);
    }

    /**
     * mod 取模 函数
     *
     * @param column 列
     * @return Mod
     */
    public static Mod mod(Cmd column, Number divisor) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(divisor);
        return new Mod(column, divisor);
    }

    /**
     * 返回 e 的 n 次方 函数
     *
     * @param n 次方
     * @return Exp
     */
    public static Exp exp(int n) {
        return new Exp(n);
    }

    /**
     * 以base为底，求number自然对数
     *
     * @param base   底数
     * @param number 对数
     * @return Log
     */
    public static Log log(Cmd base, int number) {
        Objects.requireNonNull(base);
        return new Log(base, number);
    }

    /**
     * 将弧度转换为角度 函数
     *
     * @param column 列
     * @return Degrees
     */
    public static Degrees degrees(Cmd column) {
        Objects.requireNonNull(column);
        return new Degrees(column);
    }

    /**
     * 将角度转换为弧度 函数
     *
     * @param column 列
     * @return Radians
     */
    public static Radians radians(Cmd column) {
        Objects.requireNonNull(column);
        return new Radians(column);
    }

    /**
     * 求正弦值 函数
     *
     * @param column 列
     * @return Sin
     */
    public static Sin sin(Cmd column) {
        Objects.requireNonNull(column);
        return new Sin(column);
    }

    /**
     * 求反正弦值 函数
     *
     * @param column 列
     * @return Asin
     */
    public static Asin asin(Cmd column) {
        Objects.requireNonNull(column);
        return new Asin(column);
    }

    /**
     * 求余弦值 函数
     *
     * @param column 列
     * @return Cos
     */
    public static Cos cos(Cmd column) {
        Objects.requireNonNull(column);
        return new Cos(column);
    }

    /**
     * 求反余弦值 函数
     *
     * @param column 列
     * @return Acos
     */
    public static Acos acos(Cmd column) {
        Objects.requireNonNull(column);
        return new Acos(column);
    }

    /**
     * 求正切值 函数
     *
     * @param column 列
     * @return Tan
     */
    public static Tan tan(Cmd column) {
        Objects.requireNonNull(column);
        return new Tan(column);
    }

    /**
     * 求反正切值 函数
     *
     * @param column 列
     * @return Atan
     */
    public static Atan atan(Cmd column) {
        Objects.requireNonNull(column);
        return new Atan(column);
    }

    /**
     * 求余切值 函数
     *
     * @param column 列
     * @return Cot
     */
    public static Cot cot(Cmd column) {
        Objects.requireNonNull(column);
        return new Cot(column);
    }

    /**
     * 返回字符串的字符数
     *
     * @param column 列
     * @return CharLength
     */
    public static CharLength charLength(Cmd column) {
        Objects.requireNonNull(column);
        return new CharLength(column);
    }

    /**
     * 返回字符串的长度 函数
     *
     * @param column 列
     * @return Length
     */
    public static Length length(Cmd column) {
        Objects.requireNonNull(column);
        return new Length(column);
    }

    /**
     * 转换成大写 函数
     *
     * @param column 列
     * @return Upper
     */
    public static Upper upper(Cmd column) {
        Objects.requireNonNull(column);
        return new Upper(column);
    }

    /**
     * 转换成小写 函数
     *
     * @param column 列
     * @return Lower
     */
    public static Lower lower(Cmd column) {
        Objects.requireNonNull(column);
        return new Lower(column);
    }

    /**
     * 左边截取
     *
     * @param column 列
     * @return Left
     */
    public static Left left(Cmd column, int length) {
        Objects.requireNonNull(column);
        return new Left(column, length);
    }

    /**
     * 右边截取
     *
     * @param column 列
     * @return Right
     */
    public static Right right(Cmd column, int start) {
        Objects.requireNonNull(column);
        return new Right(column, start);
    }

    /**
     * 字符截取
     *
     * @param column 列
     * @param start  开始位置
     * @return SubStr
     */
    public static SubStr subStr(Cmd column, int start) {
        Objects.requireNonNull(column);
        return new SubStr(column, start);
    }

    /**
     * 字符截取
     *
     * @param column 列
     * @param start  开始位置
     * @param length 长度
     * @return SubStr
     */
    public static SubStr subStr(Cmd column, int start, int length) {
        Objects.requireNonNull(column);
        return new SubStr(column, start, length);
    }

    /**
     * 从左边开始填充
     *
     * @param column 列
     * @param length 填充后的长度
     * @param pad    填充内容
     * @return Lpad
     */
    public static Lpad lpad(Cmd column, int length, String pad) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(pad);
        return new Lpad(column, length, pad);
    }

    /**
     * 从左边开始填充
     *
     * @param column 列
     * @param length 填充后的长度
     * @param pad    填充内容
     * @return Rpad
     */
    public static Rpad rpad(Cmd column, int length, String pad) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(pad);
        return new Rpad(column, length, pad);
    }

    /**
     * 删除两边空格
     *
     * @param column 列
     * @return Trim
     */
    public static Trim trim(Cmd column) {
        Objects.requireNonNull(column);
        return new Trim(column);
    }

    /**
     * 删除左边空格
     *
     * @param column 列
     * @return Ltrim
     */
    public static Ltrim ltrim(Cmd column) {
        Objects.requireNonNull(column);
        return new Ltrim(column);
    }

    /**
     * 删除右边空格
     *
     * @param column 列
     * @return Rtrim
     */
    public static Rtrim rtrim(Cmd column) {
        Objects.requireNonNull(column);
        return new Rtrim(column);
    }

    /**
     * 字符串比较 函数
     * 返回 -1 0 1
     *
     * @param column 列
     * @param str    比较对象
     * @return Strcmp
     */
    public static Strcmp strcmp(Cmd column, String str) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(str);
        return new Strcmp(column, str);
    }

    /**
     * 将字符串  重复 n 次
     *
     * @param column 列
     * @param n      次数
     * @return Repeat
     */
    public static Repeat repeat(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new Repeat(column, n);
    }

    /**
     * 替换 函数
     *
     * @param column      列
     * @param target      匹配目标
     * @param replacement 替换值
     * @return Replace
     */
    public static Replace replace(Cmd column, String target, String replacement) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(target);
        Objects.requireNonEmpty(replacement);
        return new Replace(column, target, replacement);
    }

    /**
     * 反转函数
     *
     * @param column 列
     * @return Reverse
     */
    public static Reverse reverse(Cmd column) {
        Objects.requireNonNull(column);
        return new Reverse(column);
    }

    /**
     * 匹配 match 在 key里边的位置
     * column 需要符合逗号分割规范
     *
     * @param column 列
     * @param match  匹配值
     * @return FindInSet
     */
    public static FindInSet mysqlFindInSet(Cmd column, String match) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(match);
        return new FindInSet(column, match);
    }

    /**
     * 匹配 match 在 key里边的位置
     * column 需要符合逗号分割规范
     *
     * @param column 列
     * @param match  匹配值
     * @return FindInSet
     */
    public static FindInSet mysqlFindInSet(Cmd column, Number match) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(match);
        return new FindInSet(column, match + "");
    }

    /**
     * 匹配key 在values里的位置 从1 开始
     *
     * @param column 列
     * @param values 指定顺利的数据（一组数据）
     * @return Field
     */
    @SafeVarargs
    public static Field mysqlFiled(Cmd column, Object... values) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(values);
        return new Field(column, values);
    }

    /**
     * 当前日期
     *
     * @return CurrentDate
     */
    public static CurrentDate currentDate() {
        return new CurrentDate();
    }

    /**
     * 当前时间（不包含日期）
     *
     * @return CurrentTime
     */
    public static CurrentTime currentTime() {
        return new CurrentTime();
    }

    /**
     * 当前时间（包含日期、时分秒）
     *
     * @return CurrentDateTime
     */
    public static CurrentDateTime currentDateTime() {
        return new CurrentDateTime();
    }

    /**
     * 获取年份
     *
     * @param column 列
     * @return Year
     */
    public static Year year(Cmd column) {
        Objects.requireNonNull(column);
        return new Year(column);
    }

    /**
     * 获取月份
     *
     * @param column 列
     * @return Month
     */
    public static Month month(Cmd column) {
        Objects.requireNonNull(column);
        return new Month(column);
    }

    /**
     * 获取日期部分，不包含时分秒
     *
     * @param column 列
     * @return DateFormat
     */
    public static DateFormat date(Cmd column) {
        Objects.requireNonNull(column);
        return new DateFormat(column, DatePattern.YYYY_MM_DD);
    }

    /**
     * 格式化时间
     *
     * @param column  列
     * @param pattern 格式
     * @return DateFormat
     */
    public static DateFormat dateFormat(Cmd column, String pattern) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(pattern);
        return new DateFormat(column, pattern);
    }

    /**
     * 格式化时间
     *
     * @param column  列
     * @param pattern 格式
     * @return DateFormat
     */
    public static DateFormat dateFormat(Cmd column, DatePattern pattern) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(pattern);
        return new DateFormat(column, pattern);
    }

    /**
     * 获取column的第几天
     *
     * @param column 列
     * @return Day
     */
    public static Day day(Cmd column) {
        Objects.requireNonNull(column);
        return new Day(column);
    }

    /**
     * 获取column的星期几
     *
     * @param column 列
     * @return Weekday
     */
    public static Weekday weekday(Cmd column) {
        Objects.requireNonNull(column);
        return new Weekday(column);
    }

    /**
     * 获取column的小时部分
     *
     * @param column 列
     * @return Hour
     */
    public static Hour hour(Cmd column) {
        Objects.requireNonNull(column);
        return new Hour(column);
    }

    /**
     * 日期比较
     *
     * @param column 列
     * @return DateDiff
     */
    public static DateDiff dateDiff(Cmd column, Cmd another) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(another);
        return new DateDiff(column, another);
    }

    /**
     * 日期增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd dateAdd(Cmd column, int n, TimeUnits timeUnit) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, timeUnit);
    }

    /**
     * 日期增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd dateAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.DAYS);
    }

    /**
     * 日期减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd dateSub(Cmd column, int n, TimeUnits timeUnit) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n * -1, timeUnit);
    }

    /**
     * 日期减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd dateSub(Cmd column, int n) {
        return dateSub(column, n);
    }

    /**
     * 秒增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd secondAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.SECONDS);
    }

    /**
     * 秒减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd secondSub(Cmd column, int n) {
        return secondAdd(column, n * -1);
    }

    /**
     * 分增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd minuteAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.MINUTES);
    }

    /**
     * 分减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd minuteSub(Cmd column, int n) {
        return minuteAdd(column, n * -1);
    }

    /**
     * 小时增
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd hourAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.HOURS);
    }

    /**
     * 小时减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd hourSub(Cmd column, int n) {
        return hourAdd(column, n * -1);
    }

    /**
     * 月增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd monthAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.MONTHS);
    }

    /**
     * 月减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd monthSub(Cmd column, int n) {
        return monthAdd(column, n * -1);
    }

    /**
     * 年增加
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd yearAdd(Cmd column, int n) {
        Objects.requireNonNull(column);
        return new DateAdd(column, n, TimeUnits.YEARS);
    }

    /**
     * 年减少
     *
     * @param column 列
     * @return DateAdd
     */
    public static DateAdd yearSub(Cmd column, int n) {
        return yearAdd(column, n * -1);
    }

    /**
     * md5
     *
     * @param str
     * @return Md5
     */
    public static Md5 mysqlMd5(String str) {
        Objects.requireNonEmpty(str);
        return new Md5(str);
    }

    /**
     * md5
     *
     * @param column 列
     * @return Md5
     */
    public static Md5 mysqlMd5(Cmd column) {
        Objects.requireNonNull(column);
        return new Md5(column);
    }

    /**
     * 将ip转成数字
     *
     * @param ip
     * @return InetAton
     */
    public static InetAton inetAton(String ip) {
        Objects.requireNonEmpty(ip);
        return new InetAton(ip);
    }

    /**
     * 将ip转成数字
     *
     * @param column 列
     * @return InetAton
     */
    public static InetAton inetAton(Cmd column) {
        Objects.requireNonNull(column);
        return new InetAton(column);
    }

    /**
     * 将ip数字转成ip
     *
     * @param ipNumber
     * @return InetNtoa
     */
    public static InetNtoa inetNtoa(Number ipNumber) {
        Objects.requireNonNull(ipNumber);
        return new InetNtoa(ipNumber);
    }

    /**
     * 将ip数字转成ip
     *
     * @param column 列
     * @return InetNtoa
     */
    public static InetNtoa inetNtoa(Cmd column) {
        Objects.requireNonNull(column);
        return new InetNtoa(column);
    }

    /**
     * concat拼接 函数
     *
     * @param column 列
     * @param values 需要拼接的值
     * @return Concat
     */
    @SafeVarargs
    public static Concat concat(Cmd column, Object... values) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(values);
        return new Concat(column, values);
    }

    /**
     * concatWs拼接 函数
     *
     * @param column 列
     * @param split  分隔符
     * @param values 需要拼接的值
     * @return ConcatWs
     */
    @SafeVarargs
    public static ConcatWs concatWs(Cmd column, String split, Object... values) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(split);
        Objects.requireNonNull(values);
        return new ConcatWs(column, split, values);
    }

    /* --------------------------------------以下为判断条件----------------------------------------------*/

    /**
     * if(条件,值1,值2) 函数
     *
     * @param condition 条件
     * @param value     condition符合时的值
     * @param thenValue condition不符合时的值
     * @return If
     */
    public static If if_(Condition condition, Serializable value, Serializable thenValue) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(thenValue);
        Objects.requireNonNull(condition);
        return new If(condition, value, thenValue);
    }

    /**
     * if(条件,值1,值2) 函数
     *
     * @param condition 条件
     * @param value     condition符合时的值
     * @param thenValue condition不符合时的值
     * @return If
     */
    public static If if_(Condition condition, Cmd value, Serializable thenValue) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(thenValue);
        Objects.requireNonNull(condition);
        return new If(condition, value, thenValue);
    }

    /**
     * if(条件,值1,值2) 函数
     *
     * @param condition 条件
     * @param value     condition符合时的值
     * @param thenValue condition不符合时的值
     * @return If
     */
    public static If if_(Condition condition, Serializable value, Cmd thenValue) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(thenValue);
        Objects.requireNonNull(condition);
        return new If(condition, value, thenValue);
    }

    /**
     * IF(条件,值1,值2) 函数
     *
     * @param condition 条件
     * @param value     condition符合时的值
     * @param thenValue condition不符合时的值
     * @return If
     */
    public static If if_(Condition condition, Cmd value, Cmd thenValue) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(thenValue);
        Objects.requireNonNull(condition);
        return new If(condition, value, thenValue);
    }

    /**
     * IFNULL(条件,值1,值2) 函数
     *
     * @param column 列
     * @param value  值
     * @return IfNull
     */
    public static IfNull ifNull(Cmd column, Cmd value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new IfNull(column, value);
    }

    /**
     * IFNULL(条件,值1,值2) 函数
     *
     * @param column 列
     * @param value  值
     * @return IfNull
     */
    public static IfNull ifNull(Cmd column, Serializable value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new IfNull(column, value);
    }

    /**
     * column 列 is NOT NULL
     *
     * @param column 列
     * @return IsNull
     */
    public static IsNull isNull(Cmd column) {
        Objects.requireNonNull(column);
        return new IsNull(column);
    }

    /**
     * column 列 is NOT NULL
     *
     * @param column 列
     * @return IsNotNull
     */
    public static IsNotNull isNotNull(Cmd column) {
        Objects.requireNonNull(column);
        return new IsNotNull(column);
    }

    /**
     * case 语句块
     *
     * @return Case
     */
    public static Case case_() {
        return new Case();
    }

    /**
     * eq等于 判断
     *
     * @param column 列
     * @return Eq
     */
    public static Eq eq(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Eq(column, paramWrapAndConvertToCmd(Eq.class, column, value));
    }

    /**
     * ne不等于 判断
     *
     * @param column 列
     * @return Ne
     */
    public static Ne ne(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Ne(column, paramWrapAndConvertToCmd(Ne.class, column, value));
    }

    /**
     * 不为空 判断
     *
     * @param column 列
     * @return NotEmpty
     */
    public static NotEmpty notEmpty(Cmd column) {
        Objects.requireNonNull(column);
        return new NotEmpty(column);
    }

    /**
     * 为空 判断
     *
     * @param column 列
     * @return Empty
     */
    public static Empty empty(Cmd column) {
        Objects.requireNonNull(column);
        return new Empty(column);
    }

    /**
     * gt大于 判断
     *
     * @param column 列
     * @param value  比较对象
     * @return Gt
     */
    public static Gt gt(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Gt(column, paramWrapAndConvertToCmd(Gt.class, column, value));
    }

    /**
     * gte大于等于 判断
     *
     * @param column 列
     * @param value  比较对象
     * @return Gte
     */
    public static Gte gte(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Gte(column, paramWrapAndConvertToCmd(Gte.class, column, value));
    }

    /**
     * gt小于 判断
     *
     * @param column 列
     * @param value  比较对象
     * @return Lt
     */
    public static Lt lt(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Lt(column, paramWrapAndConvertToCmd(Lt.class, column, value));
    }

    /**
     * gt小于等于 判断
     *
     * @param column 列
     * @param value  比较对象
     * @return Lte
     */
    public static Lte lte(Cmd column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        return new Lte(column, paramWrapAndConvertToCmd(Lte.class, column, value));
    }

    /**
     * in 多个值
     *
     * @param column 列
     * @param values 包含的指定值
     * @return In
     */
    @SafeVarargs
    public static In in(Cmd column, Object... values) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(values);
        Cmd[] cmds = new Cmd[values.length];
        for (int i = 0; i < values.length; i++) {
            cmds[i] = paramWrapAndConvertToCmd(In.class, column, values[i]);
        }
        return new In(column).add(cmds);
    }

    /**
     * in 多个值
     *
     * @param column 列
     * @param values 包含的指定值
     * @return In
     */
    public static In in(Cmd column, Collection<?> values) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(values);
        return new In(column).add(values.stream().map(item -> paramWrapAndConvertToCmd(In.class, column, item)).collect(Collectors.toList()));
    }

    /**
     * in 一个查询
     *
     * @param column 列
     * @param query  子查询
     * @return In
     */
    public static In in(Cmd column, IQuery query) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(query);
        return new In(column).add(query);
    }

    /**
     * not in 多个值
     *
     * @param column 列
     * @return NotIn
     */
    @SafeVarargs
    public static NotIn notIn(Cmd column, Object... values) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(values);
        Cmd[] cmds = new Cmd[values.length];
        for (int i = 0; i < values.length; i++) {
            cmds[i] = paramWrapAndConvertToCmd(NotIn.class, column, values[i]);
        }
        return new NotIn(column).add(cmds);
    }

    /**
     * not in 多个值
     *
     * @param column 列
     * @return NotIn
     */
    public static NotIn notIn(Cmd column, Collection<?> values) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(values);
        return new NotIn(column).add(values.stream().map(item -> paramWrapAndConvertToCmd(NotIn.class, column, item)).collect(Collectors.toList()));
    }

    /**
     * not in 一个查询
     *
     * @param column 列
     * @return NotIn
     */
    public static NotIn notIn(Cmd column, IQuery query) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(query);
        return new NotIn(column).add(query);
    }

    /**
     * exists 一个查询
     *
     * @param query 查询
     * @return Exists
     */
    public static Exists exists(IQuery query) {
        Objects.requireNonNull(query);
        return new Exists(query);
    }

    /**
     * not exists 一个查询
     *
     * @param query 查询
     * @return NotExists
     */
    public static NotExists notExists(IQuery query) {
        Objects.requireNonNull(query);
        return new NotExists(query);
    }

    /**
     * between 区间判断
     *
     * @param column 列
     * @param value  开始值
     * @param value2 结束值
     * @return Between
     */
    public static Between between(Cmd column, Object value, Object value2) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        Objects.requireNonNull(value2);
        return new Between(column, paramWrapAndConvertToCmd(Between.class, column, value), paramWrapAndConvertToCmd(Between.class, column, value2));
    }

    /**
     * not between 区间判断
     *
     * @param column 列
     * @param value  值
     * @param value2
     * @return NotBetween
     */
    public static NotBetween notBetween(Cmd column, Object value, Object value2) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        Objects.requireNonNull(value2);
        return new NotBetween(column, paramWrapAndConvertToCmd(NotBetween.class, column, value), paramWrapAndConvertToCmd(NotBetween.class, column, value2));
    }

    /**
     * like 判断
     *
     * @param column 列
     * @param value  值
     * @return Like
     */
    public static Like like(Cmd column, Object value) {
        return like(LikeMode.DEFAULT, column, value);
    }

    /**
     * like 判断
     *
     * @param column 列
     * @param value  值
     * @return Like
     */
    public static Like like(LikeMode mode, Cmd column, Object value) {
        Objects.requireNonNull(column);
        Object wrapValue = likeParamWrap(column, value, mode, false);
        if (wrapValue instanceof Object[]) {
            Object[] values = (Object[]) wrapValue;
            mode = (LikeMode) values[0];
            value = values[1];
        }
        return new Like(mode, column, cmd(value));
    }

    /**
     * notLike 判断
     *
     * @param column 列
     * @param value  值
     * @return NotLike
     */
    public static NotLike notLike(Cmd column, Object value) {
        return notLike(LikeMode.DEFAULT, column, value);
    }

    /**
     * not like 判断
     *
     * @param column 列
     * @param value  值
     * @return NotLike
     */
    public static NotLike notLike(LikeMode mode, Cmd column, Object value) {
        Objects.requireNonNull(column);
        Object wrapValue = likeParamWrap(column, value, mode, true);
        if (wrapValue instanceof Object[]) {
            Object[] values = (Object[]) wrapValue;
            mode = (LikeMode) values[0];
            value = values[1];
        }
        return new NotLike(mode, column, cmd(value));
    }

    /**
     * like 判断
     *
     * @param column 列
     * @param value  值
     * @return Like
     */
    public static ILike iLike(Cmd column, Object value) {
        return iLike(LikeMode.DEFAULT, column, value);
    }

    /**
     * like 判断
     *
     * @param column 列
     * @param value  值
     * @return Like
     */
    public static ILike iLike(LikeMode mode, Cmd column, Object value) {
        Objects.requireNonNull(column);
        Object wrapValue = likeParamWrap(column, value, mode, false);
        if (wrapValue instanceof Object[]) {
            Object[] values = (Object[]) wrapValue;
            mode = (LikeMode) values[0];
            value = values[1];
        }
        return new ILike(mode, column, cmd(value));
    }

    /**
     * notLike 判断
     *
     * @param column 列
     * @param value  值
     * @return NotLike
     */
    public static NotILike notILike(Cmd column, Object value) {
        return notILike(LikeMode.DEFAULT, column, value);
    }

    /**
     * not like 判断
     *
     * @param column 列
     * @param value  值
     * @return NotLike
     */
    public static NotILike notILike(LikeMode mode, Cmd column, Object value) {
        Objects.requireNonNull(column);
        Object wrapValue = likeParamWrap(column, value, mode, true);
        if (wrapValue instanceof Object[]) {
            Object[] values = (Object[]) wrapValue;
            mode = (LikeMode) values[0];
            value = values[1];
        }
        return new NotILike(mode, column, cmd(value));
    }

    /**
     * mysql fromUnixTime 函数
     *
     * @param column 列
     * @return FromUnixTime
     */
    public static FromUnixTime mysqlFromUnixTime(Cmd column) {
        Objects.requireNonNull(column);
        return new FromUnixTime(column);
    }

    /**
     * mysql fromUnixTime 函数
     *
     * @param column       列
     * @param containValue 包含值
     * @return JsonContains
     */
    public static JsonContains mysqlJsonContains(Cmd column, Serializable containValue) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(containValue);
        return new JsonContains(column, containValue);
    }

    /**
     * mysql json 是否包含值
     *
     * @param column       列
     * @param containValue 包含值
     * @param path         指定路径
     * @return JsonContains
     */
    public static JsonContains mysqlJsonContains(Cmd column, Serializable containValue, String path) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(containValue);
        Objects.requireNonEmpty(path);
        return new JsonContains(column, containValue, path);
    }

    /**
     * mysql json 是否包含路径
     *
     * @param column 列
     * @param paths  指定路径
     * @return JsonContainsPath
     */
    @SafeVarargs
    public static JsonContainsPath mysqlJsonContainsPath(Cmd column, String... paths) {
        return mysqlJsonContainsPath(column, true, paths);
    }

    /**
     * mysql json 是否包含路径
     *
     * @param column   列
     * @param allMatch 是否全匹配上
     * @param paths    指定路径
     * @return JsonContainsPath
     */
    @SafeVarargs
    public static JsonContainsPath mysqlJsonContainsPath(Cmd column, boolean allMatch, String... paths) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(paths);
        return new JsonContainsPath(column, allMatch, paths);
    }

    /**
     * mysql json 值提取操作
     *
     * @param column 列
     * @param paths  指定路径
     * @return JsonExtract
     */
    @SafeVarargs
    public static JsonExtract mysqlJsonExtract(Cmd column, String... paths) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(paths);
        return new JsonExtract(column, paths);
    }

    /**
     * 返回目标字符(串)在源字符串字符串中出现的起始位置
     *
     * @param column 列
     * @param str    匹配的字符
     * @return Instr
     */
    public static Instr instr(Cmd column, String str) {
        Objects.requireNonNull(column);
        Objects.requireNonEmpty(str);
        return new Instr(column, str);
    }


    /**
     * 排序值，如果包含在 values 里 则返回 在values的顺序值 从1开始；如果不在values里 则 返回 column的值
     * 可以稍微替代 mysql field 函数
     *
     * @param column 列
     * @param values 指定值
     * @return sort
     */
    public static Case sort(Cmd column, Serializable... values) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(values);
        Case cs = case_();
        for (int i = 0; i < values.length; i++) {
            cs.when(eq(column, values[i]), i + 1);
        }
        cs.else_(column);
        return cs;
    }

    /**
     * 分组后对列拼接 函数
     *
     * @param column 列
     * @param split  分隔符
     * @return GroupConcat
     */
    public static GroupConcat groupConcat(Cmd column, String split) {
        return new GroupConcat(column, split);
    }

    /**
     * 分组后对列拼接 函数
     * 默认是逗号拼接
     *
     * @param column 列
     * @return GroupConcat
     */
    public static GroupConcat groupConcat(Cmd column) {
        return groupConcat(column, ",");
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param column      列
     * @param point       另外一个对象
     * @param distance    距离
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    public static ST_DWithin ST_DWithin(Cmd column, Cmd point, double distance, Boolean useSpheroid) {
        return new ST_DWithin(column, point, distance, useSpheroid);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param column   列
     * @param point    另外一个坐标
     * @param distance 距离
     * @return
     */
    public static ST_DWithin ST_DWithin(Cmd column, ST_Point point, double distance) {
        return ST_DWithin(column, point, distance, null);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param column      列
     * @param point       另外一个坐标
     * @param distance    距离
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    public static ST_DWithin ST_DWithin(Cmd column, ST_Point point, double distance, Boolean useSpheroid) {
        return ST_DWithin(column, (Cmd) point, distance, useSpheroid);
    }

    /**
     * 计算2个对象的距离
     *
     * @param column 列
     * @param point  另外一个参考对象
     * @return
     */
    public static ST_Distance ST_Distance(Cmd column, Cmd point) {
        return ST_Distance(column, point, null);
    }

    /**
     * 计算2个对象的距离
     *
     * @param column      列
     * @param point       另外一个参考对象
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    public static ST_Distance ST_Distance(Cmd column, Cmd point, Boolean useSpheroid) {
        return new ST_Distance(column, point, useSpheroid);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param column 列
     * @param point  另外一个坐标
     * @return
     */
    public static ST_Distance ST_Distance(Cmd column, ST_Point point) {
        return ST_Distance(column, point, null);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param column      列
     * @param point       另外一个坐标
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    public static ST_Distance ST_Distance(Cmd column, ST_Point point, Boolean useSpheroid) {
        return ST_Distance(column, (Cmd) point, useSpheroid);
    }

    /**
     * 判断是否包含 geo
     *
     * @param column 列
     * @param geo    另外一个geo对象
     * @return
     */
    public static ST_Contains ST_Contains(Cmd column, Cmd geo) {
        return new ST_Contains(column, geo);
    }

    /**
     * 判断是否包含 point
     *
     * @param column 列
     * @param point  另外一个坐标
     * @return
     */
    public static ST_Contains ST_Contains(Cmd column, ST_Point point) {
        return ST_Contains(column, (Cmd) point);
    }

    /**
     * 获得 mysql的函数聚合类
     *
     * @return MysqlFunctions
     */
    public static MysqlFunctions mysql() {
        return new MysqlFunctions(null);
    }

    /**
     * 获得 mysql的函数聚合类
     *
     * @param column 列，后续可以以此列操作
     * @return MysqlFunctions
     */
    public static MysqlFunctions mysql(Cmd column) {
        Objects.requireNonNull(column);
        return new MysqlFunctions(column);
    }

    /**
     * 获得 postgres的函数聚合类
     *
     * @return PostgresFunctions
     */
    public static PostgresFunctions postgres() {
        return new PostgresFunctions(null);
    }

    /**
     * 获得 postgres的函数聚合类
     *
     * @param column 列，后续可以以此列操作
     * @return PostgresFunctions
     */
    public static PostgresFunctions postgres(Cmd column) {
        Objects.requireNonNull(column);
        return new PostgresFunctions(column);
    }

    /**
     * postgres DistinctOn
     *
     * @param onKeys 排重依据列
     * @return DISTINCT ON(...onKeys)
     */
    @SafeVarargs
    public static DistinctOn postgresDistinctOn(Cmd... onKeys) {
        Objects.requireNonNull(onKeys);
        return new DistinctOn(onKeys);
    }

    //-----------------------------构建getters----------------------------------------

    @SafeVarargs
    public static <T> GetterField[] getters(Getter<T>... getters) {
        return GetterFields.of(getters);
    }

    @SafeVarargs
    public static <T> GetterField[] getters(int storey, Getter<T>... getters) {
        return GetterFields.of(storey, getters);
    }

    public static <T1> GetterField[] getters(Getter<T1> getter1) {
        return GetterFields.of(getter1);
    }

    public static <T1> GetterField[] getters(Getter<T1> getter1, int storey1) {
        return GetterFields.of(getter1, storey1);
    }

    public static <T1, T2> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2) {
        return GetterFields.of(getter1, getter2);
    }

    public static <T1, T2> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2) {
        return GetterFields.of(getter1, storey1, getter2, storey2);
    }

    public static <T1, T2, T3> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3) {
        return GetterFields.of(getter1, getter2, getter3);
    }

    public static <T1, T2, T3> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3);
    }

    public static <T1, T2, T3, T4> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4) {
        return GetterFields.of(getter1, getter2, getter3, getter4);
    }

    public static <T1, T2, T3, T4> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4);
    }

    public static <T1, T2, T3, T4, T5> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5);
    }

    public static <T1, T2, T3, T4, T5> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5);
    }

    public static <T1, T2, T3, T4, T5, T6> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5, getter6);
    }

    public static <T1, T2, T3, T4, T5, T6> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5, getter6, storey6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5, getter6, getter7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5, getter6, storey6, getter7, storey7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5, getter6, getter7, getter8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5, getter6, storey6, getter7, storey7, getter8, storey8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8, Getter<T9> getter9) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5, getter6, getter7, getter8, getter9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8, Getter<T9> getter9, int storey9) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5, getter6, storey6, getter7, storey7, getter8, storey8, getter9, storey9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> GetterField[] getters(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8, Getter<T9> getter9, Getter<T10> getter10) {
        return GetterFields.of(getter1, getter2, getter3, getter4, getter5, getter6, getter7, getter8, getter9, getter10);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> GetterField[] getters(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8, Getter<T9> getter9, int storey9, Getter<T10> getter10, int storey10) {
        return GetterFields.of(getter1, storey1, getter2, storey2, getter3, storey3, getter4, storey4, getter5, storey5, getter6, storey6, getter7, storey7, getter8, storey8, getter9, storey9, getter10, storey10);
    }
}
