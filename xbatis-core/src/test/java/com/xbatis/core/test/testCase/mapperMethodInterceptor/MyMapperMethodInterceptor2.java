package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import cn.xbatis.core.mybatis.mapper.intercept.Invocation;
import cn.xbatis.core.mybatis.mapper.intercept.MethodInterceptor;

public class MyMapperMethodInterceptor2 implements MethodInterceptor {


    @Override
    public Object around(Invocation invocation) throws Throwable {
        try {
            if (invocation.getMethod().isAnnotationPresent(MapperLimit.class)) {
                System.out.println(">>2222>>>>>>>start");
                return 123;
            }
            return invocation.proceed();
        } finally {
            System.out.println(">>2222>>>>>>>end");
        }
    }
}
