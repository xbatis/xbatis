package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import cn.xbatis.core.XbatisGlobalConfig;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodInterceptorTestcase extends BaseTest {

    @Test
    public void test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            XbatisGlobalConfig.addMapperMethodInterceptor(new MyMapperMethodInterceptor());
            XbatisGlobalConfig.addMapperMethodInterceptor(new MyMapperMethodInterceptor2());
            Integer cnt;
            cnt = sysUserMapper.nativeCount();
            assertEquals(cnt, 3);
//
            cnt = sysUserMapper.javaCount();
            assertEquals(cnt, 3);

            cnt = sysUserMapper.javaLimitAnnotationCount();
            assertEquals(123, cnt);
        }
    }

    @Test
    public void test2() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            XbatisGlobalConfig.removeMapperMethodInterceptor(MyMapperMethodInterceptor2.class);
            XbatisGlobalConfig.addMapperMethodInterceptor(new MyMapperMethodInterceptor());

            Integer cnt;

            cnt = sysUserMapper.javaLimitAnnotationCount();
            assertEquals(3, cnt);
        }
    }

    @Test
    public void test3() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            XbatisGlobalConfig.removeMapperMethodInterceptor(MyMapperMethodInterceptor.class);
            XbatisGlobalConfig.addMapperMethodInterceptor(new MyMapperMethodInterceptor2());

            Integer cnt;

            cnt = sysUserMapper.javaLimitAnnotationCount();
            assertEquals(123, cnt);
        }
    }
}


