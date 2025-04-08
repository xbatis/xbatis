package cn.xbatis.core.mybatis.mapper.intercept;

import java.lang.reflect.Method;

public interface MapperMethodInterceptor {

    /**
     * 拦截器-处理前置处理
     *
     * @param target 目标对象
     * @param method 方法
     * @param args   参数
     */
    void before(Object target, Method method, Object[] args);

    /**
     * 拦截器-处理后置处理
     *
     * @param target 目标对象
     * @param method 方法
     * @param args   参数
     * @param result 结果
     * @return 返回result 或者 新的结果
     */
    Object after(Object target, Method method, Object[] args, Object result);

    /**
     * 拦截器-完成后处理
     *
     * @param target 目标对象
     * @param method 方法
     * @param args   参数
     * @param result 结果
     * @return 返回result 或者 新的结果
     */
    Object complete(Object target, Method method, Object[] args, Object result, Throwable e);
}
