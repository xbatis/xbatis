package cn.xbatis.core.mybatis.mapper.intercept;


import java.lang.reflect.Method;
import java.util.List;

public class MethodInvocation implements Invocation {

    private final List<MethodInterceptor> list;

    private final Object target;

    private final Method method;

    private final Object[] arguments;

    private final InvocationCall<Object> callable;

    //已经执行Count
    private int proceedCount;

    public MethodInvocation(List<MethodInterceptor> list, Object target, Method method, Object[] arguments, InvocationCall<Object> callable){
        this.list = list;
        this.target = target;
        this.method= method;
        this.arguments = arguments;
        this.callable = callable;
    }

    @Override
    public Object proceed() throws Throwable {
        if(this.proceedCount == list.size() -1){
            return callable.call();
        }
        return list.get(++this.proceedCount).around(this);
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }
}
