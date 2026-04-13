package com.xbatis.core.test.vo;


import cn.xbatis.db.annotations.Fetch;
import cn.xbatis.db.annotations.ResultEntity;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.DO.SysUser;
import com.xbatis.core.test.DO.SysUserRoleMiddle;
import lombok.Data;

import java.util.List;

@Data
@ResultEntity(SysUser.class)
public class SysUserMiddleMergeFetchVO {

    private Integer id;

    @Fetch(property = "id", middle = SysUserRoleMiddle.class
            , middleSourceProperty = SysUserRoleMiddle.Fields.userId
            , middleTargetProperty = SysUserRoleMiddle.Fields.roleId
            , target = SysRole.class, targetProperty = SysRole.Fields.id
            , targetSelectProperty = SysRole.Fields.id
            , orderBy = "[{" + SysRole.Fields.id + "} asc]",
            mergeGroup = "role"
    )
    private List<Integer> sysRoleIdList;



    @Fetch(property = "id", middle = SysUserRoleMiddle.class
            , middleSourceProperty = SysUserRoleMiddle.Fields.userId
            , middleTargetProperty = SysUserRoleMiddle.Fields.roleId
            , target = SysRole.class, targetProperty = SysRole.Fields.id
            , targetSelectProperty = SysRole.Fields.name
            , orderBy = "[{" + SysRole.Fields.id + "} asc]",
            mergeGroup = "role"
    )
    private List<String> sysRoleNameList;




    @Fetch(property = "id", middle = SysUserRoleMiddle.class
            , middleSourceProperty = SysUserRoleMiddle.Fields.userId
            , middleTargetProperty = SysUserRoleMiddle.Fields.roleId
            , target = SysRole.class, targetProperty = SysRole.Fields.id
            , targetSelectProperty = "[count(1)]"
    )
    private Integer cnts;
}
