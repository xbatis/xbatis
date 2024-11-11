package cn.mybatis.mp.core.mybatis.mapper.mappers.basicMapper;

import cn.mybatis.mp.core.db.reflect.Tables;
import cn.mybatis.mp.core.mybatis.mapper.context.Pager;
import cn.mybatis.mp.core.mybatis.mapper.mappers.utils.PagingMethodUtil;
import db.sql.api.Getter;
import db.sql.api.impl.cmd.struct.Where;

import java.util.function.Consumer;

public interface PagingBasicMapper extends BaseBasicMapper {

    /**
     * 分页查询
     *
     * @param entityType 实体类
     * @param consumer   where consumer
     * @param pager      分页参数
     * @return 分页结果
     */
    default <T, P extends Pager<T>> P paging(Class<T> entityType, P pager, Consumer<Where> consumer) {
        return this.paging(entityType, pager, consumer, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param entityType   实体类
     * @param consumer     where consumer
     * @param pager        pager
     * @param selectFields select指定列
     * @return
     */
    default <T, P extends Pager<T>> P paging(Class<T> entityType, P pager, Consumer<Where> consumer, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), Tables.get(entityType), pager, consumer, selectFields);
    }

    /**
     * 分页查询
     *
     * @param entityType 实体类
     * @param where      where
     * @param pager      分页参数
     * @return 分页结果
     */
    default <T, P extends Pager<T>> P paging(Class<T> entityType, P pager, Where where) {
        return this.paging(entityType, pager, where, (Getter<T>[]) null);
    }

    /**
     * 分页查询
     *
     * @param entityType   实体类
     * @param pager
     * @param where
     * @param selectFields
     * @param <T>
     * @param <P>
     * @return
     */
    default <T, P extends Pager<T>> P paging(Class<T> entityType, P pager, Where where, Getter<T>... selectFields) {
        return PagingMethodUtil.paging(getBasicMapper(), Tables.get(entityType), pager, where, selectFields);
    }
}
