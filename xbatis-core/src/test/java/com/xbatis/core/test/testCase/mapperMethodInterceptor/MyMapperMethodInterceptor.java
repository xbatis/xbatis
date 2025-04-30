package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import cn.xbatis.core.mybatis.mapper.intercept.Invocation;
import cn.xbatis.core.mybatis.mapper.intercept.MethodInterceptor;

public class MyMapperMethodInterceptor implements MethodInterceptor {


    @Override
    public Object around(Invocation invocation) throws Throwable {
        try {
            System.out.println(invocation.getMethod());
            if (invocation.getMethod().isAnnotationPresent(MapperLimit.class)) {
                System.out.println(">>1111>>>>>>>start");
            }
            return invocation.proceed();
        } finally {
            System.out.println(">>1111>>>>>>>end");
        }
    }
}
