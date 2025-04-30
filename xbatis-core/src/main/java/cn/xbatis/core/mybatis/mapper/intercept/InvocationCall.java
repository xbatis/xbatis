package cn.xbatis.core.mybatis.mapper.intercept;

@FunctionalInterface
public interface InvocationCall<V> {

    V call() throws Throwable;
}
