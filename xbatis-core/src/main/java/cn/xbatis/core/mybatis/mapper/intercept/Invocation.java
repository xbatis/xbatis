package cn.xbatis.core.mybatis.mapper.intercept;


import java.lang.reflect.Method;

public interface Invocation {

    /**
     * 执行原生方法
     *
     * @return 返回原方法的执行结果
     * @throws Throwable
     */
    Object proceed() throws Throwable;

    /**
     * 获取目标，一般都Mapper的代理类
     *
     * @return 目标
     */
    Object getTarget();

    /**
     * 获取拦截的方法
     *
     * @return 当前方法
     */
    Method getMethod();

    /**
     * 获取方法的参数
     *
     * @return 当前的参数
     */
    Object[] getArguments();
}
