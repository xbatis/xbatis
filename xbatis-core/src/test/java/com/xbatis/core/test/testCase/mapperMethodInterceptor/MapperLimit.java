package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MapperLimit {
}
