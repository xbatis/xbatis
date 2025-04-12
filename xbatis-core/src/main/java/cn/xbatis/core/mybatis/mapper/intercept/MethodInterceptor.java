package cn.xbatis.core.mybatis.mapper.intercept;

public interface MethodInterceptor {

    /**
     * 拦截器-环绕处理
     *
     * @param invocation 方法反射器
     * @return 原目标执行结果
     */
    Object around(Invocation invocation) throws Throwable;

}
