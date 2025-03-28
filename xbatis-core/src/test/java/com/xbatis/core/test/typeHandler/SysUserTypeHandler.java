package com.xbatis.core.test.typeHandler;

import com.xbatis.core.test.vo.SysUserHandlerVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SysUserTypeHandler extends BaseTypeHandler<SysUserHandlerVo>   {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SysUserHandlerVo parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public SysUserHandlerVo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        SysUserHandlerVo vo=new SysUserHandlerVo();
        vo.setId(rs.getInt("id"));
        return vo;
    }

    @Override
    public SysUserHandlerVo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SysUserHandlerVo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
