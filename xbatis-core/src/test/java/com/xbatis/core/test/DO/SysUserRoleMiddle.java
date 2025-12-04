package com.xbatis.core.test.DO;

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@Table("sys_role_middle")
public class SysUserRoleMiddle {

    @TableId(value = IdAutoType.NONE)
    private Integer userId;

    @TableId(value = IdAutoType.NONE)
    private Integer roleId;
}
