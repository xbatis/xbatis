package com.xbatis.core.test.vo;

import cn.xbatis.db.annotations.TypeHandler;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.typeHandler.SysUserTypeHandler;

@TypeHandler(SysUserTypeHandler.class)
public class SysUserHandlerVo extends SysUser {
}
