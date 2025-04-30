package cn.xbatis.core.mybatis.mapper.context;

public class ExecuteAndSelectPreparedContext<T> extends SelectPreparedContext<T> {

    public ExecuteAndSelectPreparedContext(Class<T> returnType, String sql, Object[] params) {
        super(returnType, sql, params);
    }
}
