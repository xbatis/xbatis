package cn.mybatis.mp.core.mybatis.mapper;

import cn.mybatis.mp.core.mybatis.mapper.context.*;
import cn.mybatis.mp.core.sql.executor.BaseDelete;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.BaseQuery;
import cn.mybatis.mp.core.sql.executor.BaseUpdate;
import cn.mybatis.mp.db.Model;
import db.sql.api.Getter;
import db.sql.api.cmd.executor.DBRunnable;
import db.sql.api.impl.cmd.struct.Where;
import db.sql.api.impl.tookit.LambdaUtil;
import org.apache.ibatis.cursor.Cursor;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 数据库 Mapper
 * $ 开头的方法一般不建议去使用
 *
 * @param <T>
 */
public interface MybatisMapper<T> extends CommonMapper {

    default void runDBRunner(Consumer<DBRunnable<BasicMapper, BasicMapper>> consumer) {
        this.getBasicMapper().runDBRunner(consumer);
    }

    /**
     * 获取实体类的type
     *
     * @return 当前实体类
     */
    Class<T> getEntityType();

    BasicMapper getBasicMapper();

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 当个当前实体类
     */
    default T getById(Serializable id) {
        return getBasicMapper().getById(getEntityType(), id);
    }

    /**
     * 根据ID查询，只返回指定列
     *
     * @param id           ID
     * @param selectFields select列
     * @return 当个当前实体类
     */
    default T getById(Serializable id, Getter<T>... selectFields) {
        return getBasicMapper().getById(getEntityType(), id, selectFields);
    }


    /**
     * 根据实体类删除
     *
     * @param entity 实体类实例
     * @return 影响的数量
     */
    default int delete(T entity) {
        return getBasicMapper().delete(entity);
    }

    /**
     * 多个删除
     *
     * @param list 实体类实例list
     * @return 修改条数
     */
    default int delete(List<T> list) {
        return getBasicMapper().delete(list);
    }


    /**
     * 根据id删除
     *
     * @param id ID
     * @return 影响的数量
     */
    default int deleteById(Serializable id) {
        return getBasicMapper().deleteById(getEntityType(), id);
    }

    /**
     * 批量删除多个
     *
     * @param ids 多个ID
     * @return 影响的数量
     */
    default int deleteByIds(Serializable... ids) {
        return getBasicMapper().deleteByIds(getEntityType(), ids);
    }

    /**
     * 批量删除多个
     *
     * @param ids 多个ID
     * @return 影响数量
     */
    default int deleteByIds(List<Serializable> ids) {
        return getBasicMapper().deleteByIds(getEntityType(), ids);
    }

    /**
     * 动态条件删除
     *
     * @param consumer
     * @return
     */
    default int delete(Consumer<Where> consumer) {
        return getBasicMapper().delete(getEntityType(), consumer);
    }

    /**
     * 动态条件删除
     *
     * @param where
     * @return
     */
    default int delete(Where where) {
        return getBasicMapper().delete(getEntityType(), where);
    }


    /**
     * 单个查询
     *
     * @param consumer where consumer
     * @return 当个当前实体
     */
    default T get(Consumer<Where> consumer) {
        return getBasicMapper().get(getEntityType(), consumer);
    }

    default T getWithQueryFun(Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().getWithQueryFun(getEntityType(), consumer);
    }

    /**
     * 是否存在
     *
     * @param consumer where consumer
     * @return 是否存在
     */
    default boolean exists(Consumer<Where> consumer) {
        return getBasicMapper().exists(getEntityType(), consumer);
    }


    default boolean existsWithQueryFun(Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().existsWithQueryFun(getEntityType(), consumer);
    }


    /**
     * 实体类修改
     *
     * @param entity
     * @return 影响条数
     */
    default int update(T entity) {
        return getBasicMapper().update(entity);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list
     * @return 影响条数
     */
    default int update(List<T> list) {
        return getBasicMapper().update(list);
    }

    /**
     * 多个修改，非批量行为
     *
     * @param list
     * @return 修改条数
     */
    default int update(List<T> list, Getter<T>... forceUpdateFields) {
        return getBasicMapper().update(list, forceUpdateFields);
    }

    /**
     * 实体类修改
     *
     * @param entity
     * @param forceUpdateFields 强制更新指定，解决需要修改为null的需求
     * @return 返回修改条数
     */
    default int update(T entity, Getter<T>... forceUpdateFields) {
        return getBasicMapper().update(entity, forceUpdateFields);
    }


    default int update(T entity, Consumer<Where> consumer) {
        return getBasicMapper().update(entity, consumer);
    }

    /**
     * @param entity 实体类
     * @param where  可通过Wheres.create()创建
     * @return
     */
    default int update(T entity, Where where) {
        return getBasicMapper().update(entity, where, null);
    }

    /**
     * @param entity            实体类
     * @param where             可通过Wheres.create()创建
     * @param forceUpdateFields 强制更新字段
     * @return
     */
    default int update(T entity, Where where, Getter<T>... forceUpdateFields) {
        return getBasicMapper().update(entity, where, forceUpdateFields);
    }

    /**
     * model修改 部分字段修改
     *
     * @param model 实体类model
     * @return 修改的条数
     */
    default int update(Model<T> model) {
        return getBasicMapper().update(model);
    }

    /**
     * model修改 部分字段修改
     *
     * @param model             实体类model
     * @param forceUpdateFields 强制更新指定，解决需要修改为null的需求
     * @return 修改的条数
     */
    default int update(Model<T> model, Getter<T>... forceUpdateFields) {
        return getBasicMapper().update(model, forceUpdateFields);
    }

    default int update(Model<T> model, Where consumer, Getter<T>... forceUpdateFields) {
        return getBasicMapper().update(model, consumer, forceUpdateFields);
    }

    /**
     * 列表查询,返回类型，当前实体类
     *
     * @param consumer where consumer
     * @return 返回结果列表
     */
    default List<T> list(Consumer<Where> consumer) {
        return getBasicMapper().list(getEntityType(), consumer);
    }

    default List<T> list(Where consumer, Getter<T>... selectFields) {
        return getBasicMapper().list(getEntityType(), consumer, selectFields);
    }

    default List<T> listWithQueryFun(Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().listWithQueryFun(getEntityType(), consumer);
    }

    /**
     * 游标查询,返回类型，当前实体类
     *
     * @param consumer where consumer
     * @return 返回游标
     */
    default Cursor<T> cursor(Consumer<Where> consumer) {
        return getBasicMapper().cursor(getEntityType(), consumer);
    }

    default Cursor<T> cursor(Where consumer, Getter<T>... selectFields) {
        return getBasicMapper().cursor(getEntityType(), consumer, selectFields);
    }

    default Cursor<T> cursorWithQueryFun(Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().cursorWithQueryFun(getEntityType(), consumer);
    }

    /**
     * count查询
     *
     * @param consumer where consumer
     * @return 返回count数
     */
    default Integer count(Consumer<Where> consumer) {
        return getBasicMapper().count(getEntityType(), consumer);
    }

    /**
     * count查询
     *
     * @param consumer query consumer
     * @return 返回count数
     */
    default Integer countWithQueryFun(Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().countWithQueryFun(getEntityType(), consumer);
    }

    /**
     * 分页查询
     *
     * @param consumer where consumer
     * @param pager    分页参数
     * @return 分页结果
     */
    default Pager<T> paging(Pager<T> pager, Consumer<Where> consumer) {
        return getBasicMapper().paging(getEntityType(), pager, consumer);
    }

    default Pager<T> paging(Consumer<Where> consumer, Pager<T> pager, Getter<T>... selectFields) {
        return getBasicMapper().paging(getEntityType(), consumer, pager, selectFields);
    }


    /**
     * 分页查询
     *
     * @param consumer query consumer
     * @return 返回page
     */
    default <R, P extends Pager<R>> P pagingWithQueryFun(P pager, Consumer<BaseQuery<?>> consumer) {
        return getBasicMapper().pagingWithQueryFun(getEntityType(), pager, consumer);
    }


    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey map的key
     * @param ids    ids
     * @param <K>    map的key的类型
     * @return 一个map
     */
    default <K> Map<K, T> mapWithKey(Getter<T> mapKey, Serializable... ids) {
        return getBasicMapper().mapWithKey(mapKey, ids);
    }

    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey map的key
     * @param ids    ids
     * @param <K>    map的key的类型
     * @return 一个map
     */
    default <K> Map<K, T> mapWithKey(Getter<T> mapKey, List<Serializable> ids) {
        return getBasicMapper().mapWithKey(mapKey, ids);
    }


    /**
     * 根据多个id查询结果转map
     *
     * @param mapKey   map的key
     * @param consumer where consumer
     * @param <K>      map的key的类型
     * @return 一个map
     */
    default <K> Map<K, T> mapWithKey(Getter<T> mapKey, Consumer<Where> consumer) {
        return getBasicMapper().mapWithKey(mapKey, consumer);
    }


    //通用

    /**
     * 动态查询 返回单个当前实体
     *
     * @param query 查询query
     * @return 单个当前实体
     */

    default <R> R get(BaseQuery<?> query) {
        return this.get(query, true);
    }

    /**
     * 动态查询
     *
     * @param query    查询query
     * @param optimize 是否优化
     * @param <R>      返回的类型
     * @return 返回单个当前实体
     */
    default <R> R get(BaseQuery<?> query, boolean optimize) {
        return this.getBasicMapper().get(query, optimize);
    }

    /**
     * 是否存在
     *
     * @param query
     * @return 是否存在
     */
    default boolean exists(BaseQuery<?> query) {
        return this.exists(query, true);
    }

    /**
     * 是否存在
     *
     * @param query    子查询
     * @param optimize 是否优化
     * @return
     */
    default boolean exists(BaseQuery<?> query, boolean optimize) {
        return this.getBasicMapper().exists(query, optimize);
    }

    /**
     * 实体类新增
     *
     * @param entity
     * @return 影响条数
     */
    default int save(T entity) {
        return this.$saveEntity(new EntityInsertContext(entity));
    }

    /**
     * 多个保存，非批量行为
     *
     * @param list
     * @return 插入条数
     */
    default int save(List<T> list) {
        int cnt = 0;
        for (T entity : list) {
            cnt += this.save(entity);
        }
        return cnt;
    }

    /**
     * 使用数据库原生方式批量插入
     * 一次最好在100条内
     * <p>
     * 会自动加入 主键 租户ID 逻辑删除列 乐观锁
     * 自动设置 默认值,不会忽略NULL值字段
     *
     * @param list
     * @param saveFields 指定那些列插入
     * @return 插入的条数
     */
    default int saveBatch(List<T> list, Getter<T>... saveFields) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return 0;
        }
        if (Objects.isNull(saveFields) || saveFields.length < 1) {
            throw new RuntimeException("saveFields can't be null or empty");
        }
        Set<String> saveFieldSet = new HashSet<>();
        for (Getter<?> column : saveFields) {
            saveFieldSet.add(LambdaUtil.getName(column));
        }
        return this.$save(new EntityBatchInsertContext(list, saveFieldSet));
    }

    /**
     * model插入 部分字段插入
     *
     * @param model
     * @return
     */
    default int save(Model<T> model) {
        return this.$saveModel(new ModelInsertContext<>(model));
    }

    /**
     * 动态插入
     *
     * @param insert
     * @return 影响条数
     */
    default int save(BaseInsert<?> insert) {
        return this.$save(new SQLCmdInsertContext<>(insert));
    }

    /**
     * 动态修改
     *
     * @param update 修改update
     * @return 修改的条数
     */
    default int update(BaseUpdate<?> update) {
        return this.getBasicMapper().update(update);
    }

    /**
     * 动态删除
     *
     * @param delete 上下文
     * @return 删除条数
     */
    default int delete(BaseDelete<?> delete) {
        return this.getBasicMapper().delete(delete);
    }

    /**
     * 列表查询
     *
     * @param query 查询query
     * @return 返回结果列表
     */
    default <R> List<R> list(BaseQuery<?> query) {
        return this.list(query, true);
    }


    /**
     * 列表查询
     *
     * @param query    查询query
     * @param optimize 是否优化
     * @return 返回查询列表
     */
    default <R> List<R> list(BaseQuery<?> query, boolean optimize) {
        return this.getBasicMapper().list(query, optimize);
    }

    /**
     * 游标查询
     *
     * @param query 查询query
     * @return 返回游标
     */
    default <R> Cursor<R> cursor(BaseQuery<?> query) {
        return this.cursor(query, true);
    }

    /**
     * 游标查询
     *
     * @param query    查询query
     * @param optimize 是否优化
     * @return 返回游标
     */
    default <R> Cursor<R> cursor(BaseQuery query, boolean optimize) {
        return this.getBasicMapper().cursor(query, optimize);
    }

    /**
     * count查询
     *
     * @param query 上下文
     * @return 返回count 数
     */
    default Integer count(BaseQuery<?> query) {
        return this.count(query, false);
    }

    /**
     * count查询
     *
     * @param query    上下文
     * @param optimize 是否优化
     * @return 返回count 数
     */
    default Integer count(BaseQuery<?> query, boolean optimize) {
        return this.getBasicMapper().count(query, optimize);
    }


    /**
     * 分页查询
     *
     * @param query 查询query
     * @param pager 分页参数
     * @return 分页结果
     */
    default <R, P extends Pager<R>> P paging(BaseQuery query, P pager) {
        return getBasicMapper().paging(query, pager);
    }

    /**
     * 将结果转成map
     *
     * @param mapKey 指定的map的key属性
     * @param query  查询对象
     * @param <K>    map的key
     * @param <V>    map的value
     * @return
     */
    default <K, V, G> Map<K, V> mapWithKey(Getter<G> mapKey, BaseQuery<?> query) {
        return this.mapWithKey(mapKey, query, true);
    }

    /**
     * 将结果转成map
     *
     * @param mapKey   指定的map的key属性
     * @param query    查询对象
     * @param optimize 是否优化sql
     * @param <K>      map的key
     * @param <V>      map的value
     * @return
     */
    default <K, V, G> Map<K, V> mapWithKey(Getter<G> mapKey, BaseQuery<?> query, boolean optimize) {
        return this.mapWithKey(LambdaUtil.getName(mapKey), query, optimize);
    }

    /**
     * 将结果转成map
     *
     * @param mapKey   指定的map的key属性
     * @param query    查询对象
     * @param optimize 是否优化sql
     * @param <K>      map的key
     * @param <V>      map的value
     * @return
     */
    default <K, V> Map<K, V> mapWithKey(String mapKey, BaseQuery<?> query, boolean optimize) {
        return getBasicMapper().mapWithKey(mapKey, query, optimize);
    }
}
