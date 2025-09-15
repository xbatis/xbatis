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

package cn.xbatis.core.sql.executor.chain;

import cn.xbatis.core.mybatis.mapper.BaseMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.sql.executor.BaseQuery;
import cn.xbatis.core.sql.util.SelectClassUtil;
import cn.xbatis.page.IPager;
import db.sql.api.GetterFun;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.tookit.LambdaUtil;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 查询链路
 */
public class QueryChain<T> extends BaseQuery<QueryChain<T>, T> {

    protected BaseMapper mapper;

    protected boolean autoSelect = true;

    protected Class<?> entityType;

    protected QueryChain() {

    }

    public QueryChain(MybatisMapper<T> mapper) {
        this.mapper = mapper;
    }

    public QueryChain(MybatisMapper<T> mapper, Where where) {
        super(where);
        this.mapper = mapper;
    }

    public QueryChain(BaseMapper mapper, Class<T> entityType) {
        this.mapper = mapper;
        this.entityType = entityType;
    }

    public QueryChain(BaseMapper mapper, Class<T> entityType, Where where) {
        super(where);
        this.mapper = mapper;
        this.entityType = entityType;
    }

    /**
     * 非特殊情况 请使用of静态方法
     * 使用此方法后 后续执行查询需调用一次withMapper(Mapper)方法
     *
     * @param <T>
     * @return 自己
     */
    public static <T> QueryChain<T> create() {
        return new QueryChain<>();
    }

    public static <T> QueryChain<T> of(MybatisMapper<T> mapper) {
        return new QueryChain<>(mapper);
    }

    public static <T> QueryChain<T> of(MybatisMapper<T> mapper, Where where) {
        return new QueryChain<>(mapper, where);
    }

    public static <T> QueryChain<T> of(BaseMapper mapper, Class<T> entityType) {
        return new QueryChain<>(mapper, entityType);
    }

    public static <T> QueryChain<T> of(BaseMapper mapper, Class<T> entityType, Where where) {
        return new QueryChain<>(mapper, entityType, where);
    }

    protected Class<?> getEntityType() {
        if (entityType != null) {
            return entityType;
        }
        if (mapper instanceof MybatisMapper) {
            this.entityType = ((MybatisMapper) mapper).getEntityType();
        } else {
            throw new RuntimeException("you need specify entityType");
        }

        return entityType;
    }

    public QueryChain<T> disableAutoSelect() {
        this.autoSelect = false;
        return this;
    }

    public <T2> QueryChain<T2> returnType(Class<T2> returnType) {
        return (QueryChain<T2>) super.setReturnType(returnType);
    }

    public <T2> QueryChain<T2> returnType(Class<T2> returnType, Consumer<T2> consumer) {
        return (QueryChain<T2>) super.setReturnType(returnType, consumer);
    }

    public <V> QueryChain<Map<String, V>> returnMap() {
        return (QueryChain) super.setReturnType(Map.class);
    }

    private void setDefault() {
        this.setDefault(false);
    }

    private void setDefault(boolean forCount) {
        if (autoSelect && (Objects.isNull(this.select) || this.select.getSelectField().isEmpty())) {
            if (forCount) {
                this.selectCountAll();
            } else {
                boolean hasSetSelect = false;
                if (Objects.nonNull(this.returnType)) {
                    hasSetSelect = SelectClassUtil.select(this, this.returnType);
                }
                if (!hasSetSelect) {
                    this.select(getEntityType());
                }
            }
        }
        if (Objects.isNull(this.from)) {
            this.from(getEntityType());
        }
        if (Objects.isNull(this.returnType)) {
            this.returnType(getEntityType());
        }
    }

    private void checkAndSetMapper(BaseMapper mapper) {
        if (Objects.isNull(this.mapper)) {
            this.mapper = mapper;
            return;
        }
        if (this.mapper == mapper) {
            return;
        }
        throw new RuntimeException(" the mapper is already set, can't use another mapper");
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 操作目标实体类的mapper
     * @return 自己
     */
    public QueryChain<T> withMapper(MybatisMapper<?> mapper) {
        this.checkAndSetMapper(mapper);
        return this;
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 操作目标实体类的mapper
     * @return 自己
     */
    public QueryChain<T> withMapper(BaseMapper mapper, Class<?> entityType) {
        this.checkAndSetMapper(mapper);
        this.entityType = entityType;
        return this;
    }

    /**
     * 获取单个对象
     *
     * @return
     */
    public T get() {
        this.setDefault(false);
        return mapper.get(this);
    }

    /**
     * 获取列表
     *
     * @return
     */
    public List<T> list() {
        this.setDefault(false);
        return mapper.list(this);
    }

    /**
     * 获取列表
     *
     * @return
     */
    public Cursor<T> cursor() {
        this.setDefault(false);
        return mapper.cursor(this);
    }


    /**
     * 获取条数
     *
     * @return
     */
    public Integer count() {
        if (this.select == null) {
            this.selectCountAll();
        }
        this.setDefault(true);
        return mapper.count(this);
    }

    /**
     * 判断是否存在
     *
     * @return
     */
    public boolean exists() {
        if (this.select == null) {
            this.select1();
        }
        this.limit(1);
        this.setDefault();
        return mapper.exists(this);
    }

    /**
     * 分页查询
     *
     * @param pager
     * @return
     */
    public <P extends IPager<T>> P paging(P pager) {
        this.setDefault();
        return mapper.paging(this, pager);
    }

    /**
     * 将结果转成map
     *
     * @param mapKey 指定的map的key属性
     * @param mapKey map的key
     * @return
     */
    public <R> Map<R, T> mapWithKey(GetterFun<T, R> mapKey) {
        this.setDefault();
        return mapper.mapWithKey(LambdaUtil.getName(mapKey), this);
    }

    /**
     * 将结果转成map（key value都是简单类型的情况）
     * 缺点：需要自定义类接收key value的值
     *
     * @param mapKey 指定的map的key属性
     * @param <R>    valueGetter  指定返回T中的某字段的Getter方法
     * @param <R>    map的key类型
     * @param <R2>   map的value类型
     * @return
     */
    public <R, R2> Map<R, R2> mapWithKeyAndValue(GetterFun<T, R> mapKey, GetterFun<T, R2> valueGetter) {
        Map<R, Object> data = (Map<R, Object>) this.mapWithKey(mapKey);
        if (data == null) {
            return null;
        }
        data.entrySet().forEach(entry -> {
            Object value = valueGetter.apply((T) entry.getValue());
            entry.setValue(value);
        });
        return (Map<R, R2>) data;
    }

    /**
     * 将结果转成map
     *
     * @param mapKey 指定的map的key属性
     * @param <K>    map的key
     * @return
     */
    public <K> Map<K, T> mapWithKey(String mapKey) {
        this.setDefault();
        return mapper.mapWithKey(mapKey, this);
    }
}
