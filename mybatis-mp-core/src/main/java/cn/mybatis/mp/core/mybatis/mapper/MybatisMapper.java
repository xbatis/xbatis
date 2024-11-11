package cn.mybatis.mp.core.mybatis.mapper;

import cn.mybatis.mp.core.mybatis.mapper.context.Pager;
import cn.mybatis.mp.core.mybatis.mapper.mappers.*;
import cn.mybatis.mp.core.sql.executor.BaseDelete;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.BaseQuery;
import cn.mybatis.mp.core.sql.executor.BaseUpdate;
import db.sql.api.impl.cmd.executor.SelectorCall;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 数据库 Mapper
 * $ 开头的方法一般不建议去使用
 *
 * @param <T>
 */
public interface MybatisMapper<T> extends CRUDMapper, GetMapper<T>, ExistsMapper<T>, CountMapper<T>, ListMapper<T>, CursorMapper<T>,
        PagingMapper<T>, MapWithKeyMapper<T>, SaveMapper<T>, SaveOrUpdateMapper<T>, SaveModelMapper<T>, SaveOrUpdateModelMapper<T>,
        UpdateMapper<T>, UpdateModelMapper<T>, DeleteMapper<T> {

    /**
     * 选择器 不同数据库执行不同的方法
     *
     * @param consumer
     */
    <R> R dbAdapt(Consumer<SelectorCall<R>> consumer);

    @Override
    default <T2> T2 get(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().get(query);
    }

    @Override
    default boolean exists(BaseQuery<? extends BaseQuery, ?> query) {
        return getBasicMapper().exists(query);
    }

    @Override
    default int save(BaseInsert<?> insert) {
        return getBasicMapper().save(insert);
    }

    @Override
    default int update(BaseUpdate<?> update) {
        return getBasicMapper().update(update);
    }

    @Override
    default int delete(BaseDelete<?> delete) {
        return getBasicMapper().delete(delete);
    }

    @Override
    default <T2> List<T2> list(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().list(query);
    }

    @Override
    default <T2> Cursor<T2> cursor(BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().cursor(query);
    }

    @Override
    default Integer count(BaseQuery<? extends BaseQuery, ?> query) {
        return getBasicMapper().count(query);
    }

    @Override
    default <T2, P extends Pager<T2>> P paging(BaseQuery<? extends BaseQuery, T2> query, P pager) {
        return getBasicMapper().paging(query, pager);
    }

    @Override
    default <K, T2> Map<K, T2> mapWithKey(String mapKey, BaseQuery<? extends BaseQuery, T2> query) {
        return getBasicMapper().mapWithKey(mapKey, query);
    }
}