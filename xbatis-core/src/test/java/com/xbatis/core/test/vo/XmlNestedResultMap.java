package com.xbatis.core.test.vo;

import com.xbatis.core.test.DO.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class XmlNestedResultMap {

    private Integer id;

    private String password;

    private List<SysRole> sysRoleList;
}
