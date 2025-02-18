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

package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.cmd.LikeMode;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.DatePattern;
import db.sql.api.impl.cmd.condition.*;
import db.sql.api.impl.cmd.dbFun.mysql.MysqlFunctions;
import db.sql.api.impl.cmd.postgis.ST_Contains;
import db.sql.api.impl.cmd.postgis.ST_DWithin;
import db.sql.api.impl.cmd.postgis.ST_Distance;
import db.sql.api.impl.cmd.postgis.ST_Point;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface FunctionInterface extends Cmd {

    /**
     * 获得 mysql的函数聚合类
     *
     * @return MysqlFunctions
     */
    default MysqlFunctions mysql() {
        return new MysqlFunctions(this);
    }

    /**
     * 对自己进行求和操作
     *
     * @return Sum
     */
    default Sum sum() {
        return Methods.sum(this);
    }

    /**
     * 对自己进行求最大操作
     *
     * @return Max
     */
    default Max max() {
        return Methods.max(this);
    }

    /**
     * 对自己进行求最小操作
     *
     * @return Min
     */
    default Min min() {
        return Methods.min(this);
    }

    /**
     * 对自己进行avg求平均值操作
     *
     * @return Avg
     */
    default Avg avg() {
        return Methods.avg(this);
    }

    /**
     * 对自己进行abs求绝对值操作
     *
     * @return Abs
     */
    default Abs abs() {
        return Methods.abs(this);
    }

    /**
     * 对自己进行n次幂操作
     *
     * @return Pow
     */
    default Pow pow(int n) {
        return Methods.pow(this, n);
    }

    /**
     * 对自己进行统计个数操作
     *
     * @return Count
     */
    default Count count() {
        return Methods.count(this);
    }

    /**
     * 对自己进行统计个数并是否去重操作
     *
     * @param distinct 是否去重，true会加上DISTINCT
     * @return Count
     */
    default Count count(boolean distinct) {
        return Methods.count(this, distinct);
    }

    /**
     * 对自己进行四舍五入操作
     *
     * @return Round
     */
    default Round round() {
        return this.round(0);
    }

    /**
     * 对自己进行四舍五入操作
     *
     * @param precision 保留小数的位数
     * @return Round
     */
    default Round round(int precision) {
        return Methods.round(this, precision);
    }

    /**
     * 对自己进行向上取整操作
     *
     * @return Ceil
     */
    default Ceil ceil() {
        return Methods.ceil(this);
    }

    /**
     * 对自己进行向下取整操作
     *
     * @return Floor
     */
    default Floor floor() {
        return Methods.floor(this);
    }

    /**
     * 获取 自己的符号，负数为-1 0为0、正数为1
     *
     * @return Sign
     */
    default Sign sign() {
        return Methods.sign(this);
    }

    /**
     * 对自己进行整数操作，不留小数操作
     *
     * @return Truncate
     */
    default Truncate truncate() {
        return this.truncate(0);
    }

    /**
     * 对自己进行保留小数位数操作
     *
     * @return Truncate
     * @precision 保留小数位数
     */
    default Truncate truncate(int precision) {
        return Methods.truncate(this, precision);
    }

    /**
     * 对自己求平方根操作
     *
     * @return Sqrt
     */
    default Sqrt sqrt() {
        return Methods.sqrt(this);
    }

    /**
     * 对自己求余取模操作操作
     *
     * @param divisor 除数
     * @return Mod
     */
    default Mod mod(Number divisor) {
        return Methods.mod(this, divisor);
    }

    /**
     * 以自己为底数，求number对数操作
     *
     * @return Log
     */
    default Log log(int number) {
        return Methods.log(this, number);
    }

    /**
     * 以自己为弧度，转换为度数操作
     *
     * @return Degrees
     */
    default Degrees degrees() {
        return Methods.degrees(this);
    }

    /**
     * 以自己为度数，转换为弧度操作
     *
     * @return Radians
     */
    default Radians radians() {
        return Methods.radians(this);
    }

    /**
     * 以自己为角度，求正弦值操作
     *
     * @return Sin
     */
    default Sin sin() {
        return Methods.sin(this);
    }

    /**
     * 以自己为角度，求反正弦值操作
     *
     * @return Asin
     */
    default Asin asin() {
        return Methods.asin(this);
    }

    /**
     * 以自己为角度，求余弦值操作
     *
     * @return Cos
     */
    default Cos cos() {
        return Methods.cos(this);
    }

    /**
     * 以自己为角度，求反余弦值操作
     *
     * @return Cos
     */
    default Acos acos() {
        return Methods.acos(this);
    }


    /**
     * 以自己为角度，求正切值操作
     *
     * @return Tan
     */
    default Tan tan() {
        return Methods.tan(this);
    }

    /**
     * 以自己为角度，求反正切值操作
     *
     * @return Atan
     */
    default Atan atan() {
        return Methods.atan(this);
    }

    /**
     * 以自己为角度，求余切值操作
     *
     * @return Cot
     */
    default Cot cot() {
        return Methods.cot(this);
    }

    /**
     * 求自己的char长度操作
     *
     * @return CharLength
     */
    default CharLength charLength() {
        return Methods.charLength(this);
    }

    /**
     * 求自己的长度操作
     *
     * @return Length
     */
    default Length length() {
        return Methods.length(this);
    }

    /**
     * 从左边开始截取length个长度操作
     *
     * @return Left
     */
    default Left left(int length) {
        return Methods.left(this, length);
    }

    /**
     * 从右边开始截取length个长度操作
     *
     * @return Right
     */
    default Right right(int length) {
        return Methods.right(this, length);
    }

    /**
     * 字段串截取操作
     *
     * @param start 默认从1开始
     * @return
     */
    default SubStr subStr(int start) {
        return Methods.subStr(this, start);
    }

    /**
     * 字段串截取操作
     *
     * @param start  默认从1开始
     * @param length 截取长度
     * @return
     */
    default SubStr subStr(int start, int length) {
        return Methods.subStr(this, start, length);
    }

    /**
     * 将自己转成大写操作
     *
     * @return Upper
     */
    default Upper upper() {
        return Methods.upper(this);
    }


    /**
     * 将自己转成小写操作
     *
     * @return Lower
     */
    default Lower lower() {
        return Methods.lower(this);
    }

    /**
     * 左边填充内容操作
     *
     * @param length 填充后的长度
     * @param pad    填充内容
     * @return Lpad
     */
    default Lpad lpad(int length, String pad) {
        return Methods.lpad(this, length, pad);
    }

    /**
     * 右边填充内容操作
     *
     * @param length 填充后的长度
     * @param pad    填充内容
     * @return Rpad
     */
    default Rpad rpad(int length, String pad) {
        return Methods.rpad(this, length, pad);
    }

    /**
     * 对自己进行去空格操作
     *
     * @return Trim
     */
    default Trim trim() {
        return Methods.trim(this);
    }

    /**
     * 对自己进行去左边空格操作
     *
     * @return Ltrim
     */
    default Ltrim ltrim() {
        return Methods.ltrim(this);
    }

    /**
     * 对自己进行去右边空格操作
     *
     * @return Ltrim
     */
    default Rtrim rtrim() {
        return Methods.rtrim(this);
    }

    /**
     * 将自己和s 进行比较，<s返回-1，等于s返回0，大于s返回1
     *
     * @param s 需要比较的值
     * @return Strcmp
     */
    default Strcmp strcmp(String s) {
        return Methods.strcmp(this, s);
    }

    /**
     * 将自己重复n次操作
     *
     * @param n 次数
     * @return Repeat
     */
    default Repeat repeat(int n) {
        return Methods.repeat(this, n);
    }

    /**
     * 对自己进行字符串替换操作
     *
     * @return Repeat
     * @target 匹配字符
     * @replacement 用于替换的字符
     */
    default Replace replace(String target, String replacement) {
        return Methods.replace(this, target, replacement);
    }

    /**
     * 将自己内容反转操作
     *
     * @return Reverse
     */
    default Reverse reverse() {
        return Methods.reverse(this);
    }

    /**
     * 取自己的年份部分操作
     *
     * @return Year
     */
    default Year year() {
        return Methods.year(this);
    }

    /**
     * 取自己的月份部分操作
     *
     * @return Month
     */
    default Month month() {
        return Methods.month(this);
    }

    /**
     * 取自己的月份部分操作
     *
     * @return Day
     */
    default Day day() {
        return Methods.day(this);
    }

    /**
     * 将自己转成date，格式为YYYY-MM-DD
     *
     * @return DateFormat
     */
    default DateFormat date() {
        return Methods.date(this);
    }

    /**
     * 将自己转成date格式化
     *
     * @param pattern 格式
     * @return DateFormat
     */
    default DateFormat dateFormat(String pattern) {
        return Methods.dateFormat(this, pattern);
    }

    /**
     * 将自己转成date格式化
     *
     * @param pattern 格式，例如 DatePattern.YYYY_MM_DD
     * @return DateFormat
     */
    default DateFormat dateFormat(DatePattern pattern) {
        return Methods.dateFormat(this, pattern);
    }

    /**
     * 获取自己属于星期几
     *
     * @return
     */
    default Weekday weekday() {
        return Methods.weekday(this);
    }

    /**
     * 取自己的小时部分操作
     *
     * @return Day
     */
    default Hour hour() {
        return Methods.hour(this);
    }

    /**
     * 获取自己与another 日期差（正数）
     *
     * @param another 需要对比对象
     * @return DateDiff
     */
    default DateDiff dateDiff(Cmd another) {
        return Methods.dateDiff(this, another);
    }

    /**
     * 获取自己与another 日期差（正数）
     *
     * @param another
     * @return DateDiff
     */
    default DateDiff dateDiff(Object another) {
        return Methods.dateDiff(this, Methods.cmd(another));
    }

    /**
     * 对自己进行天数增加
     *
     * @param n 增加的数量
     * @return DateAdd
     */
    default DateAdd dateAdd(int n) {
        return this.dateAdd(n, TimeUnit.DAYS);
    }

    /**
     * 对自己进行天数增加
     *
     * @param n        增加的数量
     * @param timeUnit n的单位
     * @return DateAdd
     */
    default DateAdd dateAdd(int n, TimeUnit timeUnit) {
        return Methods.dateAdd(this, n, timeUnit);
    }

    /**
     * 对自己进行天数减少
     *
     * @param n 减少的数量
     * @return DateAdd
     */
    default DateAdd dateSub(int n) {
        return this.dateSub(n, TimeUnit.DAYS);
    }

    /**
     * 对自己进行天数减少
     *
     * @param n        减少的数量
     * @param timeUnit n的单位
     * @return DateAdd
     */
    default DateAdd dateSub(int n, TimeUnit timeUnit) {
        return this.dateAdd(n * -1, timeUnit);
    }

    /**
     * 以自己为IP，转换成网络字节序操作
     *
     * @return InetAton
     */
    default InetAton inetAton() {
        return Methods.inetAton(this);
    }

    /**
     * 以自己为网络字节序，转换成IP操作
     *
     * @return InetNtoa
     */
    default InetNtoa inetNtoa() {
        return Methods.inetNtoa(this);
    }

    /**
     * 乘法操作
     *
     * @param value 被乘数
     * @return Multiply
     */
    default Multiply multiply(Number value) {
        return Methods.multiply(this, value);
    }

    /**
     * 乘法操作
     *
     * @param value 乘数
     * @return Multiply
     */
    default Multiply multiply(Cmd value) {
        return Methods.multiply(this, value);
    }

    /**
     * 除法操作
     *
     * @param value 除数
     * @return Multiply
     */
    default Divide divide(Number value) {
        return Methods.divide(this, value);
    }

    /**
     * 除法操作
     *
     * @param value 除数
     * @return Multiply
     */
    default Divide divide(Cmd value) {
        return Methods.divide(this, value);
    }

    /**
     * 减法操作
     *
     * @param value 减数
     * @return Multiply
     */
    default Subtract subtract(Number value) {
        return Methods.subtract(this, value);
    }

    /**
     * 减法操作
     *
     * @param value 减数
     * @return Multiply
     */
    default Subtract subtract(Cmd value) {
        return Methods.subtract(this, value);
    }

    /**
     * 加法操作
     *
     * @param value 加数
     * @return Plus
     */
    default Plus plus(Number value) {
        return Methods.plus(this, value);
    }

    /**
     * 加法操作
     *
     * @param value 加数
     * @return Plus
     */
    default Plus plus(Cmd value) {
        return Methods.plus(this, value);
    }

    /**
     * 将自己和 values 进行字符串拼接操作
     *
     * @param values
     * @return Concat
     */
    default Concat concat(Serializable... values) {
        return Methods.concat(this, (Object[]) values);
    }

    /**
     * 将自己和 values 进行字符串拼接操作
     *
     * @param values 字符串
     * @return Concat
     */
    default Concat concat(Cmd... values) {
        return Methods.concat(this, (Object[]) values);
    }

    /**
     * 将自己和 values 进行字符串拼接操作
     *
     * @param split  分隔符
     * @param values 字符串
     * @return ConcatWs
     */
    default ConcatWs concatWs(String split, Serializable... values) {
        return Methods.concatWs(this, split, (Object[]) values);
    }

    /**
     * 将自己和 values 进行字符串拼接操作
     *
     * @param split  分隔符
     * @param values 字符串
     * @return ConcatWs
     */
    default ConcatWs concatWs(String split, Cmd... values) {
        return Methods.concatWs(this, split, (Object[]) values);
    }

    /**
     * ifNull选择操作，自己为NULL时 以value返回，否则返回自己
     *
     * @param value 自己是NULL的else值
     * @return IfNull
     */
    default IfNull ifNull(Object value) {
        return Methods.ifNull(this, Methods.cmd(value));
    }

    /**
     * 条件判断（相等判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Eq
     */
    default Eq eq(Object value) {
        return Methods.eq(this, Methods.cmd(value));
    }

    /**
     * 条件判断（大于判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Gt
     */
    default Gt gt(Object value) {
        return Methods.gt(this, Methods.cmd(value));
    }

    /**
     * 条件判断（大于等于判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Gte
     */
    default Gte gte(Object value) {
        return Methods.gte(this, Methods.cmd(value));
    }

    /**
     * 条件判断（小于判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Lt
     */
    default Lt lt(Object value) {
        return Methods.lt(this, Methods.cmd(value));
    }

    /**
     * 条件判断（小于等于判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Lt
     */
    default Lte lte(Object value) {
        return Methods.lte(this, Methods.cmd(value));
    }

    /**
     * 条件判断（不相等判断）
     *
     * @param value 比较值；可以是普通值，也可以是CMD
     * @return Ne
     */
    default Ne ne(Object value) {
        return Methods.ne(this, Methods.cmd(value));
    }

    /**
     * 条件判断（BETWEEN区间）
     *
     * @param value1 开始区间值
     * @param value2 结束区间值
     * @return Between
     */
    default Between between(Serializable value1, Serializable value2) {
        return Methods.between(this, value1, value2);
    }

    /**
     * 条件判断（NOT BETWEEN区间）
     *
     * @param value1 开始区间值
     * @param value2 结束区间值
     * @return Between
     */
    default NotBetween notBetween(Serializable value1, Serializable value2) {
        return Methods.notBetween(this, value1, value2);
    }

    /**
     * 条件判断（LIKE模糊匹配）
     *
     * @param value 匹配值
     * @return Like
     */
    default Like like(String value) {
        return like(LikeMode.DEFAULT, value);
    }

    /**
     * 条件判断（LIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default Like like(LikeMode mode, String value) {
        return Methods.like(mode, this, value);
    }

    /**
     * 条件判断（LIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default Like like(LikeMode mode, Cmd value) {
        return Methods.like(mode, this, value);
    }

    /**
     * 条件判断（NOT LIKE模糊匹配）
     *
     * @param value 匹配值
     * @return Like
     */
    default NotLike notLike(String value) {
        return notLike(LikeMode.DEFAULT, value);
    }

    /**
     * 条件判断（NOT LIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default NotLike notLike(LikeMode mode, String value) {
        return Methods.notLike(mode, this, value);
    }

    /**
     * 条件判断（NOT LIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default NotLike notLike(LikeMode mode, Cmd value) {
        return Methods.notLike(mode, this, value);
    }

    /**
     * 条件判断（iLIKE模糊匹配）
     *
     * @param value 匹配值
     * @return Like
     */
    default ILike iLike(String value) {
        return iLike(LikeMode.DEFAULT, value);
    }

    /**
     * 条件判断（iLIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default ILike iLike(LikeMode mode, String value) {
        return Methods.iLike(mode, this, value);
    }

    /**
     * 条件判断（iLIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default ILike iLike(LikeMode mode, Cmd value) {
        return Methods.iLike(mode, this, value);
    }

    /**
     * 条件判断（NOT ILIKE模糊匹配）
     *
     * @param value 匹配值
     * @return Like
     */
    default NotILike notILike(String value) {
        return notILike(LikeMode.DEFAULT, value);
    }

    /**
     * 条件判断（NOT iLIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default NotILike notILike(LikeMode mode, String value) {
        return Methods.notILike(mode, this, value);
    }

    /**
     * 条件判断（NOT iLIKE模糊匹配）
     *
     * @param mode  匹配方式；例如LikeMode.LEFT
     * @param value 匹配值
     * @return Like
     */
    default NotILike notILike(LikeMode mode, Cmd value) {
        return Methods.notILike(mode, this, value);
    }

    /**
     * 条件判断（IN包含判断）
     *
     * @param values 匹配值
     * @return Ne
     */
    default In in(Serializable... values) {
        return Methods.in(this, (Object[]) values);
    }

    /**
     * 条件判断（IN包含判断）
     *
     * @param values 匹配值
     * @return Ne
     */
    default In in(Collection<? extends Serializable>... values) {
        return Methods.in(this, values);
    }

    /**
     * 条件判断（NOT IN不包含判断）
     *
     * @param values 匹配值
     * @return Ne
     */
    default NotIn notIn(Serializable... values) {
        return Methods.notIn(this, (Object[]) values);
    }

    /**
     * 条件判断（NOT IN不包含判断）
     *
     * @param values 匹配值
     * @return Ne
     */
    default NotIn notIn(Collection<? extends Serializable>... values) {
        return Methods.notIn(this, values);
    }

    /**
     * 条件判断（是否NULL值判断）
     *
     * @return IsNull
     */
    default IsNull isNull() {
        return Methods.isNull(this);
    }

    /**
     * 条件判断（是否空值判断）
     *
     * @return Empty
     */
    default Empty empty() {
        return Methods.empty(this);
    }

    /**
     * 条件判断（是否非空值判断）
     *
     * @return NotEmpty
     */
    default NotEmpty notEmpty() {
        return Methods.notEmpty(this);
    }

    /**
     * 条件判断（查找自己种str字符串中出现的起始位置）
     *
     * @param str 搜索的字符
     * @return Instr
     */
    default Instr instr(String str) {
        return Methods.instr(this, str);
    }

    /**
     * 排序值，如果包含在 values 里 则返回 在values的顺序值 从1开始；如果不在values里 则 返回 自身的值
     * 可以稍微替代 mysql field 函数
     *
     * @param values 指定值
     * @return Case
     */
    default Case sort(Serializable... values) {
        return Methods.sort(this, values);
    }

    /**
     * 分组后对列拼接
     *
     * @param split 分隔符
     * @return GroupConcat
     */
    default GroupConcat groupConcat(String split) {
        return Methods.groupConcat(this, split);
    }

    /**
     * 分组后对列拼接
     * 默认是逗号拼接
     *
     * @return GroupConcat
     */
    default GroupConcat groupConcat() {
        return Methods.groupConcat(this);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param point    另外一个对象
     * @param distance 距离
     * @return
     */
    default ST_DWithin ST_DWithin(Cmd point, double distance) {
        return ST_DWithin(point, distance, null);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param point       另外一个对象
     * @param distance    距离
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    default ST_DWithin ST_DWithin(Cmd point, double distance, Boolean useSpheroid) {
        return Methods.ST_DWithin(this, point, distance, useSpheroid);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param point    另外一个坐标
     * @param distance 距离
     * @return
     */
    default ST_DWithin ST_DWithin(ST_Point point, double distance) {
        return ST_DWithin(point, distance, null);
    }

    /**
     * 如果两个对象之间的距离在指定范围之内，则返回True
     *
     * @param point       另外一个坐标
     * @param distance    距离
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    default ST_DWithin ST_DWithin(ST_Point point, double distance, Boolean useSpheroid) {
        return ST_DWithin((Cmd) point, distance, useSpheroid);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param point 另外一个对象
     * @return
     */
    default ST_Distance ST_Distance(Cmd point) {
        return ST_Distance(point, null);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param point       另外一个对象
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    default ST_Distance ST_Distance(Cmd point, Boolean useSpheroid) {
        return Methods.ST_Distance(this, point, useSpheroid);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param point 另外一个坐标
     * @return
     */
    default ST_Distance ST_Distance(ST_Point point) {
        return ST_Distance(point, null);
    }

    /**
     * 计算2个坐标的距离
     *
     * @param point       另外一个坐标
     * @param useSpheroid 是否使用椭球参考系。使用椭球参考系会使得结果更精确但稍慢。
     * @return
     */
    default ST_Distance ST_Distance(ST_Point point, Boolean useSpheroid) {
        return ST_Distance((Cmd) point, useSpheroid);
    }

    /**
     * 判断是否包含 geo
     *
     * @param geo 另外一个geo对象
     * @return
     */
    default ST_Contains ST_Contains(Cmd geo) {
        return new ST_Contains(this, geo);
    }

    /**
     * 判断是否包含 point
     *
     * @param point 另外一个坐标
     * @return
     */
    default ST_Contains ST_Contains(ST_Point point) {
        return ST_Contains((Cmd) point);
    }
}
