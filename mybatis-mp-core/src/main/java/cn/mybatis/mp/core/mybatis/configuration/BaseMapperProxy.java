/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.mybatis.configuration;

import cn.mybatis.mp.core.MybatisMpConfig;
import cn.mybatis.mp.core.function.ThreeFunction;
import cn.mybatis.mp.core.mybatis.executor.BasicMapperThreadLocalUtil;
import cn.mybatis.mp.core.mybatis.mapper.BasicMapper;
import cn.mybatis.mp.core.mybatis.mapper.MybatisMapper;
import cn.mybatis.mp.core.mybatis.mapper.context.MapKeySQLCmdQueryContext;
import cn.mybatis.mp.core.sql.executor.Where;
import cn.mybatis.mp.core.util.DbTypeUtil;
import cn.mybatis.mp.db.annotations.Paging;
import cn.mybatis.mp.page.IPager;
import cn.mybatis.mp.page.PagerField;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.executor.DbSelectorCall;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseMapperProxy<T> extends MapperProxy<T> {

    public final static String MAP_WITH_KEY_METHOD_NAME = "$mapWithKey";

    public final static String DB_ADAPT_METHOD_NAME = "dbAdapt";

    public final static String CURRENT_DB_TYPE_METHOD_NAME = "getCurrentDbType";

    public final static String WITH_SQL_SESSION_METHOD_NAME = "withSqlSession";

    protected final SqlSession sqlSession;

    protected final Class<T> mapperInterface;

    private volatile DbType dbType;

    public BaseMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map methodCache) {
        super(sqlSession, mapperInterface, methodCache);
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    private DbType getDbType() {
        if (Objects.isNull(dbType)) {
            dbType = DbTypeUtil.getDbType(sqlSession.getConfiguration());
        }
        return dbType;
    }

    protected boolean setBasicMapperToThreadLocal(Object proxy) {
        if (proxy instanceof BasicMapper) {
            BasicMapperThreadLocalUtil.set(proxy);
            return true;
        } else if (proxy instanceof MybatisMapper) {
            BasicMapperThreadLocalUtil.set((Supplier<BasicMapper>) () -> ((MybatisMapper) proxy).getBasicMapper());
            return true;
        }
        return false;
    }

    private void wrapperParams(Method method, Object[] args) {
        if (Objects.isNull(args) || args.length == 0) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null && arg instanceof Where) {
                Parameter[] parameters = method.getParameters();
                Param param = parameters[i].getAnnotation(Param.class);
                Where where = (Where) arg;
                if (param != null) {
                    where.setMybatisParamName(param.value());
                } else if (args.length > 1) {
                    where.setMybatisParamName("param" + (i + 1));
                }
                where.setDbType(getDbType());
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            return super.invoke(proxy, method, args);
        }

        boolean isSetBasicMapperToThreadLocal = false;
        try {
            isSetBasicMapperToThreadLocal = setBasicMapperToThreadLocal(proxy);
            if (method.getName().equals(DB_ADAPT_METHOD_NAME)) {
                Consumer<Object> consumer = (Consumer<Object>) args[0];
                DbSelectorCall dbSelector = new DbSelectorCall();
                consumer.accept(dbSelector);
                return dbSelector.dbExecute(this.getDbType());
            } else if (method.getName().equals(MAP_WITH_KEY_METHOD_NAME)) {
                this.wrapperParams(method, args);
                return mapWithKey(method, args);
            } else if (method.isAnnotationPresent(Paging.class)) {
                this.wrapperParams(method, args);
                return paging(method, args);
            } else if (method.getName().equals(CURRENT_DB_TYPE_METHOD_NAME)) {
                return this.getDbType();
            } else if (method.getName().equals(WITH_SQL_SESSION_METHOD_NAME)) {
                this.wrapperParams(method, args);
                if (args.length == 1) {
                    Function<SqlSession, ?> function = (Function<SqlSession, ?>) args[0];
                    return function.apply(this.sqlSession);
                }

                String statement;
                if (args.length == 4) {
                    statement = MybatisMpConfig.getSingleMapperClass().getName() + "." + ((Class) args[0]).getSimpleName() + ":" + args[1];
                } else {
                    if (args[0] instanceof String) {
                        statement = (String) args[0];
                        if (statement.startsWith(".")) {
                            statement = MybatisMpConfig.getSingleMapperClass().getName() + statement;
                        }
                    } else {
                        statement = MybatisMpConfig.getSingleMapperClass().getName() + "." + ((Class) args[0]).getSimpleName() + ":" + args[1];
                    }
                }

                if (args.length == 2) {
                    BiFunction<String, SqlSession, ?> function = (BiFunction<String, SqlSession, ?>) args[1];
                    return function.apply(statement, this.sqlSession);
                } else if (args.length == 3) {
                    if (args[0] instanceof String) {
                        ThreeFunction<String, Object, SqlSession, ?> function = (ThreeFunction<String, Object, SqlSession, ?>) args[2];
                        return function.apply(statement, args[1], this.sqlSession);
                    } else {
                        BiFunction<String, SqlSession, ?> function = (BiFunction<String, SqlSession, ?>) args[2];
                        return function.apply(statement, this.sqlSession);
                    }
                } else if (args.length == 4) {
                    ThreeFunction<String, Object, SqlSession, ?> function = (ThreeFunction<String, Object, SqlSession, ?>) args[3];
                    return function.apply(statement, args[2], this.sqlSession);
                } else {
                    throw new RuntimeException("NOT SUPPORTED");
                }
            }
            this.wrapperParams(method, args);
            return super.invoke(proxy, method, args);
        } finally {
            if (isSetBasicMapperToThreadLocal) {
                BasicMapperThreadLocalUtil.clear();
            }
        }

    }

    private <K, V> Map<K, V> mapWithKey(Method method, Object[] args) {
        MapKeySQLCmdQueryContext queryContext = (MapKeySQLCmdQueryContext) args[0];
        String statementId = mapperInterface.getName() + "." + method.getName();
        return sqlSession.selectMap(statementId, queryContext, queryContext.getKey());
    }

    private IPager<?> paging(Method method, Object[] args) {
        ParamNameResolver paramNameResolver = new ParamNameResolver(this.sqlSession.getConfiguration(), method);
        Object params = paramNameResolver.getNamedParams(args);
        String statementId = mapperInterface.getName() + "." + method.getName();
        IPager<?> pager = (IPager) args[0];

        Integer count = null;
        List list;
        if (pager.get(PagerField.IS_EXECUTE_COUNT)) {
            count = sqlSession.selectOne(statementId + "&count", params);
            count = Objects.isNull(count) ? 0 : count;
            if (count == 0) {
                list = new ArrayList<>();
            } else {
                list = sqlSession.selectList(statementId + "&list", params);
            }
        } else {
            list = sqlSession.selectList(statementId + "&list", params);
        }
        pager.set(PagerField.RESULTS, list);
        pager.set(PagerField.TOTAL, count);
        return pager;
    }
}
