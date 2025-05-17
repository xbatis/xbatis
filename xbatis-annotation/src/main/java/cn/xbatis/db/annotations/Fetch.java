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

package cn.xbatis.db.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Fetch {

    /**
     * Fetch列
     * column 和  property 二选一，column优先
     *
     * @return
     */
    String column() default "";

    /**
     * Fetch 源实体类的属性
     * property + source + storey 组合 用于获取列
     *
     * @return
     */
    String property() default "";

    /**
     * Fetch property 对应的实体类
     * 默认时：从当前作用域自动获取
     * @return
     */
    Class source() default Void.class;

    /**
     * 存储层级
     *
     * @return
     */
    int storey() default 1;

    /**
     * 目标，相当于表
     *
     * @return
     */
    Class target();

    /**
     * 目标属性，相当于关联列 用于条件
     *
     * @return
     */
    String targetProperty();

    /**
     * 目标select属性
     * 用于返回单列的情况
     * 可以动态select 例如:[count({id})] or [{id}+{name} as aa]等
     *
     * @return
     */
    String targetSelectProperty() default "";

    /**
     * 用于结果排序 例如 "xx desc,xx2 desc"; 其中 xx xx2 均为 实体类属性，不是列，多个逗号分割
     * 可以动态 例如:[{id} desc,{createTime} asc]等
     *
     * @return
     */
    String orderBy() default "";

    /**
     * 用于查询group by 例如 "xx,xx2"; 其中 xx xx2 均为 实体类属性，不是列，多个逗号分割
     * 可以动态 例如:[{id} desc,{createTime}]等
     *
     * @return
     */
    String groupBy() default "";

    /**
     * 1 对 1 多条时，发现多条不报错
     *
     * @return
     */
    boolean multiValueErrorIgnore() default false;

    /**
     * 限制条数
     *
     * @return
     */
    int limit() default 0;

    /**
     * 强制使用in 查询，减少查询次数
     *
     * @return
     */
    boolean forceUseIn() default false;

    /**
     * 当值为null时，填充的值
     */
    String nullFillValue() default "";

    /**
     * 其他条件
     *
     * @return
     */
    String otherConditions() default "";
}
