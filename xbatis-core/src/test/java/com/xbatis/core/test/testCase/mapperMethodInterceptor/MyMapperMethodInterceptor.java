package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import cn.xbatis.core.mybatis.mapper.intercept.DefaultMapperMethodInterceptor;

import java.lang.reflect.Method;

public class MyMapperMethodInterceptor extends DefaultMapperMethodInterceptor {

    @Override
    public void before(Object target, Method method, Object[] args) {
        if (method.isAnnotationPresent(MapperLimit.class)) {
            System.out.println(method + "有MapperLimit注解");
        }
    }

    @Override
    public Object after(Object target, Method method, Object[] args, Object result) {
        if (method.isAnnotationPresent(MapperLimit.class)) {
            return 123;
        }
        return super.after(target, method, args, result);
    }
}
