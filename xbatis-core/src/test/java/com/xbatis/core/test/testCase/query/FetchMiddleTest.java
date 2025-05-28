package com.xbatis.core.test.testCase.query;

import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.mapper.SysRoleMapper;
import com.xbatis.core.test.mapper.SysUserMapper;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.vo.SysUserMiddleFetchVO;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FetchMiddleTest extends BaseTest {

    @Test
    public void onRowEvent() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
            SysRoleMapper sysRoleMapper = session.getMapper(SysRoleMapper.class);

            List<SysUserMiddleFetchVO> list = QueryChain.of(sysUserMapper)
                    .returnType(SysUserMiddleFetchVO.class)
                    .list();

            System.out.println(list);

            SysRole sysRole1 = sysRoleMapper.getById(1);
            SysRole sysRole2 = sysRoleMapper.getById(2);

            assertEquals(list.get(0).getSysRoleList().size(), 2);
            assertEquals(list.get(0).getSysRoleList().get(0), sysRole1);
            assertEquals(list.get(0).getSysRoleList().get(1), sysRole2);

            assertEquals(list.get(0).getCnts(), 2);


            assertEquals(list.get(1).getSysRoleList().size(), 1);
            assertEquals(list.get(1).getSysRoleList().get(0), sysRole2);

            assertEquals(list.get(1).getCnts(), 1);

        }
    }
}
