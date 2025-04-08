package com.xbatis.core.test.testCase.mapperMethodInterceptor;

import cn.xbatis.core.XbatisGlobalConfig;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperMethodInterceptorTestcase extends BaseTest {

    @Test
    public void test() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);

            XbatisGlobalConfig.addMapperMethodInterceptor(new MyMapperMethodInterceptor());
            Integer cnt = sysUserMapper.nativeCount();
            assertEquals(cnt, 3);

            cnt = sysUserMapper.javaCount();
            assertEquals(cnt, 3);

            cnt = sysUserMapper.javaLimitAnnotationCount();
            assertEquals(cnt, 123);
        }
    }
}
