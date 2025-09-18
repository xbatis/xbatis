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

package cn.xbatis.core.mybatis;

import cn.xbatis.core.XbatisGlobalConfig;
import cn.xbatis.core.mybatis.mapper.BasicMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * mybatis 批量工具
 */
public final class MybatisBatchUtil {

    /**
     * 批量插入（表自增的，无法获取主键ID）
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchSave(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list) {
        return batchSave(sqlSessionFactory, mapperType, list, XbatisGlobalConfig.getDefaultBatchSize());
    }

    /**
     * 批量插入（batchSize！=1时，无法获取主键）
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param batchSize         一次批量处理的条数(如需获取主键，请设置为1)
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchSave(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list, int batchSize) {
        return batch(sqlSessionFactory, mapperType, list, batchSize, (session, mapper, data) -> {
            if (mapper instanceof BasicMapper) {
                ((BasicMapper) mapper).save(data);
            } else {
                ((MybatisMapper) mapper).save(data);
            }
        });
    }


    /**
     * 批量更新
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchUpdate(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list) {
        return batchUpdate(sqlSessionFactory, mapperType, list, XbatisGlobalConfig.getDefaultBatchSize());
    }

    /**
     * 批量更新
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param batchSize         一次批量处理的条数
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchUpdate(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list, int batchSize) {
        return batch(sqlSessionFactory, mapperType, list, batchSize, ((session, mapper, data) -> {
            if (mapper instanceof BasicMapper) {
                ((BasicMapper) mapper).update(data);
            } else {
                ((MybatisMapper) mapper).update(data);
            }
        }));
    }

    /**
     * 批量操作
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param batchSize         一次批量处理的条数
     * @param batchFunction     操作方法
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batch(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list, int batchSize, MybatisBatchBiConsumer<SqlSession, M, T> batchFunction) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            M mapper = session.getMapper(mapperType);
            int updateCnt = 0;
            int optTimes = 0;
            for (T entity : list) {
                optTimes++;
                batchFunction.accept(session, mapper, entity);
                if (optTimes == batchSize) {
                    updateCnt += getEffectCnt(session.flushStatements());
                    optTimes = 0;
                }
            }
            if (optTimes != 0) {
                updateCnt += getEffectCnt(session.flushStatements());
            }
            return updateCnt;
        }
    }

    /**
     * 批量原生插入操作,一次多组分隔后的数据
     * 默认是原生100条 然后10个原生插入
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchMultiSave(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list) {
        return batchMultiSave(sqlSessionFactory, mapperType, list, 10, 100);
    }

    /**
     * 批量原生插入操作,一次多组分隔后的数据
     * 默认是原生100条 然后10个原生插入
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param batchSize         一次批量处理的条数
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchMultiSave(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list, int batchSize, int subBatchSize) {
        MybatisBatchBiConsumer<SqlSession, M, List<T>> batchFunction = ((session, mapper, data) -> {
            if (mapper instanceof BasicMapper) {
                ((BasicMapper) mapper).saveBatch(data);
            } else {
                ((MybatisMapper) mapper).saveBatch(data);
            }
        });
        return batchMulti(sqlSessionFactory, mapperType, list, batchSize, subBatchSize, batchFunction);
    }

    /**
     * 批量分批操作,一次多组分隔后的数据
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param mapperType        Mapper 的 class 可以是 MybatisMapper 或 BasicMapper 或子类
     * @param list              数据列表
     * @param batchSize         一次批量处理的条数
     * @param batchFunction     操作方法
     * @param <M>               Mapper 实例
     * @param <T>               数据的类型
     * @return 影响的条数
     */
    public static <M, T> int batchMulti(SqlSessionFactory sqlSessionFactory, Class<M> mapperType, Collection<T> list, int batchSize, int subBatchSize, MybatisBatchBiConsumer<SqlSession, M, List<T>> batchFunction) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            M mapper = session.getMapper(mapperType);
            int updateCnt = 0;
            int optTimes = 0;
            int counter = 0;
            List<T> subList = new ArrayList<>();
            for (T entity : list) {
                subList.add(entity);
                counter++;
                if (counter == subBatchSize) {
                    batchFunction.accept(session, mapper, subList);
                    counter = 0;
                    optTimes++;
                    subList.clear();
                }

                if (optTimes == batchSize) {
                    updateCnt += getEffectCnt(session.flushStatements());
                    optTimes = 0;
                }
            }
            if (counter != 0) {
                optTimes++;
                batchFunction.accept(session, mapper, subList);
            }

            if (optTimes != 0) {
                updateCnt += getEffectCnt(session.flushStatements());
            }
            return updateCnt;
        }
    }


    /**
     * 批量操作
     *
     * @param sqlSessionFactory mybatis SqlSessionFactory 通过spring 注解注入获取
     * @param batchFunction     操作方法
     * @return 影响的条数
     */
    public static int batch(SqlSessionFactory sqlSessionFactory, Function<SqlSession, Integer> batchFunction) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            return batchFunction.apply(session);
        }
    }

    /**
     * 获取批量操作影响的条数
     *
     * @param batchResultList 批量操作结果list
     * @return 影响的条数
     */
    public static int getEffectCnt(List<BatchResult> batchResultList) {
        int updateCnt = 0;
        for (BatchResult batchResult : batchResultList) {
            for (int i : batchResult.getUpdateCounts()) {
                updateCnt += i;
            }
        }
        return updateCnt;
    }

    public interface MybatisBatchBiConsumer<S extends SqlSession, M, T> {
        void accept(S session, M mapper, T data);

        default MybatisBatchBiConsumer<S, M, T> andThen(MybatisBatchBiConsumer<? super S, ? super M, ? super T> after) {
            Objects.requireNonNull(after);
            return (l, r, t) -> {
                accept(l, r, t);
                after.accept(l, r, t);
            };
        }
    }

}
